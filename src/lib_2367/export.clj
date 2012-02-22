(ns lib-2367.export
  (:require [inflections.core :as inf])
  (:require [clojure.string :as string]))

(defn- uncapitalize
  [s]
  (str
    (.toLowerCase (subs s 0 1))
    (subs s 1)))

(defn- camelize-sym
  [sym]
  (-> sym name inf/underscore inf/camelize uncapitalize symbol))

(defn- make-class-name
  [sym]
  (let [parts (string/split (name sym) #"\.")]
    (symbol
      (string/join "."
                   (concat
                     (map inf/underscore (butlast parts))
                     (-> parts last inf/underscore inf/camelize list))))))

(defn- camelize-and-prefix-sym
  [sym]
  (symbol
    (str "-" (camelize-sym sym))))

(defn determine-exports
  [namespace]
  (filter identity
          (for [[name var] (ns-publics namespace),
                :when (-> var meta :arglists),
                arglist (-> var meta :arglists)]
            (if (some #{'&} arglist)
              (println "WARNING -- export-ns ignoring vararg definition of" name "in" namespace)
              [name (count arglist)]))))

(def already-exported (atom #{}))

(defmacro export-ns
  "Macro to be used instead of :gen-class. Should be placed
  at the bottom of the ns file. All public functions in the
  ns will be exported. Available options are:

    :name some.fully.qualified.Symbol
    :post <a function> through which the return value will be passed
    :pre  <a function> through which each argument will be passed

  A function passed in as post will be called with the return value of
  each of the ns's functions before it is returned to the caller."
  [& {:as opts}]
  (when (and *compile-files* (not (@already-exported (ns-name *ns*))))
    (swap! already-exported conj (ns-name *ns*))
    (let [to-export (determine-exports *ns*),
          ;; This is kind of gross, we should instead make it so the
          ;; functions are not emitted instead of substituting identity
          return-transformer (or (:post opts) 'identity),
          arg-transformer (or (:pre opts) 'identity),
          class-name (or (:name opts)
                         (-> *ns* ns-name make-class-name))]
      `(do
        ~@(for [name (distinct (map first to-export))]
          `(defn ~(camelize-and-prefix-sym name)
             [& args#]
             (~return-transformer
               (apply (var-get (var ~name)) (map ~arg-transformer args#)))))
        (gen-class
          :name ~class-name
          :methods
          ~(vec
            (for [[name arity] to-export]
              ^:static [(camelize-sym name)
                        (vec (repeat arity Object))
                        Object])))))))
