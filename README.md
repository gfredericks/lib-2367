# lib-2367

lib-2367 is a Clojure library for generating classes. At the moment the only
functionality provided is generating a java class with setters and
implementations of interfaces.

## Usage

Add to your `project.clj`:

```clojure
[lib-2367 "0.0.1"]
```

or your `pom.xml`:

```xml
<dependency>
  <groupId>lib-2367</groupId>
  <artifactId>lib-2367</artifactId>
  <version>0.0.1</version>
</dependency>
```

And use something like this:

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

## License

Copyright (C) 2011 Gary Fredericks

Distributed under the Eclipse Public License, the same as Clojure.
