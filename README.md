# lein-cascade

A Leiningen plugin to execute cascading task dependencies. This is vaguely
similar to <a href="http://en.wikipedia.org/wiki/Make_(software)">Makefiles</a>
and [Ant](http://ant.apache.org/) target dependencies.

## Installation

Use this for user-level plugins:

Put `[lein-cascade "0.1.0"]` into the `:plugins` vector of your
`:user` profile, or if you are on Leiningen 1.x do `lein plugin install
lein-cascade 0.1.0`.

Use this for project-level plugins:

Put `[lein-cascade "0.1.0"]` into the `:plugins` vector of your project.clj.

## Usage

Add a `:cascade` key in your `project.clj` - example below:

```clojure
;; Inner vectors contain task name and args if any, non-vector is a dependency
:cascade {"foo"  [["clean"]]
          "bar"  ["foo"      ; triggers "foo"
                  ["javac"]]
          "baz"  ["bar"      ; triggers "bar"
                  ["test"]]
          "quux" ["bar"      ; triggers "bar"
                  ["doc"]    ; codox plugin example
                  ["uberjar"]]}
```

You can execute a cascade as follows:

```bash
$ lein cascade quux
```

## License

Copyright © 2013 Shantanu Kumar

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
