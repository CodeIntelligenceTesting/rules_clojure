(ns example.app)

(require 'example.name-converter)


(def foo example.name-converter/body)

(println foo)