# lein-cascade

A Leiningen plugin to execute cascading task dependencies. This is vaguely
similar to <a href="http://en.wikipedia.org/wiki/Make_(software)">Makefiles</a>
and [Ant](http://ant.apache.org/) target dependencies.

## Installation

You must have Leiningen 2.0 or higher to use this plugin.

Use this for user-level plugins:

Put `[lein-cascade "0.1.2"]` into the `:plugins` vector of your `:user` profile.

Use this for project-level plugins:

Put `[lein-cascade "0.1.2"]` into the `:plugins` vector of your project.clj.

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

### Cascade name types

String cascade names are public, whereas keyword cascade names are internal.
Symbols are special. See below:

- _Symbol_ - Environment variables and command-line args
- _String_ - Public cascade targets visible from command-line
- _Keyword_ - Internal cascade targets not visible from command-line


### Symbols

Symbols have a special meaning. They are looked up as environment variables.
For example: `["with-profile" "foo" "test" TESTNS]` expects `TESTNS` to be an
environment variable, which must be present at runtime. If no such environment
variable exists, an error message would be shown.

The special symbols `%`, `%<integer>` and `%&` are substituted by the command
line arguments. For example in `lein cascade foo bar baz`, `%` and `%1` are
`bar` and `%2` is `baz`.


## License

Copyright © 2013-2014 Shantanu Kumar

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
