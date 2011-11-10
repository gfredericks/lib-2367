# lib-2367

lib-2367 is a Clojure library for generating classes. At the moment
its only use case is when you need a Java class with setters.

## Usage

Add to your `project.clj`:

```clojure
[lib-2367 "0.0.1-SNAPSHOT"]
```

or your `pom.xml`:

```xml
<dependency>
  <groupId>lib-2367</groupId>
  <artifactId>lib-2367</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
```

And use something like this:

```clojure
(ns example
  (:use [lib-2367.core :only [defbean]]))

; Class Tommy is generated using gen-class with a setX and a setY method.
(defbean Tommy
  [x y]
  Runnable
  (run [_this] (println "Hey!" x y)))
```

## License

Copyright (C) 2011 Gary Fredericks

Distributed under the Eclipse Public License, the same as Clojure.
