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
                 [org.clojure/java.jdbc "0.3.6"]
                 [org.clojure/tools.cli "0.3.1"]
                 [org.clojure/tools.logging "0.3.1"]
                 [liberator "0.12.2"]
                 [compojure "1.2.1"]
                 [hiccup "1.0.5"]
                 [ring "1.3.2"]
                 [ring-server "0.3.1"]
                 [yesql "0.5.0-rc1"]
                 [environ "1.0.0"]
                 [joplin.core "0.2.2" :exclusions [clj-time org.clojure/java.classpath]]
                 [joplin.jdbc "0.2.2" :exclusions [clj-time org.clojure/java.classpath]]
                 [joplin.lein "0.2.2"]
                 [jarohen/chime "0.1.6"]
                 [org.xerial/sqlite-jdbc "3.8.7"]]
  :main clj-codetip.core
  :profiles {:uberjar {:aot :all}
             :dev     {:env  {:codetip-dev true}}}
  :joplin {:migrators {:sql-mig "joplin/migrators/sql"}
           :databases {:sql-dev  {:type :jdbc :url "jdbc:sqlite:codetip-dev.db"}
                       :sql-prod {:type :jdbc :url "jdbc:sqlit:ecodetip-prod.db"}}
           :environments {:dev  [{:db       :sql-dev
                                  :migrator :sql-mig}]
                          :prod [{:db       :sql-prod
                                  :migrator :sql-mig}]}})
