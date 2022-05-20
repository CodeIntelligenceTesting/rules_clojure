(ns example.name-converter-test
  (:require [example.name-converter :refer :all]
   [clojure.test :refer :all]
            [clojure.test.check.clojure-test :refer [defspec]]))

(deftest test-name-conversion
  (is (= :foo-bar (name->keyword "fooBar"))))
