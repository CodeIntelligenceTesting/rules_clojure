(ns rules-clojure.transitive.app
  (:require [rules-clojure.transitive.greeter.hello :as hello])
  (:gen-class))

(defn greeting [subject message]
  (str (hello/greet subject) " " message))

(defn -main [& args] (println (apply greeting args)))
