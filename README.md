# lein-expectations

A leiningen plugin to make running tests written using [expectations](https://github.com/jaycfields/expectations) library.

## Usage

In your project.clj:

```clojure
:dev-dependencies [[lein-expectations "0.0.1"]]
```

or install as a plugin:

```bash
$ lein plugin install lein-expectations "0.0.1"
```

To run all your tests:

```bash
$ lein expectations
```

To run specific test namespaces:

```bash
$ lein expectations my.test.namespace1 my.test.namespace2
```

To run test namespaces by regex:

```bash
$ lein expectations my.tests.foo.* my.tests.bar.*
```

## License

Copyright (C) 2011 FIXME

Distributed under the Eclipse Public License, the same as Clojure.
