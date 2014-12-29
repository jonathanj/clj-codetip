(ns clj-codetip.handler
  (:require [liberator.core :refer [resource]]
            [liberator.dev :refer [wrap-trace]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.server.standalone :refer [serve]]
            [clojure.java.jdbc :refer [with-db-connection]]
            [clojure.tools.logging :as log]
            [compojure.core :refer [defroutes ANY GET POST]]
            [compojure.route :as route]
            [clj-time.core :as t]
            [clj-codetip.database :as db]
            [clj-codetip.view :as view]))


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
          (view/syntax-mime-modes content-type)))


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
    (db/schedule-unexpired-deletes db-spec now)
    (db/delete-expired-pastes! db-spec now)))


(defn log-request [handler]
  (fn [req]
    (log/info (apply format "%S %s" ((juxt :request-method :uri) req)))
    (log/debug req)
    (handler req)))


(defn wrap-sql-connection [handler db-spec]
  (fn [request]
    (with-db-connection [conn db-spec]
      (handler (assoc request :db-conn conn)))))


(defn build-handler
  "Build a ring handler."
  [db-spec dev?]
  (cond-> app
          dev? (wrap-trace :header :ui)
          true (-> wrap-params
                   (wrap-sql-connection db-spec)
                   log-request)))
