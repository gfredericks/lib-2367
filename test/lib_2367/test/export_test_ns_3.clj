(ns lib-2367.test.export-test-ns-3
  (:use [lib-2367.export :only [export-ns]]))

(def old-cf *compile-files*)
(alter-var-root #'*compile-files* (constantly true))

(defn bar [] "TWELVE")

(export-ns :name foo.bar.ClassName)

(alter-var-root #'*compile-files* (constantly old-cf))
