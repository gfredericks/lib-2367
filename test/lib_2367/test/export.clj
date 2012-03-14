(ns lib-2367.test.export
  (:require (lib-2367.test export-test-ns-1
                           export-test-ns-2
                           export-test-ns-3
                           export-test-ns-4
                           export-test-ns-5))
  (:use clojure.test))

(import 'lib_2367.test.ExportTestNs1)

(deftest export-test
  (is (= "FOO" (ExportTestNs1/foo)))
  (is (= 25 (ExportTestNs1/bar 6 7))))


(import 'lib_2367.test.ExportTestNs2)

(deftest post-test
  (is (= "36" (ExportTestNs2/bar 12 12)))
  (is (= "30" (ExportTestNs2/bar 2 3 5))))

(import 'foo.bar.ClassName)

(deftest class-name-test
  (is (= "TWELVE" (ClassName/bar))))

(import 'lib_2367.test.ExportTestNs4)

(deftest pre-test
  (is (= 16 (ExportTestNs4/bar 7))))

(import 'lib_2367.test.ExportTestNs5)

(deftest wrap-test
  (is (= {:called-with [2 4]} (ExportTestNs5/beezles 2 4))))