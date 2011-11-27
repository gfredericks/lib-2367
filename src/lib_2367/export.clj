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
  ns will be exported."
  [& {:as opts}]
  (when (and *compile-files* (not (@already-exported (ns-name *ns*))))
    (swap! already-exported conj (ns-name *ns*))
    (let [to-export (determine-exports *ns*),
          transformer (or (:post opts) 'identity),
          class-name (or (:name opts)
                         (-> *ns* ns-name make-class-name))]
      `(do
        ~@(for [name (distinct (map first to-export))]
          `(defn ~(camelize-and-prefix-sym name)
             [& args#]
             (~transformer
               (apply (var-get (var ~name)) args#))))
        (gen-class
          :name ~class-name
          :methods
          ~(vec
            (for [[name arity] to-export]
              ^:static [(camelize-sym name)
                        (vec (repeat arity Object))
                        Object])))))))
