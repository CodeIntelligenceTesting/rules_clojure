(ns rules-clojure.app-test
    (:require [rules-clojure.app :as app])
    (:use clojure.test))

(deftest app
    (is (= (app/echo "message") "app library message")))
