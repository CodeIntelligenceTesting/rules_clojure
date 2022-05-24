(ns rules-clojure.protocol.foo-impl
  (:require [rules-clojure.protocol.foo :as foo])
  (:use clojure.test))

(defrecord constant-foo [value]
  foo/foo-protocol
  (foo [x] value))

(deftest test-constant-foo
  (is (= 42 (foo/foo (constant-foo. 42)))))
