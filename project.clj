(defproject clj-codetip "0.1.0"
  :description "A simple pastebin."
  :url "http://github.com/jonathanj/clj-codetip"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :plugins [[lein-ring "0.8.13" :exclusions [org.clojure/clojure]]
            [lein-environ "1.0.0"]
            [joplin.lein "0.2.2"]]
  :source-paths ["src" "joplin"]
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/java.jdbc "0.5.8"]
                 [org.clojure/tools.cli "0.3.5"]
                 [org.clojure/tools.logging "0.3.1"]
                 [liberator "0.14.1"]
                 [compojure "1.5.0"]
                 [hiccup "1.0.5"]
                 [ring "1.4.0"]
                 [ring-server "0.4.0"]
                 [yesql "0.5.3"]
                 [environ "1.0.3"]
                 [joplin.core "0.3.6" :exclusions [clj-time org.clojure/java.classpath]]
                 [joplin.jdbc "0.3.6" :exclusions [clj-time org.clojure/java.classpath]]
                 [joplin.lein "0.2.18"]
                 [jarohen/chime "0.1.9"]
                 [org.xerial/sqlite-jdbc "3.8.11.2"]]
  :main clj-codetip.core
  :aot [clj-codetip.streams.limited-input-stream clj-codetip.core]
  :profiles {:dev {:env  {:codetip-dev     true
                          :codetip-db-spec "jdbc:sqlite:codetip-dev.db"}
                   :ring {:handler clj-codetip.handler/dev-handler
                          :init    clj-codetip.handler/dev-init}}})
