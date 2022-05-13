(ns rules-clojure.library-test
  (:require [rules-clojure.library :as lib])
  (:use clojure.test))

(deftest library
  (is (= (lib/echo "test")) "library test"))
