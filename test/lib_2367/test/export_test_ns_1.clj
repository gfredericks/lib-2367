(ns lib-2367.test.export-test-ns-1
  (:use [lib-2367.export :only [export-ns]]))

(def old-cf *compile-files*)
(alter-var-root #'*compile-files* (constantly true))

(defn foo
  []
  "FOO")

(defn bar
  [x y]
  (+ x y 12))

(export-ns)

(alter-var-root #'*compile-files* (constantly old-cf))
