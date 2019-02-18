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

(defn select-genre-order-name []
  (try
    (into [] (j/with-db-transaction [t-con db-map]
                                    (j/query db-map ["SELECT * FROM zanr order by nazivzanra asc"]
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

(defn insert-new-member [jmbg name surname email phone dateofentry]

  (j/with-db-transaction [t-con db-map]
                         (try
                           (j/db-unset-rollback-only! t-con)
                           (j/insert! t-con
                                      :clan {:jmbg jmbg :ime name :prezime surname :email email
                                              :telefon phone :datumuclanjenja dateofentry})
                           (catch Exception e
                             (j/db-set-rollback-only! t-con)
                             (throw (Exception. e))
                             ))))

(defn insert-new-record-for-member [surname namebook datetake note]

  (j/with-db-transaction [t-con db-map]
                         (try
                           (j/db-unset-rollback-only! t-con)
                           (j/insert! t-con
                                      :evidencijaoclanu {:jmbg surname :publikacijaid namebook :datumuzimanjaknjige datetake
                                              :napomena note})
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

(defn select-member-by-surname [surnameMember]
  (try
    (j/with-db-transaction [t-con db-map]
                           (j/query db-map ["SELECT * FROM clan WHERE prezimea=?" surnameMember]
                                    ))
    (catch Exception e
      (throw (Exception. "Dogodila se greska!"))))
  )

(defn select-record-by-member [surname namebook]
  (try
    (j/with-db-transaction [t-con db-map]
                           (j/query db-map ["SELECT * FROM evidencijaoclanu evdclan INNER JOIN publikacija p ON evdclan.publikacijaid=p.idpublikacije JOIN clan c ON evdclan.jmbg=c.jmbg WHERE prezime=? AND nazivpublikacije=?" surname namebook]
                                    ))
    (catch Exception e
      (throw (Exception. "Dogodila se greska!"))))
  )

(defn select-member-by-surname [surnameMember]
  (try
    (j/with-db-transaction [t-con db-map]
                           (j/query db-map ["SELECT * FROM clan WHERE prezime=?" surnameMember]
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
                                    (j/query db-map ["SELECT * FROM autor a INNER JOIN publikacija p ON a.autorid=p.autorid JOIN zanr z ON p.zanrid=z.zanrid WHERE prezimeautora=?" surnameAuthor]
                                             )))
    (catch Exception e
      (throw (Exception. "Dogodila se greska!"))))

  )

(defn select_all_books []
  (try
    (into [] (j/with-db-transaction [t-con db-map]
                                    (j/query db-map ["SELECT * FROM autor a INNER JOIN publikacija p ON a.autorid=p.autorid JOIN zanr z ON p.zanrid=z.zanrid"]
                                             )))
    (catch Exception e
      (throw (Exception. "Dogodila se greska!"))))

  )

(defn select_records [surnameMember]
  (try
    (into [] (j/with-db-transaction [t-con db-map]
                                    (j/query db-map ["SELECT * FROM clan cl INNER JOIN evidencijaoclanu evdclan ON cl.jmbg=evdclan.jmbg JOIN publikacija p ON evdclan.publikacijaid=p.idpublikacije JOIN autor a ON p.autorid=a.autorid WHERE prezime=?" surnameMember]
                                             )))
    (catch Exception e
      (throw (Exception. "Dogodila se greska!"))))

  )

(defn select-books-order-name []
  (try
    (into [] (j/with-db-transaction [t-con db-map]
                                    (j/query db-map ["SELECT * FROM autor a INNER JOIN publikacija p ON a.autorid=p.autorid JOIN zanr z ON p.zanrid=z.zanrid  order by nazivpublikacije asc"]
                                             )))
    (catch Exception e
      (throw (Exception. "Dogodila se greska!"))))

  )

(defn select-name-member-order-name []
  (try
    (into [] (j/with-db-transaction [t-con db-map]
                                    (j/query db-map ["SELECT * FROM clan order by prezime asc"]
                                             )))
    (catch Exception e
      (throw (Exception. "Dogodila se greska!"))))

  )

(defn select-namebook-order-name []
  (try
    (into [] (j/with-db-transaction [t-con db-map]
                                    (j/query db-map ["SELECT * FROM publikacija WHERE status= ? order by nazivpublikacije asc" "dostupna"]
                                             )))
    (catch Exception e
      (throw (Exception. "Dogodila se greska!"))))

  )

(defn select-books-order-year []
  (try
    (into [] (j/with-db-transaction [t-con db-map]
                                    (j/query db-map ["SELECT * FROM autor a INNER JOIN publikacija p ON a.autorid=p.autorid JOIN zanr z ON p.zanrid=z.zanrid order by godinaizdanja asc"]
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


(defn insert-book [namebook genre year author]
  (j/with-db-transaction [t-con db-map]
                         (try
                           (j/db-unset-rollback-only! t-con)
                           (j/insert! t-con
                                      :publikacija {:nazivpublikacije namebook :zanrid genre :godinaizdanja year
                                                    :autorid author})

                           (catch Exception e
                             (j/db-set-rollback-only! t-con)
                             (throw (Exception. e))
                             )))
  )

(defn select-book-by-id [id]
  (try
    (into [] (j/with-db-transaction [t-con db-map]
                                    (j/query db-map ["SELECT * FROM autor a INNER JOIN publikacija p ON a.autorid=p.autorid JOIN zanr z ON p.zanrid=z.zanrid WHERE idpublikacije=?" id]
                                             )))
    (catch Exception e
      (throw (Exception. "Dogodila se greska!"))))

  )

(defn select-book-by-name [namebook]
  (try
    (into [] (j/with-db-transaction [t-con db-map]
                                    (j/query db-map ["SELECT * FROM autor a INNER JOIN publikacija p ON a.autorid=p.autorid JOIN zanr z ON p.zanrid=z.zanrid WHERE nazivpublikacije=?" namebook]
                                             )))
    (catch Exception e
      (throw (Exception. "Dogodila se greska!"))))

  )

(defn select-record-by-publicationid-jmbg [publikacijaid]
  (try
    (into [] (j/with-db-transaction [t-con db-map]
                                    (j/query db-map ["SELECT * FROM evidencijaoclanu evdclan JOIN publikacija p ON evdclan.publikacijaid=p.idpublikacije JOIN clan c ON evdclan.jmbg=c.jmbg WHERE publikacijaid=?" publikacijaid]
                                             )))
    (catch Exception e
      (throw (Exception. "Dogodila se greska!"))))

  )


(defn update-book [id namebook genre year status author]
  (j/with-db-transaction [t-con db-map]
                         (try

                           (j/db-unset-rollback-only! t-con)
                           (j/execute! t-con
                                       ["UPDATE publikacija SET nazivpublikacije=?, zanrid=?, godinaizdanja=?, status=?, autorid=?
                                        WHERE idpublikacije=? " namebook genre year status author id]
                                       )
                           (catch Exception e
                             (j/db-set-rollback-only! t-con)
                             (throw (Exception. e))
                             )))
  )

(defn update-record [publikacijaid jmbg dateoftake dateofreturn notes]
  (j/with-db-transaction [t-con db-map]
                         (try

                           (j/db-unset-rollback-only! t-con)
                           (j/execute! t-con
                                       ["UPDATE evidencijaoclanu SET datumuzimanjaknjige=?, datumvracanjaknjige=?, napomena=?
                                        WHERE publikacijaid=? AND jmbg=? " dateoftake dateofreturn notes publikacijaid jmbg]
                                       )
                           (catch Exception e
                             (j/db-set-rollback-only! t-con)
                             (throw (Exception. e))
                             )))
  )



(defn update-name-book-for-member [nameofbook]
  (j/with-db-transaction [t-con db-map]
                         (try

                           (j/db-unset-rollback-only! t-con)
                           (j/execute! t-con
                                       ["UPDATE publikacija SET status = ? WHERE idpublikacije = ?" "nije dostupna" nameofbook]
                                       )
                           (catch Exception e
                             (j/db-set-rollback-only! t-con)
                             (throw (Exception. e))
                             )))
  )

(defn delete-record-for-member-after-update [nameofbook]
  (j/with-db-transaction [t-con db-map]
                         (try

                           (j/db-unset-rollback-only! t-con)
                           (j/execute! t-con
                                       ["DELETE FROM evidencijaoclanu WHERE publikacijaid=?" nameofbook])
                           (catch Exception e
                             (j/db-set-rollback-only! t-con)
                             (throw (Exception. e))
                             )))
  )

(defn update-status-book-for-member-after-update [nameofbook]
  (j/with-db-transaction [t-con db-map]
                         (try

                           (j/db-unset-rollback-only! t-con)
                           (j/execute! t-con
                                       ["UPDATE publikacija SET status = ? WHERE idpublikacije = ?" "dostupna" nameofbook]
                                       )
                           (catch Exception e
                             (j/db-set-rollback-only! t-con)
                             (throw (Exception. e))
                             )))
  )


