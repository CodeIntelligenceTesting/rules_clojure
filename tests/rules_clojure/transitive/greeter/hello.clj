(ns rules-clojure.transitive.greeter.hello
    (:import (rules_clojure.transitive.greeter HelloJava)))

(defn greet [subject]
      (.greet (HelloJava.) subject))
