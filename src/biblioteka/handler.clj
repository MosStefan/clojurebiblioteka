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

; STRANA ZA DODAVANJE NOVE KNJIGE

(defn empty-name []
  [:label {:for "n" :style "color:red"} [:b "Morate da unesete naziv knjige!!!"]]
  )
(defn empty-typeofpub []
  [:label {:for "t" :style "color:red"} [:b "Morate da unesete tip publikacije!!!"]]
  )
(defn empty-author []
  [:label {:for "a" :style "color:red"} [:b "Morate da unesete ime autora!!!"]]
  )
(defn empty-year []
  [:label {:for "y" :style "color:red"} [:b "Morate da unesete godinu izdanja!!!"]]
  )
(defn empty-numberofexample []
  [:label {:for "ni" :style "color:red"} [:b "Morate da unesete broj primeraka!!!"]]
  )
(defn year-must-number []
  [:label {:for "ym" :style "color:red"} [:b "Morate da unesete broj za godinu izdanja!!!"]]
  )

(defn example-must-number []
  [:label {:for "en" :style "color:red"} [:b "Morate da unesete broj za primerak!!!"]]
  )


(defn exist-name-of-book [name]
  [:label {:style "color:red; border-color: #000000;"} [:b (str "Nije ubacena knjiga " name ", jer vec postoji u bazi!!!")]]
  )

(defn success-insert-book [name]
  [:p {:style "color:green; border-color: #000000;"} [:b (str "Uspesno ubacena knjiga " name "!!!")]])
(defn success-update-book [name]
  [:p {:style "color:green; border-color: #000000;"} [:b (str "Uspesno izmenjena knjiga " name "!!!")]])
(defn exist-name-of-book-update [name]
  [:label {:style "color:red; border-color: #000000;"} [:b (str "Nije izmenjena knjiga " name "!!!")]])

(defn empty-name-book []
  [:label {:for "na" :style "color:red"} [:b "Morate da unesete naziv knjige!!!"]]
  )
(defn insert-new-book-page [name typepublication year nameauthor numberofexample]
  (layout/pagelayout "Biblioteka" (menu/menuapp)
                [:br]
                [:div {:class "container"}
                 [:div {:class "row"}
                  [:div {:class "col"}]
                  [:div {:class "col-6"}
                   [:h3 [:b "Unos nove knjige"]]
                   [:div {:class "tab-content"}
                    [:div {:class "tab-pane active" :role "tabpanel"}
                     (f/form-to [:post "/insertNewBook"]
                                (anti-forgery-field)
                                [:div {:class "form-group"}
                                 [:label {:for "n"} [:b "Naziv knjige:"]]
                                 [:input {:type "text" :name "name" :class "form-control" :id "n" :value name}]
                                 (if (or (nil? name) (empty? name))
                                   (empty-name)
                                   )
                                 ]
                                [:div {:class "form-group"}
                                 [:label {:for "t"} [:b "Tip publikacije:"]]
                                 [:select {:name "typepublication" :id "t" :class "form-control" :value typepublication}
                                  [:option {:value ""} ""]
                                  [:option {:value "Drama"} "Drama"]
                                  [:option {:value "Fantastika"} "Fantastika"]
                                  [:option {:value "Novela"} "Novela"]
                                  [:option {:value "Triler"} "Triler"]
                                  [:option {:value "Detektivski roman"} "Detektivski roman"]]
                                 (if (or (nil? typepublication) (empty? typepublication))
                                   (empty-typeofpub)
                                   )
                                 ]


                                [:div {:class "form-group"}
                                 [:label {:for "m"} [:b "Godina izdanja:"]]
                                 [:input {:type "text" :name "year" :class "form-control" :id "y" :value year}]
                                 (if (or (nil? year) (empty? year))
                                   (empty-year)
                                   (try
                                     (Integer/parseInt year)
                                     [:label {:for "y" :style "color:red"} [:b ""]]
                                     (catch Exception e
                                       (year-must-number))))
                                 ]

                                [:div {:class "form-group"}
                                 [:label {:for "a"} [:b "Ime autora:"]]
                                 [:input {:type "text" :name "nameauthor" :class "form-control" :id "a" :value nameauthor}]
                                 (if (or (nil? nameauthor) (empty? nameauthor))
                                   (empty-author)
                                   )
                                 ]

                                [:div {:class "form-group"}
                                 [:label {:for "ne"} [:b "Broj primeraka:"]]
                                 [:input {:type "text" :name "numberofexample" :class "form-control" :id "ne" :value numberofexample}]
                                 (if (or (nil? numberofexample) (empty? numberofexample))
                                   (empty-numberofexample)
                                   (try
                                     (Integer/parseInt numberofexample)
                                     [:label {:for "ne" :style "color:red"} [:b ""]]
                                     (catch Exception e
                                       (example-must-number))))
                                 ]


                                [:input {:type  "submit"
                                         :value "Ubaci novu knjigu"
                                         :class "btn btn-success"
                                         }]
                                (h "   ")
                                [:a {:href "/insertBook"} [:input {:type  "button"
                                                                   :value "Resetuj vrednosti"
                                                                   :class "btn btn-warning"}]])]]]
                  [:div {:class "col"}
                   (if (or (nil? name) (nil? typepublication) (nil? year) (nil? nameauthor) (nil? numberofexample) (empty? name) (empty? typepublication) (empty? year) (empty? nameauthor) (empty? numberofexample))
                     ""
                     (try
                       (if (empty? (db/select-book-by-name name))
                         (try
                           (db/insert-book name typepublication year nameauthor numberofexample)
                           (success-insert-book name)


                           (catch Exception e
                             (exist-name-of-book name )
                             ))
                         )

                       (catch Exception e
                         [:p {:style "color:red; border-color: #000000;"} [:b (str "Nije ubacena knjiga!!!" name)]]

                         )))
                   ]]
                 ])
  )

(defn insert-book-page []
  (layout/pagelayout "Biblioteka" (menu/menuapp)
                [:br]
                [:div {:class "container"}
                 [:div {:class "row"}
                  [:div {:class "col"}]
                  [:div {:class "col-6"}
                   [:h3 [:b "Unos nove knjige"]]
                   [:div {:class "tab-content"}
                    [:div {:class "tab-pane active" :role "tabpanel"}
                     (f/form-to [:post "/insertNewBook"]
                                (anti-forgery-field)
                                [:div {:class "form-group"}
                                 [:label {:for "n"} [:b "Naziv knjige:"]]
                                 [:input {:type "text" :name "name" :class "form-control" :id "n"}]
                                 ]

                                [:div {:class "form-group"}
                                 [:label {:for "t"} [:b "Zanr:"]]
                                 [:select {:name "typepublication" :id "t" :class "form-control"}
                                  [:option {:value ""} ""]
                                  [:option {:value "Drama"} "Drama"]
                                  [:option {:value "Fantastika"} "Fantastika"]
                                  [:option {:value "Novela"} "Novela"]
                                  [:option {:value "Triler"} "Triler"]
                                  [:option {:value "Detektivski roman"} "Detektivski roman"]]

                                 ]

                                [:div {:class "form-group"}
                                 [:label {:for "y"} [:b "Godina izdanja:"]]
                                 [:input {:type "text" :name "year" :class "form-control" :id "y"}]

                                 ]

                                [:div {:class "form-group"}
                                 [:label {:for "a"} [:b "Ime autora:"]]
                                 [:input {:type "text" :name "nameauthor" :class "form-control" :id "a"}]

                                 ]

                                [:div {:class "form-group"}
                                 [:label {:for "ne"} [:b "Broj primeraka:"]]
                                 [:input {:type "text" :name "numberofexample" :class "form-control" :id "ne"}]

                                 ]

                                [:input {:type  "submit" :read-only "false"
                                         :value "Ubaci novu knjigu"
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

;STRANA ZA PRIKAZ SVIH KNJIGA
(defn show-books []
  (try
    [:br]
    [:br]
    [:div {:class "container"}
     [:div {:class "row"}
      [:div {:class "col-9"}
       [:h2 [:b "Prikaz svih knjiga" ]]
       [:br]
       [:br]
       [:table {:class "table table-dark"}
        [:thead
         [:tr
          [:th "Naziv knjige"]
          [:th "Zanr"]
          [:th "Godina izdanja"]
          [:th "Autor knjige"]
          [:th "Broj primeraka"]
          [:th "Obrisi knjigu"]
          [:th "Izmeni knjigu"]
          ]]

        (into [:tbody]
              (for [book (db/select-books)]
                [:tr [:td (:nazivpublikacije book)]
                 [:td (:tippublikacije book)]
                 [:td (:godinaizdanja book)]
                 [:td (:imeautora book)]
                 [:td (:brojprimeraka book)]
                 [:td [:a {:href (str "/deleteBook/" (h (:publikacijaid book))) :style "color:red;"} "Obrisi "]]
                 [:td [:a {:href (str "/updateBook/" (h (:publikacijaid book))) :style "color:red;"} "Izmeni "]]]))]
       ]
      [:div {:class "col"}]]]
    (catch Exception e
      (throw (Exception. e))))
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
(defn error_message [e]
  [:br] [:br] [:p [:b "Dogodila se greska prilikom ucitavanja nove knjige!"]]
  )

(defn error_sql [e]
  [:br] [:br] [:p [:b "Nema knjiga!"]]
  )

(defn error-load-book [error]
  (layout/pagelayout "Biblioteka" (menu/menuapp)
                [:br] [:br] [:p [:b "Dogodila se greska prilikom ucitavanja knjige!"]]
                )
  )

; STRANA ZA BRISANJE KNJIGE
(defn show-books-for-delete []

  [:div {:class "container"}
   [:div {:class "row"}
    [:div {:class "col-6"}
     (f/form-to [:get "/deleteThisBook"]
                (anti-forgery-field)
                [:div {:class "form-group"}
                 [:label {:for "na"} [:b "Unesite naziv knjige :"]]
                 [:input {:type "text" :name "nameBook" :class "form-control" :id "na"}]
                 ]
                [:input {:type  "submit"
                         :value "Obrisi knjigu"
                         :class "btn btn-danger"
                         }]
                (h "   ")
                [:a {:href "/deleteBook"} [:input {:type  "button"
                                                   :value "Resetuj vrednost"
                                                   :class "btn btn-warning"}]]
                )

     ]
    [:div {:class "col-10"}
     [:h2 [:b "Prikaz svih knjiga"]]
     [:br]
     [:table
      [:thead
       [:tr
        [:th "Naziv knjige"]
        [:th "Zanr"]
        [:th "Godina izdanja"]
        [:th "Autor"]
        [:th "Broj primeraka"]
        ]]

      (into [:tbody]
            (try
              (for [book (db/select-books)]
                [:tr [:td (:nazivpublikacije book)]
                 [:td (:tippublikacije book)]
                 [:td (:godinaizdanja book)]
                 [:td (:imeautora book)]
                 [:td (:brojprimeraka book)]])
              (catch Exception e
                [[:b "Dogodila se greska prilikom ucitavanja knjiga!"]]
                ))
            )]
     ]
    [:div {:class "col"}]]]

  )
(defn select-books-for-delete []
  (try
    (layout/pagelayout "Biblioteka" (menu/menuapp)
                  [:br]
                  [:br]
                  [:br]
                  [:br]
                  (show-books-for-delete))
    (catch Exception e
      (throw (Exception. e))))
  )

(defn show-book-which-delete [nameBook]
  [:div {:class "container"}
   [:div {:class "row"}
    [:div {:class "col-6"}
     (if (or (nil? nameBook) (empty? nameBook))
       ""
       (try
         (if (empty? (db/select-book-by-name nameBook))
           [:p {:style "color:red; border-color: #000000;"} [:b (str "Knjiga " nameBook " koju zelite da obrisete ne postoji!!!")]]
           (try
             (db/delete-book-by-name nameBook)
             [:p {:style "color:green; border-color: #000000;"} [:b (str "Knjiga " nameBook " uspesno obrisana!!!")]]
             (catch Exception e
               [:p {:style "color:red; border-color: #000000;"} [:b (str "Knjiga " nameBook " koju zelite da obrisete ne postoji!!!")]]
               )))


         (catch Exception e
           [:p {:style "color:red; border-color: #000000;"} [:b (str "Nije obrisana knjiga " nameBook "!!!")]]

           )))
     (f/form-to [:get "/deleteThisBook"]
                (anti-forgery-field)
                [:div {:class "form-group"}
                 [:label {:for "na"} [:b "Unesite naziv knjige :"]]
                 [:input {:type "text" :name "nameBook" :class "form-control" :id "na" :value nameBook}]
                 (if (or (nil? nameBook) (empty? nameBook))
                   (empty-name-book)
                   )
                 ]
                [:input {:type  "submit"
                         :value "Obrisi knjigu"
                         :class "btn btn-primary"
                         }]
                (h "   ")
                [:a {:href "/deleteBook"} [:input {:type  "button"
                                                   :value "Resetuj vrednost"
                                                   :class "btn btn-primary"}]]
                )

     ]
    [:div {:class "col-10"}
     [:h2 [:b "Prikaz svih knjiga" ]]
     [:br]
     [:table {:class "table table-dark"}
      [:thead
       [:tr
        [:th "Naziv knjige"]
        [:th "Zanr"]
        [:th "Godina izdanja"]
        [:th "Autor"]
        [:th "Broj primeraka"]

        ]]

      (into [:tbody]
            (try
              (for [book (db/select-books)]
                [:tr [:td (:nazivpublikacije book)]
                 [:td (:tippublikacije book)]
                 [:td (:godinaizdanja book)]
                 [:td (:imeautora book)]
                 [:td (:brojprimeraka book)]])
              (catch Exception e
                [[:b "Dogodila se greska prilikom ucitavanja knjiga!"]]
                ))
            )]
     ]
    [:div {:class "col-8"}

     ]]]
  )
(defn select-book-which-delete [namebook]
  (try
    (layout/pagelayout "Biblioteka" (menu/menuapp)
                  [:br]
                  [:br]
                  [:br]
                  [:br]
                  (show-book-which-delete namebook))
    (catch Exception e
      (throw (Exception. e))))
  )

; STRANA ZA IZMENU KNJIGE
(defn update-book [id]
  (layout/pagelayout "Biblioteka" (menu/menuapp)
                [:br]
                [:div {:class "container"}
                 [:div {:class "row"}
                  [:div {:class "col"}]
                  [:div {:class "col-6"}
                   [:h3 [:b "Izmena podataka izabrane knjige"]]
                   (try
                     (for [book (db/select-book-by-id id)]
                       [:div {:class "tab-content"}
                        [:div {:class "tab-pane active" :role "tabpanel"}
                         (f/form-to [:get (str "/updateThisBook/" id)]
                                    (anti-forgery-field)
                                    [:div {:class "form-group"}
                                     [:label {:for "n"} [:b "Naziv knjige:"]]
                                     [:input {:type "text" :name "name" :class "form-control" :id "n" :value (:nazivpublikacije book)}]

                                     ]
                                    [:div {:class "form-group"}
                                     [:label {:for "changeType"} [:b "Zanr:"]]
                                     [:input {:type "text" :name "change" :class "form-control" :id "changeType" :readonly "true" :style "border-color: #000000;" :value (:tippublikacije book)} ]
                                     [:label {:for "t"} [:b "Zameni zanr knjige:"]]
                                     [:select {:name "typepublication" :id "t" :class "form-control" }
                                      [:option {:value ""} ""]
                                      [:option {:value "Drama"} "Drama"]
                                      [:option {:value "Fantastika"} "Fantastika"]
                                      [:option {:value "Novela"} "Novela"]
                                      [:option {:value "Triler"} "Triler"]
                                      [:option {:value "Detektivski roman"} "Detektivski roman"]]

                                     ]

                                    [:div {:class "form-group"}
                                     [:label {:for "y"} [:b "Godina izdanja:"]]
                                     [:input {:type "text" :name "year" :class "form-control" :id "y" :value (:godinaizdanja book)}]

                                     ]

                                    [:div {:class "form-group"}
                                     [:label {:for "a"} [:b "Autor knjige:"]]
                                     [:input {:type "text" :name "nameauthor" :class "form-control" :id "a" :value (:imeautora book)}]

                                     ]

                                    [:div {:class "form-group"}
                                     [:label {:for "ne"} [:b "Broj primeraka:"]]
                                     [:input {:type "text" :name "numberofexample" :class "form-control" :id "ne" :value (:brojprimeraka book)}]

                                     ]

                                    [:input {:type  "submit"
                                             :value "Izmeni knjigu"
                                             :class "btn btn-primary"
                                             }]
                                    (h "   ")
                                    [:a {:href (str "/updateBook/" id)} [:input {:type  "button"
                                                                                 :value "Resetuj vrednosti"
                                                                                 :class "btn btn-warning"}]])]])
                     (catch Exception e
                       [:p {:style "color:red; border-color: #000000;"} [:b (str "Doslo je do greske prilikom ucitavanja date knjige!!!")]]
                       ))

                   ]
                  [:div {:class "col"}]]])
  )

(defn update-this-book [id name typepublication year nameauthor numberofexample]
  (layout/pagelayout "Biblioteka" (menu/menuapp)
                [:br]
                [:div {:class "container"}
                 [:div {:class "row"}
                  [:div {:class "col"}]
                  [:div {:class "col-6"}
                   [:h3 [:b "Izmena podataka izabrane knjige"]]
                   [:div {:class "tab-content"}
                    [:div {:class "tab-pane active" :role "tabpanel"}
                     (f/form-to [:get (str "/updateThisBook/" id)]
                                (anti-forgery-field)
                                [:div {:class "form-group"}
                                 [:label {:for "n"} [:b "Naziv knjige:"]]
                                 [:input {:type "text" :name "name" :class "form-control" :id "n" :value name}]
                                 (if (or (nil? name) (empty? name))
                                   (empty-name)
                                   )
                                 ]
                                [:div {:class "form-group"}
                                 [:label {:for "changeType"} [:b "Zanr:"]]
                                 [:input {:type "text" :name "change" :class "form-control" :id "changeType" :readonly "true" :style "border-color: #000000;" :value typepublication}]
                                 [:label {:for "k"} [:b "Zameni zanr knjige:"]]

                                 [:select {:name "typepublication" :id "t" :class "form-control" :value typepublication}
                                  [:option {:value ""} ""]
                                  [:option {:value "Drama"} "Drama"]
                                  [:option {:value "Fantastika"} "Fantastika"]
                                  [:option {:value "Novela"} "Novela"]
                                  [:option {:value "Triler"} "Triler"]
                                  [:option {:value "Detektivski roman"} "Detektivski roman"]]
                                 (if (or (nil? typepublication) (empty? typepublication))
                                   (empty-typeofpub)
                                   )
                                 ]


                                [:div {:class "form-group"}
                                 [:label {:for "y"} [:b "Godina izdanja:"]]
                                 [:input {:type "text" :name "year" :class "form-control" :id "y" :value year}]
                                 (if (or (nil? year) (empty? year))
                                   (empty-year)
                                   (try
                                     (Integer/parseInt year)
                                     [:label {:for "y" :style "color:red"} [:b ""]]
                                     (catch Exception e
                                       (year-must-number))))
                                 ]

                                [:div {:class "form-group"}
                                 [:label {:for "a"} [:b "Autor knjige:"]]
                                 [:input {:type "text" :name "nameauthor" :class "form-control" :id "a" :value nameauthor}]
                                 (if (or (nil? nameauthor) (empty? nameauthor))
                                   (empty-author)
                                   )
                                 ]

                                [:div {:class "form-group"}
                                 [:label {:for "ne"} [:b "Broj primeraka"]]
                                 [:input {:type "text" :name "numberofexample" :class "form-control" :id "ne" :value numberofexample}]
                                 (if (or (nil? numberofexample) (empty? numberofexample))
                                   (empty-numberofexample)
                                   (try
                                     (Integer/parseInt numberofexample)
                                     [:label {:for "p" :style "color:red"} [:b ""]]
                                     (catch Exception e
                                       (example-must-number))))
                                 ]



                                [:input {:type  "submit"
                                         :value "Izmeni knjigu"
                                         :class "btn btn-primary"
                                         }]
                                (h "   ")
                                [:a {:href (str "/updateBook/" id)} [:input {:type  "button"
                                                                             :value "Resetuj vrednosti"
                                                                             :class "btn btn-warning"}]])]]]
                  [:div {:class "col"}
                   (if (or (nil? name) (nil? typepublication) (nil? year) (nil? nameauthor) (nil? numberofexample) (empty? name) (empty? typepublication) (empty? year) (empty? nameauthor) (empty? numberofexample))
                     ""
                     (try
                       (if (or (empty? (db/select-book-by-name name)) (= (count (db/select-book-by-name name)) 1))
                         (try
                           (db/update-book id name typepublication year nameauthor numberofexample)
                           (success-update-book name)


                           (catch Exception e
                             (exist-name-of-book-update name)))
                         )

                       (catch Exception e
                         [:p {:style "color:red; border-color: #000000;"} [:b (str "Nije izmenjena knjiga!!!" name)]]

                         )))
                   ]]
                 ])
  )





(defroutes app-routes
           (GET "/" [] (index))
           (GET "/showBooks" [] (try (select-books) (catch Exception e
                                                      (error-load-book e))))
           (GET "/insertBook" [] (insert-book-page))
           (POST "/insertNewBook" [name typepublication year nameauthor numberofexample]
             (insert-new-book-page name typepublication year nameauthor numberofexample))

           (GET "/deleteBook/:id" [id] (try
                                         (db/delete-book id)
                                         (ring/redirect "/showBooks")
                                         (catch Exception e
                                           (ring/redirect "/showBooks")
                                           ) (finally)))
           (GET "/deleteBook" [] (try (select-books-for-delete) (catch Exception e
                                                                  )))
           (GET "/deleteThisBook" [nameBook] (try (select-book-which-delete nameBook) (catch Exception e)))
           (GET "/updateBook/:id" [id] (try (update-book id )  (catch Exception e)))
           (GET "/updateThisBook/:id" [id name typepublication year nameauthor numberofexample] (try (update-this-book id name typepublication year nameauthor numberofexample)  (catch Exception e
                                                                                                                                                                           )))
           (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))




