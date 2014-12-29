(ns clj-codetip.core
  (:require [ring.server.standalone :refer [serve]]
            [clojure.string :as string]
            [clojure.tools.cli :refer [parse-opts]]
            [environ.core :refer [env]]
            [clj-codetip.handler :as handler])
  (:gen-class))


(def cli-options
  [["-u" "--database-uri URI" "Database URI"]
   ["-i" "--host" "Interface to bind to"]
   ["-p" "--port PORT" "Port number"
    :default 3000
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 % 0x10000) "Must be a number between 0 and 65536"]]
   ["-s" "--ssl-port PORT" "SSL port number"
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 % 0x10000) "Must be a number between 0 and 65536"]]
   ["-k" "--keystore PATH" "Keystore to use for SSL certificates"
    :default "keystore.jks"]
   ["-w" "--keystore-password PASSWORD" "Keystore password"]
   ["-h" "--help"]])


(defn usage [summary]
  (->> ["Usage: clj-codetip [options]"
        ""
        "Options:"
        summary]
       (string/join \newline)))


(defn error-msg [errors]
  (str "Errors:\n\n" (string/join \newline errors)))


(defn exit [status msg]
  (println msg)
  (System/exit status))


(defn -main
  [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)
        {:keys [database-uri host port ssl-port keystore keystore-password]} options
        db-spec {:connection-uri database-uri}
        dev? (not (nil? (env :codetip-dev)))
        handler (handler/build-handler db-spec dev?)]
    (cond
     (:help options) (exit 0 (usage summary))
     errors (exit 1 (error-msg errors)))
    (serve handler {:init (partial handler/init db-spec)
                    :open-browser? false
                    :auto-reload? dev?
                    :stacktraces? dev?
                    :host host
                    :port port
                    :ssl-port ssl-port
                    :keystore keystore
                    :key-pass keystore-password})))
