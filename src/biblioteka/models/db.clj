(ns biblioteka.models.db
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [hiccup.core :refer :all]
            [hiccup.bootstrap.page :refer :all]
            [hiccup.bootstrap.element :as hbe]
            [hiccup.page :refer [doctype include-css]]
            [hiccup.form :as f]
            [hiccup.table :as t]
            [hiccup.element :refer [link-to]]
            [ring.util.response :refer [redirect]]
            [biblioteka.views.layout :as layout]
            [biblioteka.views.menu :as menu])
  (:require [clojure.java.jdbc :as j]))


(def db-map {:classname   "com.mysql.jdbc.Driver"
             :subprotocol "mysql"
             :subname     "//localhost:3306/bibliteka"
             :user        "root"
             :password    ""})

(defn select-books []
  (try
    (into [] (j/with-db-transaction [t-con db-map]
                                    (j/query db-map ["SELECT * FROM publikacija order by godinaizdanja asc"]
                                             )))
    (catch Exception e
      (throw (Exception. e))))
  )

(defn insert-book [name typepublication year nameauthor numberofexample ]

  (j/with-db-transaction [t-con db-map]
                         (try
                           (j/db-unset-rollback-only! t-con)
                           (j/insert! t-con
                                      :publikacija {:nazivpublikacije name :tippublikacije typepublication :godinaizdanja year
                                                      :imeautora nameauthor :brojprimeraka numberofexample })
                           (catch Exception e
                             (j/db-set-rollback-only! t-con)
                             (throw (Exception. e))
                             ))))


(defn select-book-by-name [namebook]
  (try
    (j/with-db-transaction [t-con db-map]
                           (j/query db-map ["SELECT * FROM publikacija WHERE nazivpublikacije=?" namebook]
                                    ))
    (catch Exception e
      (throw (Exception. "Dogodila se greska!"))))
  )

(defn select-book-by-id [id]
  (try
    (into [] (j/with-db-transaction [t-con db-map]
                                    (j/query db-map ["SELECT * FROM publikacija WHERE publikacijaid=?" id]
                                             )))

    (catch Exception e
      (throw (Exception. "Dogodila se greska!"))))
  )

(defn delete-book [id]
  (j/with-db-transaction [t-con db-map]
                         (try
                           (j/db-unset-rollback-only! t-con)
                           (j/execute! t-con
                                       ["DELETE FROM publikacija WHERE publikacijaid=?" id])
                           (catch Exception e
                             (j/db-set-rollback-only! t-con)
                             (throw (Exception. e))
                             )))
  )

(defn delete-book-by-name [namebook]
  (j/with-db-transaction [t-con db-map]
                         (try
                           (j/db-unset-rollback-only! t-con)
                           (j/execute! t-con
                                       ["DELETE FROM publikacija WHERE nazivpublikacije=?" namebook])
                           (catch Exception e
                             (j/db-set-rollback-only! t-con)
                             (throw (Exception. e))
                             )))
  )

(defn update-book [bookid name typepublication year nameauthor numberofexample ]
  (j/with-db-transaction [t-con db-map]
                         (try
                           (j/db-unset-rollback-only! t-con)
                           (j/execute! t-con
                                       ["UPDATE publikacija SET nazivpublikacije=?, tippublikacije=?, godinaizdanja=?, imeautora=?, brojprimeraka=?
                                      WHERE publikacijaid=? " name typepublication year nameauthor numberofexample bookid]
                                       )
                           (catch Exception e
                             (j/db-set-rollback-only! t-con)
                             (throw (Exception. e))
                             )))
  )


