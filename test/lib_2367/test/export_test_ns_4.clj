(ns lib-2367.test.export-test-ns-4
  (:use [lib-2367.export :only [export-ns]]))

(def old-cf *compile-files*)
(alter-var-root #'*compile-files* (constantly true))

(defn bar [x] (* 2 x))

(export-ns :pre inc)

(alter-var-root #'*compile-files* (constantly old-cf))
