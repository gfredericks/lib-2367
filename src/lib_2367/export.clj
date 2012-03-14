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

(defn maybe-wrap
  [wrapped-thing wrapper]
  (if wrapper (list wrapper wrapped-thing) wrapped-thing))

(defmacro export-ns
  "Macro to be used instead of :gen-class. Should be placed
  at the bottom of the ns file. All public functions in the
  ns will be exported. Available options are:

    :name some.fully.qualified.Symbol
    :post <a function> through which the return value will be passed
    :pre  <a function> through which each argument will be passed
    :wrap <a function> if supplied, will be called with the raw clojure
                       function and should return a function to be used
                       in its place

  A function passed in as post will be called with the return value of
  each of the ns's functions before it is returned to the caller."
  [& {wrapper :wrap, return-transformer :post, arg-transformer :pre class-name :name}]
  (when (and *compile-files* (not (@already-exported (ns-name *ns*))))
    (swap! already-exported conj (ns-name *ns*))
    (let [to-export (determine-exports *ns*),
          class-name (or class-name
                         (-> *ns* ns-name make-class-name))
          ;; Can't use args# since we're nesting backquotes
          arg-sym (gensym "__args")]
      `(do
        ~@(for [name (distinct (map first to-export))]
          `(defn ~(camelize-and-prefix-sym name)
             [& ~arg-sym]
             ~(maybe-wrap
               `(apply ~(maybe-wrap `(var-get (var ~name)) wrapper)
                       ~(if arg-transformer
                          `(map ~arg-transformer ~arg-sym)
                          arg-sym))
               return-transformer)))
        (gen-class
          :name ~class-name
          :methods
          ~(vec
            (for [[name arity] to-export]
              ^:static [(camelize-sym name)
                        (vec (repeat arity Object))
                        Object])))))))
