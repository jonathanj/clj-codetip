(ns clj-codetip.database
  (:require [clojure.java.jdbc :as j]
            [clj-time.core :as t]
            [yesql.core :refer [defquery]]
            [clj-time.coerce :refer [to-sql-time from-long]]
            [chime :refer [chime-at]])
  (:import  [java.util UUID]))



(defn random-uuid
  "Generate a random UUID."
  []
  (UUID/randomUUID))


(defn uuid->hex
  "Convert a UUID to its lowercase hex representation."
  [uuid]
  (format "%016x%016x"
          (.getMostSignificantBits uuid)
          (.getLeastSignificantBits uuid)))


(defquery -delete-expired-pastes! "sql/delete_expired_pastes.sql")

(defn delete-expired-pastes!
  "Delete all expired pastes."
  [db-spec now]
  (j/with-db-transaction [tx db-spec]
    (-delete-expired-pastes! {:now (to-sql-time now)} {:connection tx})))


(defn- -schedule-paste-expiry
  "Schedule the expired paste cleanup to run."
  [db-spec expires now]
  (chime-at [expires] (fn [_] (delete-expired-pastes! db-spec now))))


(defquery -unexpired-pastes "sql/unexpired_pastes.sql")

(defn schedule-unexpired-deletes
  "Schedule unexpired paste deletes."
  [db-spec now]
  (j/with-db-transaction [tx db-spec]
    (doseq [p (-unexpired-pastes {:now (to-sql-time now)} {:connection tx})]
      (-schedule-paste-expiry db-spec (-> :expires p from-long) now))))


(defquery -create-paste! "sql/create_paste.sql")

(defn create-paste!
  "Create a new paste."
  [db-spec & {:keys [filename expires content content-type]
              :or {filename nil}
                   expires  nil}]
  (let [id (uuid->hex (random-uuid))]
    (j/with-db-transaction [tx db-spec]
      (-create-paste! {:id           id
                       :filename     (or filename id)
                       :created      (to-sql-time (t/now))
                       :expires      (to-sql-time expires)
                       :content      content
                       :content_type content-type}
                      {:connection tx}))
    (-schedule-paste-expiry db-spec expires (t/now))
    id))


(defquery -paste-by-id "sql/paste_by_id.sql")

(defn paste-by-id
  "Retrieve a paste by its identifier."
  [db-spec id]
  (j/with-db-transaction [tx db-spec]
    (some-> (-paste-by-id {:id id} {:connection tx})
            first
            (update-in [:created] from-long)
            (update-in [:expires] from-long)
            (clojure.set/rename-keys {:content_type :content-type}))))
