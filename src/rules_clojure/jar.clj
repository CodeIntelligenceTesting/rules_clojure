(ns rules-clojure.jar
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [rules-clojure.fs :as fs])
  (:import [java.io BufferedOutputStream FileOutputStream File]
           [java.util.jar Manifest JarEntry JarFile JarOutputStream]
           [java.nio.file Files Path LinkOption]
           [java.nio.file.attribute FileTime]
           java.time.Instant))

(def manifest
  (let [m (Manifest.)]
    (doto (.getMainAttributes m)
      (.putValue "Manifest-Version" "1.0"))
    m))

(defn put-next-entry! [^JarOutputStream target ^String name last-modified-time]
  ;; set last modified time. When both the .class and .clj are
  ;; present, Clojure loads the one with the newer file modification
  ;; time. This completely breaks reproducible builds because we can't
  ;; set the modified-time to 0 on .class files. Setting to zero means
  ;; if anything on the classpath includes the .clj version, the .clj
  ;; will be loaded because its last-modified timestamp will be
  ;; non-zero
  (.putNextEntry target
                 (doto (JarEntry. name)
                   (.setLastModifiedTime last-modified-time))))

(defn create-jar [{:keys [src-dir classes-dir output-jar resources]}]
  (let [temp (File/createTempFile (fs/filename output-jar) "jar")]

    (with-open [jar-os (-> temp FileOutputStream. BufferedOutputStream. JarOutputStream.)]
      (put-next-entry! jar-os JarFile/MANIFEST_NAME (FileTime/from (Instant/now)))
      (.write ^Manifest manifest jar-os)
      (.closeEntry jar-os)
      (doseq [r resources
              :let [^Path full-path (fs/->path src-dir r)
                    file (.toFile full-path)
                    name (str (fs/path-relative-to src-dir full-path))]]
        (assert (fs/exists? full-path) (str full-path))
        (assert (.isFile file))
        (put-next-entry! jar-os name (Files/getLastModifiedTime full-path (into-array LinkOption [])))
        (io/copy file jar-os)
        (.closeEntry jar-os))
      (doseq [^Path path (->> classes-dir fs/ls-r)
              :let [file (.toFile path)]
              :when (.isFile file)
              :let [name (str (fs/path-relative-to classes-dir path))]]
        (put-next-entry! jar-os name (Files/getLastModifiedTime path (into-array LinkOption [])))
        (io/copy file jar-os)
        (.closeEntry jar-os)))
    (fs/mv (.toPath temp) output-jar)))

(defn create-jar-json [json-str]
  (let [{:keys [src_dir resources classes_dir output_jar]} (json/read-str json-str :key-fn keyword)
        _ (assert classes_dir)
        _ (when (seq resources) (assert src_dir))
        classes-dir (fs/->path classes_dir)
        resources (map fs/->path resources)
        output-jar (fs/->path output_jar)]
    (str
     (create-jar (merge
                  {:classes-dir classes-dir
                   :resources resources
                   :output-jar output-jar}
                  (when src_dir
                    {:src-dir (fs/->path src_dir)}))))))
