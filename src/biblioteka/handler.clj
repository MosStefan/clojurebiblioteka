(ns biblioteka.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.util.response :as ring]
            [hiccup.core :refer :all]
            [hiccup.page :refer [doctype include-css]]
            [hiccup.page :refer :all]
            [hiccup.form :as f]
            [hiccup.table :as t]
             (clj-time [format :as ftime])
            [hiccup.bootstrap.page :refer :all]
            [hiccup.bootstrap.element :as hbe]
            [hiccup.element :refer [link-to]]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
            [ring.util.response :refer [redirect]]
            [biblioteka.views.layout :as layout]
            [biblioteka.views.menu :as menu]
            [biblioteka.models.db :as db]))

 (defn anti-forgery-field []
(f/hidden-field "__anti-forgery-token" *anti-forgery-token*))

; POCETNA STRANA
(defn index []
  (layout/pagelayout "Biblioteka"
                (menu/menuapp)

                [:br]
                [:br]
                [:div {:class "container"}
                 [:div {:class "row"}
                  [:div {:class "col-3"}]
                  [:div {:class "col-8"}


                   ]
                  ]
                 [:div {:class "col-2"}]]
                )

  )



(defn empty-name []
  [:label {:for "n" :style "color:red"} [:b "Morate da unesete ime autora!!!"]]
  )
(defn empty-surname []
  [:label {:for "s" :style "color:red"} [:b "Morate da unesete prezime autora!!!"]]
  )
(defn empty-country []
  [:label {:for "c" :style "color:red"} [:b "Morate da unesete zemlju porekla!!!"]]
  )
(defn empty-dateofbirth []
  [:label {:for "dbr" :style "color:red"} [:b "Morate da unesete datum rodjenja!!!"]]
  )
(defn empty-mainbook []
  [:label {:for "mb"  :style "color:red"} [:b "Morate da unesete najznacajnije delo!!!"]]
  )
(defn dateofbirth-format []
  [:label {:for "ym" :style "color:red"} [:b "Morate da unesete ispravan format za datum rodjenja: YYYY-MM-dd!!! "]]
  )

(defn year-must-number []
  [:label {:for "en" :style "color:red"} [:b "Morate da unesete broj za godinu!!!"]]
  )


(defn exist-name-of-author [surname]
  [:label {:style "color:red; border-color: #000000;"} [:b (str "Nije ubacen autor " surname ", jer vec postoji u bazi!!!")]]
  )

(defn success-insert-author [surname]
  [:p {:style "color:green; border-color: #000000;"} [:b (str "Uspesno ubacen autor " surname "!!!")]])
(defn success-update-author [surname]
  [:p {:style "color:green; border-color: #000000;"} [:b (str "Uspesno izmenjen autor " surname "!!!")]])
(defn exist-name-of-author-update [surname]
  [:label {:style "color:red; border-color: #000000;"} [:b (str "Nije izmenjen autor " surname "!!!")]])

(defn empty-surname-author []
  [:label {:for "na" :style "color:red"} [:b "Morate da unesete prezime autora!!!"]]
  )
(defn insert-new-author-page [name surname dateofbirth country mainbook]
  (layout/pagelayout "Biblioteka" (menu/menuapp)
                [:br]
                [:div {:class "container"}
                 [:div {:class "row"}
                  [:div {:class "col"}]
                  [:div {:class "col-6"}
                   [:h3 [:b "Unos novog autora"]]
                   [:div {:class "tab-content"}
                    [:div {:class "tab-pane active" :role "tabpanel"}
                     (f/form-to [:get "/insertNewAuthor"]
                                (anti-forgery-field)
                                [:div {:class "form-group"}
                                 [:label {:for "n"} [:b "Ime autora:"]]
                                 [:input {:type "text" :name "name" :class "form-control" :id "n" :value name} ]
                                 (if (or (nil? name) (empty? name))
                                   (empty-name)
                                   )
                                 ]
                                [:div {:class "form-group"}
                                 [:label {:for "s"} [:b "Prezime autora:"]]
                                 [:input {:type "text" :name "surname" :class "form-control" :id "s" :value surname} ]
                                 (if (or (nil? surname) (empty? surname))
                                   (empty-surname)
                                   )
                                 ]


                                [:div {:class "form-group"}
                                 [:label {:for "dbr"} [:b "Datum rodjenja:"]]
                                 [:input {:type "text" :name "dateofbirth" :class "form-control" :id "dbr" :value dateofbirth} ]
                                 (if (or (nil? dateofbirth) (empty? dateofbirth))
                                   (empty-dateofbirth)
                                   (try
                                     (ftime/parse (ftime/formatter "YYYY-MM-dd") dateofbirth)
                                     [:label {:for "ym" :style "color:red"} [:b ""]]
                                     (catch Exception e
                                       (dateofbirth-format))))
                                 ]

                                [:div {:class "form-group"}
                                 [:label {:for "c"} [:b "Zemlja porekla:"]]
                                 [:input {:type "text" :name "country" :class "form-control" :id "c" :value country} ]
                                 (if (or (nil? country) (empty? country))
                                   (empty-country)
                                   )
                                 ]

                                [:div {:class "form-group"}
                                 [:label {:for "mb"} [:b "Najznacajnije delo:"]]
                                 [:input {:type "text" :name "mainbook" :class "form-control" :id "mb" :value mainbook} ]
                                 (if (or (nil? mainbook) (empty? mainbook))
                                   (empty-mainbook)
                                   )
                                 ]




                                [:input {:type  "submit"
                                         :value "Ubaci novog autora"
                                         :class "btn btn-success"
                                         }]
                                (h "   ")
                                [:a {:href "/insertAuthor"} [:input {:type  "button"
                                                                   :value "Resetuj  sve vrednosti"
                                                                   :class "btn btn-warning"}]])]]]
                  [:div {:class "col"}
                   (if (or (nil? name) (nil? surname) (nil? dateofbirth) (nil? country) (nil? mainbook) (empty? name) (empty? surname) (empty? dateofbirth) (empty? country) (empty? mainbook))
                     ""
                     (try
                       (if (empty? (db/select-author-by-surname surname))
                         (try
                           (db/insert-author name surname dateofbirth country mainbook)
                           (success-insert-author surname)


                           (catch Exception e
                             [:p {:style "color:red; border-color: #000000;"} [:b (str "Nije ubacen autor!!!" surname)]]

                             ))
                         )

                       (catch Exception e

                         (exist-name-of-author surname)
                         )))
                   ]]
                 ])
  )

(defn insert-author-page []
  (layout/pagelayout "Biblioteka" (menu/menuapp)
                [:br]
                [:div {:class "container"}
                 [:div {:class "row"}
                  [:div {:class "col"}]
                  [:div {:class "col-6"}
                   [:h3 [:b "Unos novog autora"]]
                   [:div {:class "tab-content"}
                    [:div {:class "tab-pane active" :role "tabpanel"}
                     (f/form-to [:get "/insertNewAuthor"]
                                (anti-forgery-field)

                                [:div {:class "form-group"}
                                 [:label {:for "n"} [:b "Ime autora"]]
                                 [:input {:type "text" :name "name" :class "form-control" :id "n"}]
                                 ]

                                [:div {:class "form-group"}
                                 [:label {:for "s"} [:b "Prezime autora"]]
                                 [:input {:type "text" :name "surname" :class "form-control" :id "s"}]
                                 ]

                                [:div {:class "form-group"}
                                 [:label {:for "dbr"} [:b "Datum rodjenja"]]
                                 [:input {:type "text" :name "dateofbirth" :class "form-control" :id "dbr"}]

                                 ]

                                [:div {:class "form-group"}
                                 [:label {:for "c"} [:b "Zemlja porekla"]]
                                 [:input {:type "text" :name "country" :class "form-control" :id "c"}]

                                 ]

                                [:div {:class "form-group"}
                                 [:label {:for "mb"} [:b "Najznacajnije delo"]]
                                 [:input {:type "text" :name "mainbook" :class "form-control" :id "mb"}]

                                 ]

                                [:input {:type  "submit" :read-only "false"
                                         :value "Ubaci novog autora"
                                         :class "btn btn-success"
                                         }]
                                (h "   ")
                                [:input {:type  "reset"
                                         :value "Resetuj vrednosti"
                                         :class "btn btn-warning"
                                         }])
                     ]]

                   ]
                  [:div {:class "col"}]]
                 ]
                )
  )

;STRANA ZA PRIKAZ SVIH AUTORA
(defn show-authors []
  (try
    [:br]
    [:br]
    [:div {:class "container"}
     [:div {:class "row"}
      [:div {:class "col-9"}
       [:h2 [:b "Prikaz svih autora" ]]
       [:br]
       [:br]
       [:table {:class "table table-dark"}
        [:thead
         [:tr
          [:th "Ime autora"]
          [:th "Prezime autora"]
          [:th "Datum rodjenja"]
          [:th "Zemlja rodjenja"]
          [:th "Najznacajnije delo"]
          [:th "Obrisi autora"]
          [:th "Izmeni podatke o autoru"]
          ]]

        (into [:tbody]
              (for [author (db/select-authors)]
                [:tr [:td (:imeautora author)]
                 [:td (:prezimeautora author)]
                 [:td (:datumrodjenja author)]
                 [:td (:zemljaporekla author)]
                 [:td (:najznacajnijedelo author)]
                 [:td [:a {:href (str "/deleteAuthor/" (h (:autorid author))) :style "color:red;"} "Obrisi "]]
                 [:td [:a {:href (str "/updateAuthor/" (h (:autorid author))) :style "color:red;"} "Izmeni "]]]))]
       ]
      [:div {:class "col"}]]]
    (catch Exception e
      (throw (Exception. e))))
  )


(defn select-authors []
  (try
    (layout/pagelayout "Biblioteka" (menu/menuapp)
                  [:br]
                  [:br]
                  [:br]
                  [:br]
                  [:br]
                  [:br]
                  (show-authors))
    (catch Exception e
      (throw (Exception. e))))
  )
(defn error_message [e]
  [:br] [:br] [:p [:b "Dogodila se greska prilikom ucitavanja novog autora!"]]
  )

(defn error_sql [e]
  [:br] [:br] [:p [:b "Nema autora!"]]
  )

(defn error-load-author [error]
  (layout/pagelayout "Biblioteka" (menu/menuapp)
                [:br] [:br] [:p [:b "Dogodila se greska prilikom ucitavanja autora!"]]
                )
  )

; STRANA ZA BRISANJE AUTORA
(defn show-authors-for-delete []

  [:div {:class "container"}
   [:div {:class "row"}
    [:div {:class "col-6"}
     (f/form-to [:get "/deleteThisAuthor"]
                (anti-forgery-field)
                [:div {:class "form-group"}
                 [:label {:for "s"} [:b "Unesite prezime autora :"]]
                 [:input {:type "text" :name "surname" :class "form-control" :id "s"}]
                 ]
                [:input {:type  "submit"
                         :value "Obrisi autora"
                         :class "btn btn-danger"
                         }]
                (h "   ")
                [:a {:href "/deleteAuthor"} [:input {:type  "button"
                                                   :value "Resetuj  sve vrednosti"
                                                   :class "btn btn-warning"}]]
                )

     ]
    [:div {:class "col-10"}
     [:h2 [:b "Prikaz svih autora"]]
     [:br]
     [:table
      [:thead
       [:tr
        [:th "Ime autora"]
        [:th "Prezime autora"]
        [:th "Datum rodjenja"]
        [:th "Zemlja rodjenja"]
        [:th "Najznacajnije delo"]
        ]]

      (into [:tbody]
            (try
              (for [author (db/select-authors)]
                [:tr [:td (:imeautora author)]
                 [:td (:prezimeautora author)]
                 [:td (:datumrodjenja author)]
                 [:td (:zemljaporekla author)]
                 [:td (:najznacajnijedelo author)]])
              (catch Exception e
                [[:b "Dogodila se greska prilikom ucitavanja autora!"]]
                ))
            )]
     ]
    [:div {:class "col"}]]]

  )
(defn select-authors-for-delete []
  (try
    (layout/pagelayout "Biblioteka" (menu/menuapp)
                  [:br]
                  [:br]
                  [:br]
                  [:br]
                  (show-authors-for-delete))
    (catch Exception e
      (throw (Exception. e))))
  )

(defn show-authors-which-delete [surname]
  [:div {:class "container"}
   [:div {:class "row"}
    [:div {:class "col-6"}
     (if (or (nil? surname) (empty? surname))
       ""
       (try
         (if (empty? (db/select-author-by-surname surname))
           [:p {:style "color:red; border-color: #000000;"} [:b (str "Autor " surname " koga zelite da obrisete ne postoji!!!")]]
           (try
             (db/delete-autor-by-surname surname)
             [:p {:style "color:green; border-color: #000000;"} [:b (str "Autor " surname "  je uspesno obrisan!!!")]]
             (catch Exception e
               [:p {:style "color:red; border-color: #000000;"} [:b (str "Autor " surname " koga zelite da obrisete ne postoji!!!")]]
               )))


         (catch Exception e
           [:p {:style "color:red; border-color: #000000;"} [:b (str "Nije obrisan autor " surname "!!!")]]

           )))
     (f/form-to [:get "/deleteThisAuthor"]
                (anti-forgery-field)
                [:div {:class "form-group"}
                 [:label {:for "s"} [:b "Unesite prezime autora :"]]
                 [:input {:type "text" :name "surname" :class "form-control" :id "s" :value surname}]
                 (if (or (nil? surname) (empty? surname))
                   (empty-surname-author)
                   )
                 ]
                [:input {:type  "submit"
                         :value "Obrisi autora"
                         :class "btn btn-danger"
                         }]
                (h "   ")
                [:a {:href "/deleteAuthor"} [:input {:type  "button"
                                                   :value "Resetuj sve vrednosti"
                                                   :class "btn btn-warning"}]]
                )

     ]
    [:div {:class "col-10"}
     [:h2 [:b "Prikaz svih autora" ]]
     [:br]
     [:table {:class "table table-dark"}
      [:thead
       [:tr
        [:th "Ime autora"]
        [:th "Prezime autora"]
        [:th "Datum rodjenja"]
        [:th "Zemlja rodjenja"]
        [:th "Najznacajnije delo"]

        ]]

      (into [:tbody]
            (try
              (for [author (db/select-authors)]
                [:tr [:td (:imeautora author)]
                 [:td (:prezimeautora author)]
                 [:td (:datumrodjenja author)]
                 [:td (:zemljaporekla author)]
                 [:td (:najznacajnijedelo author)]])
              (catch Exception e
                [[:b "Dogodila se greska prilikom ucitavanja autora!"]]
                ))
            )]
     ]
    [:div {:class "col-8"}

     ]]]
  )
(defn select-authors-which-delete [surname]
  (try
    (layout/pagelayout "Biblioteka" (menu/menuapp)
                  [:br]
                  [:br]
                  [:br]
                  [:br]
                  (show-authors-which-delete surname))
    (catch Exception e
      (throw (Exception. e))))
  )

; STRANA ZA IZMENU AUTORA
(defn update-author [id]
  (layout/pagelayout "Biblioteka" (menu/menuapp)
                [:br]
                [:div {:class "container"}
                 [:div {:class "row"}
                  [:div {:class "col"}]
                  [:div {:class "col-6"}
                   [:h3 [:b "Izmena podataka izabranog autora"]]
                   (try
                     (for [author (db/select-author-by-id id)]
                       [:div {:class "tab-content"}
                        [:div {:class "tab-pane active" :role "tabpanel"}
                         (f/form-to [:get (str "/updateThisAuthor/" id)]
                                    (anti-forgery-field)
                                    [:div {:class "form-group"}
                                     [:label {:for "n"} [:b "Ime autora:"]]
                                     [:input {:type "text" :name "name" :class "form-control" :id "n" :value (:imeautora author)}]

                                     ]


                                    [:div {:class "form-group"}
                                     [:label {:for "s"} [:b "Prezime autora:"]]
                                     [:input {:type "text" :name "surname" :class "form-control" :id "s" :value (:prezimeautora author)}]

                                     ]

                                    [:div {:class "form-group"}
                                     [:label {:for "db"} [:b "Datum rodjenja:"]]
                                     [:input {:type "text" :name "dateofbirth" :class "form-control" :id "a" :value (:datumrodjenja author)}]

                                     ]

                                    [:div {:class "form-group"}
                                     [:label {:for "c"} [:b "Zemlja porekla:"]]
                                     [:input {:type "text" :name "country" :class "form-control" :id "c" :value (:zemljaporekla author)}]

                                     ]

                                    [:div {:class "form-group"}
                                     [:label {:for "mb"} [:b "Najznacajnije delo:"]]
                                     [:input {:type "text" :name "mainbook" :class "form-control" :id "mb" :value (:najznacajnijedelo author)}]

                                     ]

                                    [:input {:type  "submit"
                                             :value "Izmeni podatke o autoru"
                                             :class "btn btn-primary"
                                             }]
                                    (h "   ")
                                    [:a {:href (str "/updateAuthor/" id)} [:input {:type  "button"
                                                                                 :value "Resetuj sve vrednosti"
                                                                                 :class "btn btn-warning"}]])]])
                     (catch Exception e
                       [:p {:style "color:red; border-color: #000000;"} [:b (str "Doslo je do greske prilikom ucitavanja datog autora!!!")]]
                       ))

                   ]
                  [:div {:class "col"}]]])
  )

(defn update-this-author [id name surname dateofbirth country mainbook]
  (layout/pagelayout "Biblioteka" (menu/menuapp)
                [:br]
                [:div {:class "container"}
                 [:div {:class "row"}
                  [:div {:class "col"}]
                  [:div {:class "col-6"}
                   [:h3 [:b "Izmena podataka izabranog autora"]]
                   [:div {:class "tab-content"}
                    [:div {:class "tab-pane active" :role "tabpanel"}
                     (f/form-to [:get (str "/updateThisAuthor/" id)]
                                (anti-forgery-field)
                                [:div {:class "form-group"}
                                 [:label {:for "n"} [:b "Ime autora:"]]
                                 [:input {:type "text" :name "name" :class "form-control" :id "n" :value name}]
                                 (if (or (nil? name) (empty? name))
                                   (empty-name)
                                   )
                                 ]
                                [:div {:class "form-group"}
                                 [:label {:for "s"} [:b "Prezime autora:"]]
                                 [:input {:type "text" :name "surname" :class "form-control" :id "s" :value surname}]
                                 (if (or (nil? surname) (empty? surname))
                                   (empty-surname)
                                   )
                                 ]


                                [:div {:class "form-group"}
                                 [:label {:for "db"} [:b "Datum rodjenja:"]]
                                 [:input {:type "text" :name "dateofbirth" :class "form-control" :id "db" :value dateofbirth}]
                                 (if (or (nil? dateofbirth) (empty? dateofbirth))
                                   (empty-dateofbirth)
                                   (try
                                     (ftime/parse (ftime/formatter "YYYY-MM-dd") dateofbirth)
                                     [:label {:for "ym" :style "color:red"} [:b ""]]
                                     (catch Exception e
                                       (dateofbirth-format))))
                                 ]

                                [:div {:class "form-group"}
                                 [:label {:for "c"} [:b "Zemlja porekla:"]]
                                 [:input {:type "text" :name "country" :class "form-control" :id "c" :value country}]
                                 (if (or (nil? country) (empty? country))
                                   (empty-country)
                                   )
                                 ]

                                [:div {:class "form-group"}
                                 [:label {:for "mb"} [:b "Najznacajnije delo:"]]
                                 [:input {:type "text" :name "mainbook" :class "form-control" :id "mb" :value mainbook}]
                                 (if (or (nil? mainbook) (empty? mainbook))
                                   (empty-mainbook)
                                   )
                                 ]





                                [:input {:type  "submit"
                                         :value "Izmeni podatke o autoru"
                                         :class "btn btn-primary"
                                         }]
                                (h "   ")
                                [:a {:href (str "/updateAuthor/" id)} [:input {:type  "button"
                                                                             :value "Resetuj sve vrednosti"
                                                                             :class "btn btn-warning"}]])]]]
                  [:div {:class "col"}
                   (if (or (nil? name) (nil? surname) (nil? dateofbirth) (nil? country) (nil? mainbook) (empty? name) (empty? surname) (empty? dateofbirth) (empty? country) (empty? mainbook))
                     ""
                     (try
                       (if (or (empty? (db/select-author-by-surname surname)) (= (count (db/select-author-by-surname surname)) 1))
                         (try
                           (db/update-author id name surname dateofbirth country mainbook)
                           (success-update-author surname)


                           (catch Exception e
                             (exist-name-of-author-update surname)))
                         )

                       (catch Exception e
                         [:p {:style "color:red; border-color: #000000;"} [:b (str "Nije izmenjen autor!!!" surname)]]

                         )))
                   ]]
                 ])
  )


; STRANA ZA PRIKAZ KNJIGA !!!!!!!!!
(defn show-books []
  [:div {:class "container"}
   [:div {:class "row"}
    [:div {:class "col-6"}
     (f/form-to [:get "/showAllBooks"]
                (anti-forgery-field)
                [:div {:class "form-group"}
                 [:label {:for "nat"} [:b "Unesite prezime autora cija dela zelite da prikazete:"] ]
                 [:input {:type "text" :name "surnameAuthor" :class "form-control" :id "nat"}]
                 ]
                [:input {:type "submit"
                         :value "Prikazi knjige"
                         :class "btn btn-primary"
                         }]
                (h "   ")
                [:a {:href "/showBooks"}[:input {:type "button"
                                                   :value "Resetuj sve vrednosti"
                                                   :class "btn btn-warning"}]]
                )

     ]
    [:div {:class "col-10"}

     ]
    [:div {:class "col"}]]]
  )
(defn select-books []
  (try
    (layout/pagelayout "Biblioteka" (menu/menuapp)
                   [:br]
                   [:br]
                   [:br]
                   [:br]
                   [:br]
                   [:br]
                   (show-books))
    (catch Exception e
      (throw (Exception. e))))
  )

(defn show-all-books [surnameAuthor]
  [:div {:class "container"}
   [:div {:class "row"}
    [:div {:class "col-6"}
     (if (or (nil? surnameAuthor) (empty? surnameAuthor))
       ""

       (try
         (if (empty? (db/select-author-by-surname surnameAuthor))
           [:p {:style "color:red; border-color: #000000;"} [:b (str "Autor " surnameAuthor " cija dela zelite da prikazete ne postoji!!!")] ]
           (try
              (= (count (db/select_books surnameAuthor)) 0)
             [:p {:style "color:green; border-color: #000000;"} [:b (str "Dela  " surnameAuthor "-a su uspesno prikazana!!!")] ]
             (catch Exception e
               [:p {:style "color:red; border-color: #000000;"} [:b (str "Pisac " surnameAuthor " cija dela zelite da prikazete ne postoji!!!")] ]
               )))
         


         (catch Exception e
           [:p {:style "color:red; border-color: #000000;"} [:b (str "Nisu prikazana dela od " surnameAuthor "-a!!!")] ]


           ))




       
       
       )
     (f/form-to [:get "/showAllBooks"]
                (anti-forgery-field)
                [:div {:class "form-group"}
                 [:label {:for "nat"} [:b "Unesite prezime autora cije dela zelite da prikazete:"] ]
                 [:input {:type "text" :name "surnameAuthor" :class "form-control" :id "nat" :value surnameAuthor}]
                 (if (or (nil? surnameAuthor) (empty? surnameAuthor) )
                   (empty-surname-author)
                   )
                 ]
                [:input {:type "submit"
                         :value "Prikazi knjige"
                         :class "btn btn-primary"
                         }]
                (h "   ")
                [:a {:href "/showBooks"}[:input {:type "button"
                                                   :value "Resetuj sve vrednosti"
                                                   :class "btn btn-warning"}]]
                )

     ]
    [:div {:class "col-10"}
     [:h2 [:b "Prikaz svih knjiga" ]]
     [:br]
     [:table {:class "table table-dark"}
      [:thead
       [:tr
        [:th "Naziv knjige"]
        [:th "Zanr knjige"]
        [:th "Godina izdanja"]
        [:th "BrojPrimeraka"]
        [:th "Obrisi knjigu"]
        [:th "Izmeni knjigu"]
        ]]

      (into [:tbody]
            (try
              (for [book (db/select_books surnameAuthor)]
                [:tr [:td (:nazivpublikacije book)]
                 [:td (:tippublikacije book)]
                 [:td (:godinaizdanja book)]
                 [:td (:brojprimeraka book)]
                 [:td [:a {:href (str "/deleteBook/" (h (:idpublikacije book)) "/" (h (:prezimeautora book)) ) :style "color:purple;"} "Obrisi"]]
                 [:td [:a {:href (str "/updateBook/" (h (:idpublikacije book)) ) :style "color:purple;"} "Izmeni"]]])
              (catch Exception e
                [[:b "Dogodila se greska prilikom ucitavanja knjige!"]]
                ;(throw (Exception. e))
                ))
            )]
     ]
    [:div {:class "col-8"}

     ]]]
  )

(defn select-all-books [nameBook]
  (try
    (layout/pagelayout "Biblioteka" (menu/menuapp)
                   [:br]
                   [:br]
                   [:br]
                   [:br]
                   [:br]
                   [:br]
                   (show-all-books nameBook))
    (catch Exception e
      (throw (Exception. e))))
  )

(defn show-all-the-books [surnameAuthor]
  [:div {:class "container"}
   [:div {:class "row"}
    [:div {:class "col-6"}
     (if (or (nil? surnameAuthor) (empty? surnameAuthor))
       ""
       (try
         (if (empty? (db/select-author-by-surname surnameAuthor))

           (try
             (db/select_books surnameAuthor)

             (catch Exception e

               )))


         (catch Exception e



           )))
     (f/form-to [:get "/showAllBooks"]
                (anti-forgery-field)
                [:div {:class "form-group"}
                 [:label {:for "nat"} [:b "Unesite prezime autora cije dela zelite da prikazete:"] ]
                 [:input {:type "text" :name "surnameAuthor" :class "form-control" :id "nat" :value surnameAuthor}]

                 ]
                [:input {:type "submit"
                         :value "Prikazi knjige"
                         :class "btn btn-primary"
                         }]
                (h "   ")
                [:a {:href "/showBooks"}[:input {:type "button"
                                                   :value "Resetuj sve vrednosti"
                                                   :class "btn btn-warning"}]]
                )

     ]
    [:div {:class "col-10"}
     [:h2 [:b "Prikaz svih knjiga" ]]
     [:br]
     [:table {:class "table table-dark"}
      [:thead
       [:tr
        [:th {:style "border-style: solid;  border-color: #000000;    background-color: red;    color: black;"} "Naziv knjige"  ]
        [:th {:style "border-style: solid;  border-color: #000000;    background-color: red;    color: black;"} "Zanr knjige"  ]
        [:th {:style "border-style: solid;  border-color: #000000;    background-color: red;    color: black;"}"Godina izdanja" ]
        [:th {:style "border-style: solid;  border-color: #000000;    background-color: red;    color: black;"}"Broj primeraka"  ]
        [:th {:style "border-style: solid;  border-color: #000000;    background-color: red;    color: black;"}"Obrisi knjigu"  ]
        [:th {:style "border-style: solid;  border-color: #000000;    background-color: red;    color: black;"} "Izmeni knjigu"]
        ]]

      (into [:tbody]
            (try
              (for [book (db/select_books surnameAuthor)]
                [:tr [:td {:style "border-style: solid;  border-color: #000000;    background-color: yellow;    color: black;"} (:nazivpublikacije book)]
                 [:td {:style "border-style: solid;  border-color: #000000;    background-color: yellow;    color: black;"} (:tippublikacije book)]
                 [:td {:style "border-style: solid;  border-color: #000000;    background-color: yellow;    color: black;"} (:godinaizdanja book)]
                 [:td {:style "border-style: solid;  border-color: #000000;    background-color: yellow;    color: black;"} (:brojprimeraka book)]
                 [:td {:style "border-style: solid;  border-color: #000000;    background-color: yellow;    color: black;"} [:a {:href (str "/deleteBook/" (h (:idpublikacije book))  "/" (h (:prezimeautora book)) ) :style "color:purple;"} "Obrisi"]]
                 [:td {:style "border-style: solid;  border-color: #000000;    background-color: yellow;    color: black;"}  [:a {:href (str "/updateBook/" (h (:idpublikacije book))) :style "color:purple;"} "Izmeni"]]])
              (catch Exception e
                [[:b "Dogodila se greska prilikom ucitavanja knjige!"]]
                ;(throw (Exception. e))
                ))
            )]
     ]
    [:div {:class "col-8"}

     ]]]
  )

(defn select-all-the-books[surnameAuthor]
  (try
    (layout/pagelayout "Biblioteka" (menu/menuapp)
                   [:br]
                   [:br]
                   [:br]
                   [:br]
                   [:br]
                   [:br]
                   (show-all-the-books surnameAuthor))
    (catch Exception e
      (throw (Exception. e))))
  )


; STRANA ZA UBACIVANJE KNJIGE!!!!!!!!!!!!!!!!!!
(defn insert-book []
  (layout/pagelayout "Biblioteka" (menu/menuapp)
                 [:br]
                 [:div {:class "container"}
                  [:div {:class "row"}
                   [:div {:class "col"}]
                   [:div {:class "col-6"}
                    [:h2 [:b "Unos nove knjige" ]]
                    [:div {:class "tab-content" }
                     [:div {:class "tab-pane active" :role "tabpanel"}
                      (f/form-to [:get "/insertNewBook"]
                                 (anti-forgery-field)
                                 [:div {:class "form-group"}
                                  [:label {:for "nb"} [:b "Naziv knjige:"] ]
                                  [:input {:type "text" :name "namebook" :class "form-control" :id "nb"}]
                                  ]
                                 [:div {:class "form-group"}
                                  [:label {:for "tb"} [:b "Zanr knjige:"] ]
                                  [:input {:type "text" :name "typeofbook" :class "form-control" :id "tb"}]

                                  ]

                                 [:div {:class "form-group"}
                                  [:label {:for "y"} [:b "Godina izdanja:"]]
                                  [:input {:type "text" :name "year" :class "form-control" :id "y"}]

                                  ]

                                 [:div {:class "form-group"}
                                  [:label {:for "ne"}[:b "Broj primeraka:"]]
                                  [:input {:type "text" :name "numberofexample" :class "form-control" :id "ne"}]

                                  ]
                                 [:div {:class "form-group"}
                                  [:label {:for "na"} [:b "Autor knjige:"]]
                                  [:select {:name "author" :id "na" :class "form-control"}
                                   (try
                                     (for [author (db/select-authors-order-surname)]
                                       [:option {:value (:autorid author)} (:prezimeautora author)]
                                       )

                                     (catch Exception e
                                       [:b "Dogodila se greska prilikom ucitavanja autora!"]))
                                   ]
                                  ]
                                 [:input {:type "submit" :read-only "false"
                                          :value "Ubaci novu knjigu"
                                          :class "btn btn-primary"
                                          }]
                                 (h "   ")
                                 [:input {:type "reset"
                                          :value "Resetuj sve vrednosti"
                                          :class "btn btn-warning"
                                          }])
                      ]]

                    ]
                   [:div {:class "col"}]]
                  ]
                 )
  )

(defn insert-new-book [namebook typeofbook year numberofexample author]
  (layout/pagelayout "Biblioteka" (menu/menuapp)
                 [:br]
                 [:div {:class "container"}
                  [:div {:class "row"}
                   [:div {:class "col"}]
                   [:div {:class "col-6"}
                    [:h2 [:b "Unos nove knjige" ]]
                    [:div {:class "tab-content" }
                     [:div {:class "tab-pane active" :role "tabpanel"}
                      (f/form-to [:get "/insertNewBook"]
                                 (anti-forgery-field)
                                 [:div {:class "form-group"}
                                  [:label {:for "nb"} [:b "Naziv knjige:"] ]
                                  [:input {:type "text" :name "namebook" :class "form-control" :id "nb" :value namebook}]
                                  (if (or (nil? namebook) (empty? namebook) )
                                    [:label {:for "nb" :style"color:red"} [:b "Morate da unesete naziv knjige!!!"] ]
                                    )
                                  ]
                                 [:div {:class "form-group"}
                                  [:label {:for "tb"} [:b "Zanr knjige:"] ]
                                  [:input {:type "text" :name "typeofbook" :class "form-control" :id "tb" :value typeofbook} ]
                                  (if (or (nil? typeofbook) (empty? typeofbook))
                                    [:label {:for "tb" :style"color:red"} [:b "Morate da unesete zanr knjige!!!"] ])
                                  ]

                                 [:div {:class "form-group"}
                                  [:label {:for "y"} [:b "Godina izdanja :"]]
                                  [:input {:type "text" :name "year" :class "form-control" :id "y" :value year} ]
                                  (if (or (nil? year) (empty? year))
                                    [:label {:for "y" :style"color:red"} [:b "Morate da unesete godinu izdanja!!!"] ]
                                    (try
                                      (Integer/parseInt year)
                                      [:label {:for "y" :style"color:red"} [:b ""] ]
                                      (catch Exception e
                                        [:label {:for "y" :style"color:red"} [:b "Morate da unesete broj!!!"] ]))
                                    )
                                  ]

                                 [:div {:class "form-group"}
                                  [:label {:for "ne"} [:b "Broj primeraka :"]]
                                  [:input {:type "text" :name "numberofexample" :class "form-control" :id "ne" :value numberofexample} ]
                                  (if (or (nil? numberofexample) (empty? numberofexample))
                                    [:label {:for "ne" :style"color:red"} [:b "Morate da unesete broj primeraka!!!"] ]
                                    (try
                                      (Integer/parseInt numberofexample)
                                      [:label {:for "ne" :style"color:red"} [:b ""] ]
                                      (catch Exception e
                                        [:label {:for "ne" :style"color:red"} [:b "Morate da unesete broj!!!"] ]))
                                    )
                                  ]


                                 [:div {:class "form-group"}
                                  [:label {:for "na"} [:b "Autor:"]]
                                  [:select {:name "author" :id "na" :class "form-control"}
                                   (try
                                     (for [author (db/select-authors-order-surname)]
                                       [:option {:value (:autorid author)} (:prezimeautora author)]
                                       )

                                     (catch Exception e
                                       [:b "Dogodila se greska prilikom ucitavanja autora!"]))
                                   ]
                                  ]
                                 [:input {:type "submit"
                                          :value "Ubaci novu knjigu"
                                          :class "btn btn-primary"
                                          }]
                                 (h "   ")
                                 [:a {:href "/insertBook"}[:input {:type "button"
                                                                     :value "Resetuj sve vrednosti"
                                                                     :class "btn btn-warning"}]])]]]
                   [:div {:class "col"}
                    (if (or (nil? namebook) (nil? typeofbook) (nil? year) (nil? numberofexample) (empty? namebook) (empty? typeofbook) (empty? year) (empty? numberofexample))
                      ""
                      (try

                        (db/insert-book namebook typeofbook year numberofexample author)
                        [:p {:style "color:green; border-color: #000000;"} [:b (str "Uspesno ubacena knjiga " namebook "!!!")] ]
                        (catch Exception e
                          [:p {:style "color:red; border-color: #000000;"} [:b (str "Nije ubacena knjiga!!!" namebook )] ]
                          )))
                    ]]
                  ])
  )

; STRANA ZA UPDATE KNJIGE !!!!!!!!!!!!!!
(defn update-book [id]
  (layout/pagelayout "Biblioteka" (menu/menuapp)
                 [:br]
                 [:div {:class "container"}
                  [:div {:class "row"}
                   [:div {:class "col"}]
                   [:div {:class "col-6"}
                    [:h2 [:b "Izmena knjige" ]]
                    (try
                      (for [book (db/select-book-by-id id)]
                        [:div {:class "tab-content" }
                         [:div {:class "tab-pane active" :role "tabpanel"}
                          (f/form-to [:get (str "/updateThisBook/" id)]
                                     (anti-forgery-field)
                                     [:div {:class "form-group"}
                                      [:label {:for "nb"} [:b "Naziv knjige:"] ]
                                      [:input {:type "text" :name "namebook" :class "form-control" :id "nb" :value (:nazivpublikacije book)}]
                                      ]
                                     [:div {:class "form-group"}
                                      [:label {:for "tb"} [:b "Zanr knjige:"] ]
                                      [:input {:type "text" :name "typeofbook" :class "form-control" :id "tb" :value (:tippublikacije book)} ]
                                      ]

                                     [:div {:class "form-group"}
                                      [:label {:for "y"} [:b "Godina izdanja:"]]
                                      [:input {:type "text" :name "year" :class "form-control" :id "y" :value (:godinaizdanja book)} ]
                                      ]

                                     [:div {:class "form-group"}
                                      [:label {:for "ne"}[:b "Broj primeraka:"]]
                                      [:input {:type "text" :name "numberofexample" :class "form-control" :id "ne" :value (:brojprimeraka book)}]
                                      ]
                                     [:div {:class "form-group"}
                                      [:label {:for "at"} [:b "Autor:"]]
                                      [:input {:type "text" :name "control" :class "form-control" :id "at" :readonly "true" :style "border-color: #000000;" :value (:prezimeautora book)} ]
                                      [:label {:for "na"} [:b "Zameni autora:"]]
                                      [:select {:name "author" :id "na" :class "form-control"}
                                       (try
                                         (for [book (db/select-authors-order-surname)]
                                           [:option {:value (:autorid book)} (:prezimeautora book)]
                                           )

                                         (catch Exception e
                                           [:b "Dogodila se greska prilikom ucitavanja autora!"]))
                                       ]
                                      ]

                                     [:input {:type "submit"
                                              :value "Izmeni knjigu"
                                              :class "btn btn-primary"
                                              }]
                                     (h "   ")
                                     [:a {:href (str "/updateBook/" id) }[:input {:type "button"
                                                                                    :value "Resetuj sve vrednosti"
                                                                                    :class "btn btn-warning"}]])]])
                      (catch Exception e
                        [:p {:style "color:red; border-color: #000000;"} [:b (str "Doslo je do greske prilikom ucitavanja autora!!!")] ]
                        ))

                    ]
                   [:div {:class "col"}]]])
  )

(defn update-this-book [id namebook typeofbook year numberofexample author]
  (layout/pagelayout "Biblioteka" (menu/menuapp)
                 [:br]
                 [:div {:class "container"}
                  [:div {:class "row"}
                   [:div {:class "col"}]
                   [:div {:class "col-6"}
                    [:h2 [:b "Izmena knjige" ]]
                    (try
                      (for [book (db/select-book-by-id id)]
                        [:div {:class "tab-content" }
                         [:div {:class "tab-pane active" :role "tabpanel"}
                          (f/form-to [:get (str "/updateThisBook/" id)]
                                     (anti-forgery-field)
                                     [:div {:class "form-group"}
                                      [:label {:for "nb"} [:b "Naziv knjige:"] ]
                                      [:input {:type "text" :name "namebook" :class "form-control" :id "nb" :value namebook}]
                                      (if (or (nil? namebook) (empty? namebook) )
                                        [:label {:for "nb" :style"color:red"} [:b "Morate da unesete naziv knjige!!!"] ]
                                        )
                                      ]

                                     [:div {:class "form-group"}
                                      [:label {:for "tb"} [:b "Zanr knjige:"] ]
                                      [:input {:type "text" :name "typeofbook" :class "form-control" :id "tb" :value typeofbook}]
                                      (if (or (nil? typeofbook) (empty? typeofbook) )
                                        [:label {:for "tb" :style"color:red"} [:b "Morate da unesete zanr knjige!!!"] ]
                                        )
                                      ]

                                     [:div {:class "form-group"}
                                      [:label {:for "y"} [:b "Godina izdanja:"] ]
                                      [:input {:type "text" :name "year" :class "form-control" :id "y" :value year} ]
                                      (if (or (nil? year) (empty? year))
                                        [:label {:for "y" :style"color:red"} [:b "Morate da unesete godinu izdanja!!!"] ]
                                        (try
                                          (Integer/parseInt year)
                                          [:label {:for "y" :style"color:red"} [:b ""] ]
                                          (catch Exception e
                                            [:label {:for "y" :style"color:red"} [:b "Morate da unesete broj!!!"] ])))
                                      ]

                                     [:div {:class "form-group"}
                                      [:label {:for "ne"} [:b "Broj primeraka:"] ]
                                      [:input {:type "text" :name "numberofexample" :class "form-control" :id "ne" :value numberofexample} ]
                                      (if (or (nil? numberofexample) (empty? numberofexample))
                                        [:label {:for "ne" :style"color:red"} [:b "Morate da unesete broj primeraka!!!"] ]
                                        (try
                                          (Integer/parseInt numberofexample)
                                          [:label {:for "ne" :style"color:red"} [:b ""] ]
                                          (catch Exception e
                                            [:label {:for "ne" :style"color:red"} [:b "Morate da unesete broj!!!"] ])))
                                      ]


                                     [:div {:class "form-group"}
                                      [:label {:for "na"} [:b "Autor:"]]
                                      [:select {:name "author" :id "na" :class "form-control"}
                                       (try
                                         (for [book (db/select-authors-order-surname)]
                                           [:option {:value (:autorid book)} (:prezimeautora book)]
                                           )

                                         (catch Exception e
                                           [:b "Dogodila se greska prilikom ucitavanja autora!"]))
                                       ]
                                      ]
                                     [:input {:type "submit"
                                              :value "Izmeni igraca"
                                              :class "btn btn-primary"
                                              }]
                                     (h "   ")
                                     [:a {:href (str "/updateBook/" id)}[:input {:type "button"
                                                                                   :value "Resetuj sve vrednosti"
                                                                                   :class "btn btn-warning"}]])]]
                        )
                      (catch Exception e
                        ))
                    ]
                   [:div {:class "col"}
                    (if (or (nil? namebook) (nil? typeofbook) (nil? year) (nil? numberofexample) (empty? namebook) (empty? typeofbook) (empty? year) (empty? numberofexample))
                      ""
                      (try
                        (db/update-book id namebook typeofbook year numberofexample author)
                        [:p {:style "color:green; border-color: #000000;"} [:b (str "Uspesno izmenjena knjiga " namebook "!!!")] ]
                        (catch Exception e
                          [:p {:style "color:red; border-color: #000000;"} [:b (str "Nije izmenjena knjiga!!!" namebook )] ]
                          )))
                    ]]
                  ])
  )





(defroutes app-routes
           (GET "/" [] (index))
           (GET "/showAuthors" [] (try (select-authors) (catch Exception e
                                                      (error-load-author e))))
           (GET "/insertAuthor" [] (insert-author-page))
           (GET "/insertNewAuthor" [name surname dateofbirth country mainbook]
             (insert-new-author-page name surname dateofbirth country mainbook))

           (GET "/deleteAuthor/:id" [id] (try
                                         (db/delete-author id)
                                         (ring/redirect "/showAuthors")
                                         (catch Exception e
                                           (ring/redirect "/showAuthors")
                                           ) (finally)))
           (GET "/deleteAuthor" [] (try (select-authors-for-delete) (catch Exception e
                                                                  )))
           (GET "/deleteThisAuthor" [surname] (try (select-authors-which-delete surname) (catch Exception e)))
           (GET "/updateAuthor/:id" [id] (try (update-author id)  (catch Exception e)))
           (GET "/updateThisAuthor/:id" [id name surname dateofbirth country mainbook]
             (try (update-this-author id name surname dateofbirth country mainbook)  (catch Exception e


                                                                                                                                                                           )))


           (GET "/showBooks" [] (try (select-books) (catch Exception e
                                                          )))
           (GET "/showAllBooks" [surnameAuthor] (try (select-all-books surnameAuthor) (catch Exception e
                                                                             )))
           (GET "/showAllTheBooks/:surnameAuthor" [surnameAuthor] (try (select-all-the-books surnameAuthor) (catch Exception e
                                                                                         )))


           (GET "/insertBook" [] (try
                                     (insert-book)
                                     (catch Exception e
                                       )))
           (GET "/insertNewBook" [namebook typeofbook year numberofexample author] (try
                                                                                    (insert-new-book namebook typeofbook year numberofexample author)
                                                                                    (catch Exception e
                                                                                      )))

           (GET "/deleteBook/:id/:surnameAuthor" [id surnameAuthor] (try
                                                              (db/delete-book id)
                                                              (ring/redirect (str "/showAllTheBooks/" surnameAuthor))
                                                              (catch Exception e
                                                                (ring/redirect (str "/showAllTheBooks/" surnameAuthor))
                                                                ) (finally )))
           (GET "/updateBook/:id" [id] (try
                                           (update-book id)
                                           (catch Exception e
                                             )))
           (GET "/updateThisBook/:id" [id namebook typeofbook year numberofexample author] (try
                                                                                           (update-this-book id namebook typeofbook year numberofexample author)
                                                                                           (catch Exception e
                                                                                             )))


           (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))




