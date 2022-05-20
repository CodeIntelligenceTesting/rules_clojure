(ns example.name-converter
  (:require [camel-snake-kebab.core :as csk]))

(defn name->keyword
  [name]
  (csk/->kebab-case-keyword name))