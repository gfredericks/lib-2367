(ns lib-2367.core
  (:use [lib-2367.hash :only [sha-1]])
  (:require [inflections.core :as inf]))

(def ^:private underscore-sym
  (comp symbol inf/underscore name))

(defn capitalize-first
  "Apparently inflections' capitalize function lowercases
  the rest of the string."
  [s]
  (str
    (.toUpperCase (subs s 0 1))
    (subs s 1)))

(defn- generate-prefix
  "Given a ns-name and a class name, deterministically generates
  a decently unique string to use as a method prefix."
  [ns-sym class-sym]
  (str "_"
       (subs (sha-1 (str ns-sym "." class-sym)) 0 16)
       "_"))

(defmacro defbean
  [class-name
   field-names
   & interface-specs]
  (let [sym-base (generate-prefix (ns-name *ns*) class-name),
        prefix-sym #(->> % name (str sym-base) symbol),
        setter-name
          (fn [field-name]
            (->> field-name name capitalize-first (str sym-base "set") symbol)),
        setters
          (for [field-name field-names]
            `(defn ~(setter-name field-name)
              [this# v#]
              (swap!
                (. this# ~'state)
                assoc
                '~field-name
                v#))),
        interface-methods
          (for [meth interface-specs, :when (sequential? meth)]
            (let [[meth-name arg-list & body] meth]
              `(defn ~(prefix-sym meth-name)
                 ~arg-list
                 ; Create locals for all the field-names
                 (let [{:syms [~@field-names]} @(. ~(first arg-list) ~'state)]
                   ~@body))))]
    `(do
       ~@setters
       ~@interface-methods
       (defn ~(prefix-sym 'init)
         []
         [[] (atom {})])
       (gen-class
         :name ~(str (name (underscore-sym (ns-name *ns*))) "." class-name),
         :prefix ~sym-base,
         :state ~'state,
         :init ~'init,
         :implements [~@(filter symbol? interface-specs)],
         :methods
           [~@(for [field-name field-names]
                [(->> field-name name capitalize-first (str "set") symbol)
                 [Object]
                 Object])]))))

