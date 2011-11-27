# lib-2367

lib-2367 is a Clojure library for generating classes. At the moment it consists
of two macros that emit gen-class code. The first (`defbean`) creates a class
with bean-style setters that can also implement interfaces. The second
(`export-ns`) creates a class with static methods for every function in a
namespace, effectively "exporting" the namespace so that other languages can
conveniently use it.

## Usage

Add to your `project.clj`:

```clojure
[lib-2367 "0.1.0"]
```

or your `pom.xml`:

```xml
<dependency>
  <groupId>lib-2367</groupId>
  <artifactId>lib-2367</artifactId>
  <version>0.1.0</version>
</dependency>
```

### defbean

```clojure
(ns example
  (:use [lib-2367.core :only [defbean]]))

; Class Tommy is generated using gen-class with a setX and a setY method.
(defbean Tommy
  [x y]
  clojure.lang.IDeref
  ; the field names are made available in the body of the methods. They
  ; are stored internally in an atom.
  (deref [_this] (str "Hello " x " and " y "!")))
```

### export-ns

```clojure
(ns foo.bar.baz-bam
  (:use [lib-2367.export :only [export-ns]]))

(defn funny-string
  [a b]
  (format "Hey that %s is quite a %s!" a b))

(defn pooh-bear
  []
  123)

; Calling this macro at the end of the file generates a class called
; foo.bar.BazBam with static methods funny_string and pooh_bear.
(export-ns)
```

## License

Copyright (C) 2011 Gary Fredericks

Distributed under the Eclipse Public License, the same as Clojure.
