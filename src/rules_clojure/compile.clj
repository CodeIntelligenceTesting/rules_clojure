(ns rules-clojure.compile
  (:require [clojure.string]))

(defn compile-path-files []
  (-> *compile-path*
      (clojure.java.io/file)
      file-seq
      (rest)))

(defn aot-class-name [ns]
  (str (.substring (#'clojure.core/root-resource ns) 1) "__init.class"))

(defn src-resource-name [ns]
  (.substring (#'clojure.core/root-resource ns) 1))

(defn src-resource [ns]
  (->> [".clj" ".cljc"]
       (map (fn [ext]
              (let [src-path (str (src-resource-name ns) ext)
                    src-resource (clojure.java.io/resource src-path)]
                (when src-resource
                  [src-path src-resource]))))
       (filter identity)
       (first)))

(defn unconditional-compile
  "the clojure compiler works by binding *compile-files* true and then
  calling `load`. `load` looks for both the source file and .class. If
  the .class file is present it is loaded as a normal java class. If
  the src file is present , the compiler runs, and .class files are
  produced as a side effect of the load. If both are present, the
  newer one is loaded.

  If the .class file is loaded, the compiler will not run and no
  .class files will be produced. Work around that by bypassing RT/load
  and calling Compiler/compile directly"
  [ns]
  (let [[src-path src-resource] (src-resource ns)]
    (assert src-resource)
    (with-open [rdr (clojure.java.io/reader src-resource)]
      (binding [*compile-files* true]
        (clojure.lang.Compiler/compile rdr src-path (-> src-path (clojure.string/split #"/") last))))))

(defn non-transitive-compile [dep-nses ns]
  {:pre [(every? symbol? dep-nses)
         (symbol? ns)
         (not (contains? (set dep-nses) ns))]}

  (when (seq dep-nses)
    (apply require dep-nses))

  (clojure.java.io/resource (aot-class-name ns))
  (let [loaded (loaded-libs)]
    (try
      (when (contains? loaded ns)
        (throw (ex-info (print-str "ns " ns " is already loaded") {:loaded loaded})))
      (unconditional-compile ns)
      (assert (seq (compile-path-files)) (print-str "no classfiles generated for" ns *compile-path* "loaded:" loaded))
      (catch Throwable t
        (throw (ex-info (print-str "while compiling" ns) {:loaded loaded} t))))))
