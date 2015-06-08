(ns clj-codetip.handler
  (:require [liberator.core :refer [resource]]
            [liberator.dev :refer [wrap-trace]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]
            [ring.middleware.multipart-params.temp-file :refer [temp-file-store]]
            [ring.server.standalone :refer [serve]]
            [clojure.java.jdbc :refer [with-db-connection]]
            [clojure.tools.logging :as log]
            [compojure.core :refer [defroutes ANY GET POST]]
            [compojure.route :as route]
            [environ.core :refer [env]]
            [joplin.core :as joplin]
            [joplin.jdbc.database]
            [clj-time.core :as t]
            [clj-codetip.database :as db]
            [clj-codetip.view :as view]
            [clj-codetip.streams :refer [limited-input-stream]]))


(defn- expiry-name->date
  "Convert an expiration name into an absolute date."
  [expiry now]
  (t/plus now
          (condp = expiry
            "hour" (t/hours 1)
            "day"  (t/days 1)
            "week" (t/weeks 1)
            (t/hours 1))))


(defn- paste-title
  "Determine a suitable title for a paste."
  [{:keys [filename content-type]}]
  (format "Codetip \u2013 %s (%s)"
          filename
          (view/syntax-mime-modes content-type "Unknown")))


(defroutes app
  (route/resources "/static")

  (GET  "/:id" [id]
        (resource
         :available-media-types ["text/html"]

         :service-available?
         (fn [{{conn :db-conn} :request}]
           {::paste (db/paste-by-id conn id)})

         :exists?
         (fn [{paste ::paste}]
           (not (nil? paste)))

         :handle-ok
         (fn [{paste ::paste}]
           (view/application (paste-title paste)
                             (view/paste paste)))))

  (ANY  "/" []
        (resource
         :allowed-methods [:get :post]
         :available-media-types ["text/html"]

         :handle-ok
         (fn [ctx]
           (view/application "Codetip" (view/new-paste)))

         :post!
         (fn [{{conn :db-conn
                {content "content"
                 content-type "content-type"
                 expires "expires"} :params} :request :as request}]
           {::id (db/create-paste! conn
                                   :expires (expiry-name->date expires (t/now))
                                   :content content
                                   :content-type content-type)})

         :post-redirect?
         (fn [{id ::id}]
           {:location (format "/%s" id)}))))


(defn init
  "Application startup.

   Schedule unexpired paste deletes and delete expired pastes."
  [db-spec]
  (let [now (t/now)]
    (joplin/migrate-db
     {:db {:type :jdbc
           :url (:connection-uri db-spec)}
      :migrator "migrators/sql"})
    (db/schedule-unexpired-deletes db-spec now)
    (db/delete-expired-pastes! db-spec now)))


(defn log-request [handler]
  "Log request information."
  (fn [req]
    (log/info (apply format "%S %s" ((juxt :request-method :uri) req)))
    (log/info req)
    (handler req)))


(defn wrap-sql-connection [handler db-spec]
  "Wrap a request with database connection information."
  (fn [request]
    (with-db-connection [conn db-spec]
      (handler (assoc request :db-conn conn)))))


(defn limited-store [store max-length]
  "Replace the `:stream` value with a stream that limits the number of bytes
  written to it."
  (fn [item]
    (store (update-in item [:stream] limited-input-stream max-length))))


(def max-upload-size (* 1024 1024 4))


(defn build-handler
  "Build a ring handler."
  [db-spec dev?]
  (cond-> app
          dev? (wrap-trace :header :ui)
          true (-> log-request
                   ;wrap-params
                   (wrap-multipart-params
                    {:store (-> (temp-file-store)
                                (limited-store max-upload-size))})
                   (wrap-sql-connection db-spec))))

(def db-spec (env :codetip-db-spec))
(def dev-handler (build-handler db-spec (env :codetip-dev)))
(def dev-init (partial init db-spec))
