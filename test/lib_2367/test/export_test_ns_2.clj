(ns lib-2367.test.export-test-ns-2
  (:use [lib-2367.export :only [export-ns]]))

(def old-cf *compile-files*)
(alter-var-root #'*compile-files* (constantly true))

(defn bar
  ([x y]
    (+ x y 12))
  ([a b c] (* a b c)))

(export-ns :post str)

(alter-var-root #'*compile-files* (constantly old-cf))
