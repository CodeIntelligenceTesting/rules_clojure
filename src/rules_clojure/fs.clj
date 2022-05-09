(ns rules-clojure.fs
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str])
  (:import java.io.File
           [java.nio.file CopyOption Files FileSystem FileSystems Path Paths StandardCopyOption]))

(defn path? [x]
  (instance? Path x))

(s/def ::path path?)

(defn file? [x]
  (instance? File x))

(defn absolute? [^Path path]
  (.isAbsolute path))
(s/fdef absolute? :args (s/cat :p path?) :ret boolean?)

(s/def ::absolute-path (s/and path? absolute?))

(defn ->path [& dirs]
  (let [[d & dr] dirs
        d (if (string? d)
            (Paths/get d (into-array String []))
            d)]
    (assert d (str "path does not exist:" d))
    (reduce (fn [^Path p dir] (.resolve p dir)) d (rest dirs))))
(s/fdef ->path :args (s/cat :dirs (s/* (s/alt :s string? :p path?))) :ret path?)

(defn file->path [^File f]
  (.toPath f))

(defn path->file [^Path p]
  (.toFile p))

(defn absolute ^Path [^Path path]
  (.toAbsolutePath path))
(s/fdef absolute :args (s/cat :p path?) :ret path?)

(defn path-relative-to
  "Return the path to b, relative to a"
  [^Path a ^Path b]
  {:pre []}
  (.relativize (absolute a) (absolute b)))
(s/fdef relative-to :args (s/cat :a path? :b path?) :ret path?)

(defn directory? [^File file]
  (.isDirectory file))
(s/fdef directory? :args (s/cat :f file?) :ret boolean?)

(defn create-directories [path]
  (Files/createDirectories path (into-array java.nio.file.attribute.FileAttribute [])))

(defn exists? [path]
  (Files/exists path (into-array java.nio.file.LinkOption [])))

(defn filename [path]
  (-> path
      .getFileName
      str))
(s/fdef filename :args (s/cat :p path?) :ret string?)

(defn dirname [path]
  (.getParent path))

(defn extension [path]
  (-> path
      filename
      (str/split #"\.")
      rest
      last))
(s/fdef extension :args (s/cat :p path?) :ret string?)

(defn basename [path]
  (-> path
      filename
      (str/split #"\.")
      first))

(defn ls [^Path dir]
  (-> dir
      .toFile
      .listFiles
      (->>
       (map (fn [^File f]
              (.toPath f))))))
(s/fdef ls :args (s/cat :d path?) :ret (s/coll-of path?))

(defn ls-r
  "recursive list"
  [dir]
  (->> dir
       ls
       (mapcat (fn [path]
                 (if (-> path .toFile directory?)
                   (concat [path] (ls-r path))
                   [path])))))
(s/fdef ls-r :args (s/cat :d path?) :ret (s/coll-of path?))

(defn jar? [path]
  (= "jar" (extension path)))
(s/fdef jar? :args (s/cat :f path?) :ret boolean?)


(defn mv [src dest]
  (Files/move src dest (into-array CopyOption [StandardCopyOption/ATOMIC_MOVE])))
(s/fdef mv :args (s/cat :s path? :d path?))

(defn rm-rf [^Path dir]
  (while (seq (ls dir))
    (doseq [p (ls dir)
            :let [f (path->file p)]]
      (if (directory? f)
        (do
          (rm-rf p)
          (.delete f))
        (.delete f))))
  (-> dir path->file .delete))

(defn clean-directory [path]
  (rm-rf path)
  (create-directories path))
