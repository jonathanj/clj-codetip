(ns migrators.sql.20141202132511-initialise-paste-table
  (:require [clojure.java.jdbc :as j]
            [yesql.core :refer [defquery]]))

(defquery -create-pastes-table! "sql/create_pastes_table.sql")

(defn up [db-spec]
  (j/with-db-transaction [tx db-spec]
    (-create-pastes-table! {} {:connection tx})))

(defquery -drop-pastes-table! "sql/drop_pastes_table.sql")

(defn down [db-spec]
  (j/with-db-transaction [tx db-spec]
    (-drop-pastes-table! {} {:connection tx})))
