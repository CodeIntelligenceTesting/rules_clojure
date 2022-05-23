(ns rules-clojure.compile
  (:require [clojure.data.json :as json]))

(defn compile-from-json [json-str]
  (let [{:keys [aot_nses classes_dir]} (json/read-str json-str :key-fn keyword)
        aot-nses (map symbol aot_nses)]
    (binding [*compile-path* classes_dir]
      (doseq [ns aot-nses]
        (compile ns)))))
