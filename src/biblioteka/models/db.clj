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

(defn select-authors []
  (try
    (into [] (j/with-db-transaction [t-con db-map]
                                    (j/query db-map ["SELECT * FROM autor order by datumrodjenja asc"]
                                             )))
    (catch Exception e
      (throw (Exception. e))))
  )

(defn select-authors-order-surname []
  (try
    (into [] (j/with-db-transaction [t-con db-map]
                                    (j/query db-map ["SELECT * FROM autor order by prezimeautora asc"]
                                             )))
    (catch Exception e
      (throw (Exception. e))))
  )

(defn insert-author [name surname dateofbirth country mainbook]

  (j/with-db-transaction [t-con db-map]
                         (try
                           (j/db-unset-rollback-only! t-con)
                           (j/insert! t-con
                                      :autor {:imeautora name :prezimeautora surname :datumrodjenja dateofbirth
                                                      :zemljaporekla country :najznacajnijedelo mainbook})
                           (catch Exception e
                             (j/db-set-rollback-only! t-con)
                             (throw (Exception. e))
                             ))))


(defn select-author-by-surname [surname]
  (try
    (j/with-db-transaction [t-con db-map]
                           (j/query db-map ["SELECT * FROM autor WHERE prezimeautora=?" surname]
                                    ))
    (catch Exception e
      (throw (Exception. "Dogodila se greska!"))))
  )

(defn select-author-by-name [surname book year]
  (try
    (j/with-db-transaction [t-con db-map]
                           (j/query db-map ["SELECT * FROM nagradna_igra WHERE prezimeautora=? AND najpoznatijedelo=? AND godinaizdanja=?" surname book year]
                                    ))
    (catch Exception e
      (throw (Exception. "Dogodila se greska!"))))
  )

(defn select-author-by-year [surname year]
  (try
    (j/with-db-transaction [t-con db-map]
                           (j/query db-map ["SELECT * FROM nagradna_igra WHERE prezimeautora=? AND godinaizdanja=?" surname year]
                                    ))
    (catch Exception e
      (throw (Exception. "Dogodila se greska!"))))
  )

(defn select-authors-random []
  (try
    (j/with-db-transaction [t-con db-map]
                           (j/query db-map ["SELECT * FROM nagradna_igra ORDER BY RAND() LIMIT 5"]
                                    ))
    (catch Exception e
      (throw (Exception. "Dogodila se greska!"))))
  )

(defn select-author-by-id [id]
  (try
    (into [] (j/with-db-transaction [t-con db-map]
                                    (j/query db-map ["SELECT * FROM autor WHERE autorid=?" id]
                                             )))

    (catch Exception e
      (throw (Exception. "Dogodila se greska!"))))
  )

(defn delete-author [id]
  (j/with-db-transaction [t-con db-map]
                         (try
                           (j/db-unset-rollback-only! t-con)
                           (j/execute! t-con
                                       ["DELETE FROM autor WHERE autorid=?" id])
                           (catch Exception e
                             (j/db-set-rollback-only! t-con)
                             (throw (Exception. e))
                             )))
  )

(defn delete-autor-by-surname [surname]
  (j/with-db-transaction [t-con db-map]
                         (try
                           (j/db-unset-rollback-only! t-con)
                           (j/execute! t-con
                                       ["DELETE FROM autor WHERE prezimeautora=?" surname])
                           (catch Exception e
                             (j/db-set-rollback-only! t-con)
                             (throw (Exception. e))
                             )))
  )

(defn update-author [autorid name surname dateofbirth country mainbook]
  (j/with-db-transaction [t-con db-map]
                         (try
                           (j/db-unset-rollback-only! t-con)
                           (j/execute! t-con
                                       ["UPDATE autor SET imeautora=?, prezimeautora=?, datumrodjenja=?, zemljaporekla=?, najznacajnijedelo=?
                                      WHERE autorid=? " name surname dateofbirth country mainbook autorid]
                                       )
                           (catch Exception e
                             (j/db-set-rollback-only! t-con)
                             (throw (Exception. e))
                             )))
  )

(defn select_books [surnameAuthor]
  (try
    (into [] (j/with-db-transaction [t-con db-map]
                                    (j/query db-map ["SELECT * FROM autor a INNER JOIN publikacija p ON a.autorid=p.autorid WHERE prezimeautora=?" surnameAuthor]
                                             )))
    (catch Exception e
      (throw (Exception. "Dogodila se greska!"))))

  )
(defn select-books-order-name []
  (try
    (into [] (j/with-db-transaction [t-con db-map]
                                    (j/query db-map ["SELECT * FROM autor a INNER JOIN publikacija p ON a.autorid=p.autorid order by nazivpublikacije asc"]
                                             )))
    (catch Exception e
      (throw (Exception. "Dogodila se greska!"))))

  )

(defn select-books-order-year []
  (try
    (into [] (j/with-db-transaction [t-con db-map]
                                    (j/query db-map ["SELECT * FROM autor a INNER JOIN publikacija p ON a.autorid=p.autorid order by godinaizdanja asc"]
                                             )))
    (catch Exception e
      (throw (Exception. "Dogodila se greska!"))))

  )

(defn delete-book [id]
  (j/with-db-transaction [t-con db-map]
                         (try

                           (j/db-unset-rollback-only! t-con)
                           (j/execute! t-con
                                       ["DELETE FROM publikacija WHERE idpublikacije=?" id])
                           (catch Exception e
                             (j/db-set-rollback-only! t-con)
                             (throw (Exception. e))
                             )))
  )


(defn insert-book [namebook typeofbook year numberofexample author]
  (j/with-db-transaction [t-con db-map]
                         (try
                           (j/db-unset-rollback-only! t-con)
                           (j/insert! t-con
                                      :publikacija {:nazivpublikacije namebook :tippublikacije typeofbook :godinaizdanja year
                                              :brojprimeraka numberofexample :autorid author})

                           (catch Exception e
                             (j/db-set-rollback-only! t-con)
                             (throw (Exception. e))
                             )))
  )

(defn select-book-by-id [id]
  (try
    (into [] (j/with-db-transaction [t-con db-map]
                                    (j/query db-map ["SELECT * FROM autor a INNER JOIN publikacija p ON a.autorid=p.autorid WHERE idpublikacije=?" id]
                                             )))
    (catch Exception e
      (throw (Exception. "Dogodila se greska!"))))

  )


(defn update-book [id namebook typeofbook year numberofexample author]
  (j/with-db-transaction [t-con db-map]
                         (try

                           (j/db-unset-rollback-only! t-con)
                           (j/execute! t-con
                                       ["UPDATE publikacija SET nazivpublikacije=?, tippublikacije=?, godinaizdanja=?, brojprimeraka=?, autorid=?
                                        WHERE idpublikacije=? " namebook typeofbook year numberofexample author id]
                                       )
                           (catch Exception e
                             (j/db-set-rollback-only! t-con)
                             (throw (Exception. e))
                             )))
  )


