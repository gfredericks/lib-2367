(ns lib-2367.test.export-test-ns-5
  (:use [lib-2367.export :only [export-ns]]))

(def old-cf *compile-files*)
(alter-var-root #'*compile-files* (constantly true))

(defn beezles [x y] (* 2 x))

(export-ns :wrap (fn [f] (fn [& args] {:called-with args})))

(alter-var-root #'*compile-files* (constantly old-cf))
