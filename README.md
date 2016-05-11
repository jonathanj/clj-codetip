# clj-codetip – A Clojure Ring pastebin

Codetip is a simple pastebin.

## Installation

    # Clone repo.
    $ git clone http://github.com/jonathanj/clj-codetip

    # Build standalone jar.
    $ lein uberjar

## Usage

    $ java -jar clj-codetip-VERSION-standalone.jar --database-uri=JDBC_URI [args]

See `--help` for more information.

## Options

* `--database-uri`: JDBC database URI, currently it should be of the form `jdbc:sqlite:filename`.
* `--port`: Port to listen on.
* `--host`: Interface to bind to.

See `--help` for more information.

### Bugs

Report bugs to <http://github.com/jonathanj/clj-codetip/issues>.
