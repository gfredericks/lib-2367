(ns lib-2367.test.core
  (:use clojure.test)
  (:use lib-2367.core))

(def old-cf *compile-files*)
(alter-var-root #'*compile-files* (constantly true))

(defbean Tommy
  [x y]
  java.util.concurrent.Callable
  (call [_] (str x y)))

(defbean Jimmy
  [whoDoneIt]
  java.util.concurrent.Callable
  (call [_] (inc whoDoneIt)))

(alter-var-root #'*compile-files* (constantly old-cf))

(import 'lib_2367.test.core.Tommy)
(import 'lib_2367.test.core.Jimmy)

(deftest tommy-test
  (let [t (new Tommy)]
    (.setX t 12)
    (.setY t "foo")
    (is (= "12foo" (.call t)))))

(deftest inflected-test
  (let [j (new Jimmy)]
    (.setWhoDoneIt j 15)
    (is (= 16 (.call j)))))
