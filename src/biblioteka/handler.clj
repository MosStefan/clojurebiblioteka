(ns biblioteka.handler
  (:use clj-pdf.core
        ring.util.io)
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
            [biblioteka.models.db :as db])
  (require [propertea.core :refer (read-properties)]))


 (defn anti-forgery-field []
(f/hidden-field "__anti-forgery-token" *anti-forgery-token*))

(def props (read-properties "src/autori.properties"))

(defn show-award
  []

  (piped-input-stream
    (fn [output-stream]
      (pdf
        [{:title (str "Biblioteka-promocije")
          :orientation :landscape
          :size :a5
          :author "Stefan"
          :register-system-fonts true
          }
         [:heading {:style {:size 48 :color [100 40 150] :align :center}} "Promocija"]
         [:spacer]
         [:spacer]
         [:spacer]
         [:spacer]
         [:spacer]

         [:paragraph {:style :bold
                      :size 24
                      :family :helvetica
                      :align :center
                      :color [0 234 123]} "Cestitamo, za pokazano odlicno znanje darujemo vam mesec dana besplatne clanarine!!!"]


         ]
        output-stream))))

(defn award-pdf []
  "show price"

  {:headers {"Content-Type" "application/pdf"}
   :body (show-award )})

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
(defn empty-member-name []
  [:label {:for "n" :style "color:red"} [:b "Morate da unesete ime novog clana!!!"]]
  )

(defn empty-member-jmbg []
  [:label {:for "jm" :style "color:red"} [:b "Morate da unesete jmbg novog clana(JMBG mora imati tacno 13 cifara)!!!"]]
  )

(defn empty-surname []
  [:label {:for "s" :style "color:red"} [:b "Morate da unesete prezime autora!!!"]]
  )
(defn empty-member-surname []
  [:label {:for "s" :style "color:red"} [:b "Morate da unesete prezime novog clana!!!"]]
  )






(defn show-price [x]

  (cond

    (= x 5)   [:p {:style "color:green; border-color: #000000;"} [:b (str "Cestitamo sve ste pogodili!!!")] [:a {:href "/award" :target "_blank"} [:input {:type  "button"
                                                                                                                                            :value "Cestitamo ostvarili ste mesecni popust,potvrdite klikom na dugme!!!"
                                                                                                                                            :class "btn btn-success"}]]]
    (= x 4)  [:p {:style "color:green; border-color: #000000;"} [:b (str "Blizu ste, vise srece drugi put!!!")] ]
    :else [:p {:style "color:green; border-color: #000000;"} [:b (str "Morate malo obnoviti gradivo!!!")] ]))
(defn empty-country []
  [:label {:for "c" :style "color:red"} [:b "Morate da unesete zemlju porekla!!!"]]
  )

(defn empty-mail []
  [:label {:for "ml" :style "color:red"} [:b "Morate da unesete ispravan mail(*koji sadrzi @)!!!"]]
  )
(defn empty-phone []
  [:label {:for "ph" :style "color:red"} [:b "Morate da unesete broj telefona!!!"]]
  )
(defn empty-dateofbirth []
  [:label {:for "dbr" :style "color:red"} [:b "Morate da unesete datum rodjenja!!!"]]
  )

(defn empty-datein []
  [:label {:for "datetake" :style "color:red"} [:b "Morate da unesete datum uzimanja knjige!!!"]]
  )

(defn empty-dateout []
  [:label {:for "datereturn" :style "color:red"} [:b "Morate da unesete datum vracanjaknjige!!!"]]
  )

(defn empty-dateofentry []
  [:label {:for "den" :style "color:red"} [:b "Morate da unesete datum uclanjenja!!!"]]
  )
(defn empty-mainbook []
  [:label {:for "mb"  :style "color:red"} [:b "Morate da unesete najznacajnije delo!!!"]]
  )

( defn empty-name-of-book []
[:label {:for "nb"  :style "color:red"} [:b "Morate da unesete naziv knjige!!!"]]
)
(defn dateofbirth-format []
  [:label {:for "ym" :style "color:red"} [:b "Morate da unesete ispravan format za datum rodjenja: YYYY-MM-dd!!! "]]
  )

(defn dateofin-format []
  [:label {:for "datet" :style "color:red"} [:b "Morate da unesete ispravan format za datum uzimanja knjige: YYYY-MM-dd!!! "]]
  )

(defn dateofout-format []
  [:label {:for "dater" :style "color:red"} [:b "Morate da unesete ispravan format za datum vracanja knjige: YYYY-MM-dd!!! "]]
  )

(defn dateofentry-format []
  [:label {:for "enr" :style "color:red"} [:b "Morate da unesete ispravan format za datum uclanjenja: YYYY-MM-dd!!! "]]
  )

(defn year-must-number []
  [:label {:for "en" :style "color:red"} [:b "Morate da unesete broj za godinu!!!"]]
  )


(defn exist-name-of-author [surname]
  [:label {:style "color:red; border-color: #000000;"} [:b (str "Nije ubacen autor " surname ", jer vec postoji u bazi!!!")]]
  )

(defn exist-name-of-member [surname]
  [:label {:style "color:red; border-color: #000000;"} [:b (str "Nije ubacen novi clan " surname ", jer vec postoji u bazi!!!")]]
  )

(defn exist-record-of-member [surname]
  [:label {:style "color:red; border-color: #000000;"} [:b (str "NEE RADIII!!!Nije ubacen novi evidencija za clana " surname ", jer vec postoji u bazi!!!")]]
  )


(defn success-insert-author [surname]
  [:p {:style "color:green; border-color: #000000;"} [:b (str "Uspesno ubacen autor " surname "!!!")]])
(defn success-insert-new-member [surname]
  [:p {:style "color:green; border-color: #000000;"} [:b (str "Uspesno ubacen novi clan " surname "!!!")]])

(defn success-insert-new-record-for-member [surname]
  [:p {:style "color:green; border-color: #000000;"} [:b (str "Uspesno ubacen evidencija za clana " surname "-a!!!")]])


(defn success-update-author [surname]
  [:p {:style "color:green; border-color: #000000;"} [:b (str "Uspesno izmenjen autor " surname "!!!")]])
(defn exist-name-of-author-update [surname]
  [:label {:style "color:red; border-color: #000000;"} [:b (str "Nije izmenjen autor " surname "!!!")]])

(defn empty-surname-author []
  [:label {:for "na" :style "color:red"} [:b "Morate da unesete prezime autora!!!"]]
  )

(defn empty-surname-member []
  [:label {:for "nmemb" :style "color:red"} [:b "ZASSSTOOO!Morate da unesete prezime clana biblioteke!!!"]]
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
                         :value "Prikazi sve knjige"
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
        [:th "Status"]
        [:th "Obrisi knjigu"]
        [:th "Izmeni knjigu"]
        ]]

      (into [:tbody]
            (try
              (for [book (db/select_books surnameAuthor)]
                [:tr [:td (:nazivpublikacije book)]
                 [:td (:nazivzanra book)]
                 [:td (:godinaizdanja book)]
                 [:td (:status book)]
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
        [:th {:style "border-style: solid;  border-color: #000000;    background-color: red;    color: black;"}"Status"  ]
        [:th {:style "border-style: solid;  border-color: #000000;    background-color: red;    color: black;"}"Obrisi knjigu"  ]
        [:th {:style "border-style: solid;  border-color: #000000;    background-color: red;    color: black;"} "Izmeni knjigu"]
        ]]

      (into [:tbody]
            (try
              (for [book (db/select_books surnameAuthor)]
                [:tr [:td {:style "border-style: solid;  border-color: #000000;    background-color: yellow;    color: black;"} (:nazivpublikacije book)]
                 [:td {:style "border-style: solid;  border-color: #000000;    background-color: yellow;    color: black;"} (:nazivzanra book)]
                 [:td {:style "border-style: solid;  border-color: #000000;    background-color: yellow;    color: black;"} (:godinaizdanja book)]
                 [:td {:style "border-style: solid;  border-color: #000000;    background-color: yellow;    color: black;"} (:status book)]
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
                                  [:label {:for "zn"} [:b "Zanr knjige:"]]
                                  [:select {:name "genre" :id "zn" :class "form-control"}
                                   (try
                                     (for [zanr (db/select-genre-order-name)]
                                       [:option {:value (:zanrid zanr)} (:nazivzanra zanr)]
                                       )

                                     (catch Exception e
                                       [:b "Dogodila se greska prilikom ucitavanja autora!"]))
                                   ]
                                  ]

                                 [:div {:class "form-group"}
                                  [:label {:for "y"} [:b "Godina izdanja:"]]
                                  [:input {:type "text" :name "year" :class "form-control" :id "y"}]

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
                                          :value "Ubaci knjigu "
                                          :class "btn btn-primary"
                                          }]
                                 (h "   ")
                                 [:input {:type "reset"
                                          :value "Resetuj vrednosti"
                                          :class "btn btn-warning"
                                          }])
                      ]]

                    ]
                   [:div {:class "col"}]]
                  ]
                 )
  )

(defn insert-new-book [namebook genre year author]
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
                                  [:label {:for "zn"} [:b "Zanr:"]]
                                  [:select {:name "genre" :id "zn" :class "form-control"}
                                   (try
                                     (for [zanr (db/select-genre-order-name)]
                                       [:option {:value (:zanrid zanr)} (:nazivzanra zanr)]
                                       )

                                     (catch Exception e
                                       [:b "Dogodila se greska prilikom ucitavanja zanra!"]))
                                   ]
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
                                          :value "Ubaci novu knigu"
                                          :class "btn btn-primary"
                                          }]
                                 (h "   ")
                                 [:a {:href "/insertBook"}[:input {:type "button"
                                                                     :value "Resetuj sve vrednosti"
                                                                     :class "btn btn-warning"}]])]]]
                   [:div {:class "col"}
                    (if (or (nil? namebook)  (nil? year)  (empty? namebook)  (empty? year))
                      ""
                      (try

                        (db/insert-book namebook genre year author)
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
                                      [:label {:for "zn"} [:b "Zanr knjige:"]]
                                      [:input {:type "text" :name "control" :class "form-control" :id "zn" :readonly "true" :style "border-color: #000000;" :value (:nazivzanra book)} ]
                                      [:label {:for "zn"} [:b "Zameni zanr:"]]
                                      [:select {:name "genre" :id "zn" :class "form-control"}
                                       (try
                                         (for [book (db/select-genre-order-name)]
                                           [:option {:value (:zanrid book)} (:nazivzanra book)]
                                           )

                                         (catch Exception e
                                           [:b "Dogodila se greska prilikom ucitavanja autora!"]))
                                       ]
                                      ]

                                     [:div {:class "form-group"}
                                      [:label {:for "y"} [:b "Godina izdanja:"]]
                                      [:input {:type "text" :name "year" :class "form-control" :id "y" :value (:godinaizdanja book)} ]
                                      ]

                                     [:div {:class "form-group"}
                                      [:label {:for "ne"}[:b "Status:"]]
                                      [:input {:type "text" :name "status" :class "form-control" :id "ne" :value (:status book)}]
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

(defn update-this-book [id namebook genre year status author]
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
                                      [:label {:for "zn"} [:b "Zanr knjige:"]]
                                      [:select {:name "genre" :id "zn" :class "form-control"}
                                       (try
                                         (for [book (db/select-genre-order-name)]
                                           [:option {:value (:zanrid book)} (:nazivzanra book)]
                                           )

                                         (catch Exception e
                                           [:b "Dogodila se greska prilikom ucitavanja autora!"]))
                                       ]
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
                                      [:label {:for "ne"} [:b "Status:"] ]
                                      [:input {:type "text" :name "status" :class "form-control" :id "ne" :value status}]
                                      (if (or (nil? status) (empty? status) )
                                        [:label {:for "ne" :style"color:red"} [:b "Morate da unesete status knjige(da li je dostupna ili ne)!!!"] ]
                                        )
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
                                              :value "Izmeni knjigu"
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
                    (if (or (nil? namebook)  (nil? year) (nil? status) (empty? namebook)  (empty? year) (empty? status))
                      ""
                      (try
                        (db/update-book id namebook genre year status author)
                        [:p {:style "color:green; border-color: #000000;"} [:b (str "Uspesno izmenjena knjiga " namebook "!!!")] ]
                        (catch Exception e
                          [:p {:style "color:red; border-color: #000000;"} [:b (str "Nije izmenjena knjiga!!!" namebook )] ]
                          )))
                    ]]
                  ])
  )

; STRANA ZA BRISANJE KNJIGE
(defn show-books-for-delete []

  [:div {:class "container"}
   [:div {:class "row"}
    [:div {:class "col-6"}
     (f/form-to [:get "/deleteThisBook"]
                (anti-forgery-field)
                [:div {:class "form-group"}
                 [:label {:for "nb"} [:b "Unesite naziv knjige :"]]
                 [:input {:type "text" :name "namebook" :class "form-control" :id "nb"}]
                 ]
                [:input {:type  "submit"
                         :value "Obrisi knjigu"
                         :class "btn btn-danger"
                         }]
                (h "   ")
                [:a {:href "/deleteBook"} [:input {:type  "button"
                                                     :value "Resetuj  sve vrednosti"
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
        [:th "Ime pisca knjige"]
        [:th "Prezime pisca knjige"]
        [:th "Godina izdanja"]
        [:th "Zanr knjige"]
        [:th "Status"]
        ]]

      (into [:tbody]
            (try
              (for [book (db/select_all_books)]
                [:tr [:td (:nazivpublikacije book)]
                 [:td (:imeautora book)]
                 [:td (:prezimeautora book)]
                 [:td (:godinaizdanja book)]
                 [:td (:nazivzanra book)]
                 [:td (:status book)]
                 ])
              (catch Exception e
                [[:b "Dogodila se greska prilikom ucitavanja knjige!"]]
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

(defn show-books-which-delete [namebook]
  [:div {:class "container"}
   [:div {:class "row"}
    [:div {:class "col-6"}
     (if (or (nil? namebook) (empty? namebook))
       ""
       (try
         (if (empty? (db/select-book-by-name namebook))
           [:p {:style "color:red; border-color: #000000;"} [:b (str "Knjiga " namebook " koju zelite da obrisete ne postoji!!!")]]
           (try
             (db/delete-book-by-name namebook)
             [:p {:style "color:green; border-color: #000000;"} [:b (str "Knjiga " namebook "  je uspesno obrisana!!!")]]
             (catch Exception e
               [:p {:style "color:red; border-color: #000000;"} [:b (str "Knjiga " namebook " koju zelite da obrisete ne postoji!!!")]]
               )))


         (catch Exception e
           [:p {:style "color:red; border-color: #000000;"} [:b (str "Nije obrisan knjiga " namebook "!!!")]]

           )))
     (f/form-to [:get "/deleteThisBook"]
                (anti-forgery-field)
                [:div {:class "form-group"}
                 [:label {:for "nb"} [:b "Unesite naziv knjige :"]]
                 [:input {:type "text" :name "namebook" :class "form-control" :id "nb" :value namebook}]
                 (if (or (nil? namebook) (empty? namebook))
                   (empty-name-of-book)
                   )
                 ]
                [:input {:type  "submit"
                         :value "Obrisi knjigu"
                         :class "btn btn-danger"
                         }]
                (h "   ")
                [:a {:href "/deleteBook"} [:input {:type  "button"
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
        [:th "Ime pisca knjige"]
        [:th "Prezime pisca knjige"]
        [:th "Godina izdanja knjige"]
        [:th "Zanr knjige"]
        [:th "Status"]


        ]]

      (into [:tbody]
            (try
              (for [book (db/select_all_books)]
                [:tr [:td (:nazivpublikacije book)]
                 [:td (:imeautora book)]
                 [:td (:prezimeautora book)]
                 [:td (:godinaizdanja book)]
                 [:td (:nazivzanra book)]
                 [:td (:status book)]
                 ])
              (catch Exception e
                [[:b "Dogodila se greska prilikom ucitavanja knjige!"]]
                ))
            )]
     ]
    [:div {:class "col-8"}

     ]]]
  )
(defn select-books-which-delete [namebook]
  (try
    (layout/pagelayout "Biblioteka" (menu/menuapp)
                       [:br]
                       [:br]
                       [:br]
                       [:br]
                       (show-books-which-delete namebook))
    (catch Exception e
      (throw (Exception. e))))
  )




; STRANA ZA UBACIVANJE EVIDENCIJE!!!!!!!!!!!!!!!!!!
(defn insert-data-for-member-page []
  (layout/pagelayout "Biblioteka" (menu/menuapp)
                     [:br]
                     [:div {:class "container"}
                      [:div {:class "row"}
                       [:div {:class "col"}]
                       [:div {:class "col-6"}
                        [:h3 [:b "Unos novog autora"]]
                        [:div {:class "tab-content"}
                         [:div {:class "tab-pane active" :role "tabpanel"}
                          (f/form-to [:get "/insertNewDataForMember"]
                                     (anti-forgery-field)

                                     [:div {:class "form-group"}
                                      [:label {:for "namemember"} [:b "Prezime clana:"]]
                                      [:select {:name "surnamemember" :id "namemember" :class "form-control"}
                                       (try
                                         (for [surnamemember (db/select-name-member-order-name)]
                                           [:option {:value (:jmbg surnamemember)} (:prezime surnamemember)]
                                           )

                                         (catch Exception e
                                           [:b "Dogodila se greska prilikom ucitavanja clana!"]))
                                       ]
                                      ]

                                     [:div {:class "form-group"}
                                      [:label {:for "namebook"} [:b "Naziv knjige:"]]
                                      [:select {:name "nameofbook" :id "namebook" :class "form-control"}
                                       (try
                                         (for [nameofbook (db/select-namebook-order-name)]
                                           [:option {:value (:idpublikacije nameofbook)} (:nazivpublikacije nameofbook)]
                                           )

                                         (catch Exception e
                                           [:b "Dogodila se greska prilikom ucitavanja publikacije!"]))
                                       ]
                                      ]

                                     [:div {:class "form-group"}
                                      [:label {:for "datetake"} [:b "Datum uzimanja knjige"]]
                                      [:input {:type "text" :name "dateoftake" :class "form-control" :id "datetake"}]

                                      ]

                                     [:div {:class "form-group"}
                                      [:label {:for "note"} [:b "Napomena"]]
                                      [:input {:type "text" :name "notes" :class "form-control" :id "note"}]
                                      ]






                                     [:input {:type  "submit" :read-only "false"
                                              :value "Ubaci novog evidenciju"
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





(defn insert-new-data-memberpage [surnamemember nameofbook dateoftake notes]
  (layout/pagelayout "Biblioteka" (menu/menuapp)
                     [:br]
                     [:div {:class "container"}
                      [:div {:class "row"}
                       [:div {:class "col"}]
                       [:div {:class "col-6"}
                        [:h3 [:b "Unos podataka o iznajmljivanju knjige"]]
                        [:div {:class "tab-content"}
                         [:div {:class "tab-pane active" :role "tabpanel"}
                          (f/form-to [:get "/insertNewDataForMember"]
                                     (anti-forgery-field)

                                     [:div {:class "form-group"}
                                      [:label {:for "namemember"} [:b "Prezime clana:"]]
                                      [:select {:name "surnamemember" :id "namemember" :class "form-control"}
                                       (try
                                         (for [surnamemember (db/select-name-member-order-name)]
                                           [:option {:value (:jmbg surnamemember)} (:prezime surnamemember)]
                                           )

                                         (catch Exception e
                                           [:b "Dogodila se greska prilikom ucitavanja clana!"]))
                                       ]
                                      ]

                                     [:div {:class "form-group"}
                                      [:label {:for "namebook"} [:b "Naziv knjige:"]]
                                      [:select {:name "nameofbook" :id "namebook" :class "form-control"}
                                       (try
                                         (for [nameofbook (db/select-namebook-order-name)]
                                           [:option {:value (:idpublikacije nameofbook)} (:nazivpublikacije nameofbook)]
                                           )

                                         (catch Exception e
                                           [:b "Dogodila se greska prilikom ucitavanja publikacije!"]))
                                       ]
                                      ]

                                     [:div {:class "form-group"}
                                      [:label {:for "datetake"} [:b "Datum uzimanja knjige:"]]
                                      [:input {:type "text" :name "dateoftake" :class "form-control" :id "datetake" :value dateoftake}]
                                      (if (or (nil? dateoftake) (empty? dateoftake))
                                        (empty-datein)
                                        (try
                                          (ftime/parse (ftime/formatter "YYYY-MM-dd") dateoftake)
                                          [:label {:for "datet" :style "color:red"} [:b ""]]
                                          (catch Exception e
                                            (dateofin-format))))
                                      ]


                                     [:div {:class "form-group"}
                                      [:label {:for "note"} [:b "Napomena*:"]]
                                      [:input {:type "text" :name "notes" :class "form-control" :id "note" :value notes}]
                                      ]


                                     [:input {:type  "submit"
                                              :value "Ubaci novog evidenciju o iznajmljivanju"
                                              :class "btn btn-success"
                                              }]
                                     (h "   ")
                                     [:a {:href "/insertNewDataMember"} [:input {:type  "button"
                                                                          :value "Resetuj  sve vrednosti"
                                                                          :class "btn btn-warning"}]])]]]
                       [:div {:class "col"}
                        (if (or (nil? dateoftake) (nil? notes)   (empty? dateoftake) (empty? notes))
                          ""
                          (try
                            (if (empty? (db/select-record-by-member surnamemember nameofbook))


                              (try
                                [:p {:style "color:red; border-color: #000000;"} [:b (str "Nece da moze ponovo!!!" surnamemember)]]
                                (db/insert-new-record-for-member surnamemember nameofbook dateoftake notes)
                                (db/update-name-book-for-member nameofbook)
                                (success-insert-new-record-for-member surnamemember)
                                (catch Exception e
                                  [:p {:style "color:red; border-color: #000000;"} [:b (str "ZAAAASTTTTOOOOO!!!!Nije ubacen nova evidencija za clana!!!" surnamemember)]]

                                  ))

                              )


                            (catch Exception e

                              [:p {:style "color:red; border-color: #000000;"} [:b (str "Nije ubacen!!!")]]
                              (exist-record-of-member surnamemember)
                              )))
                        ]]
                      ])
  )

;;PRIKAZIVANJE EVIDENCIJA
(defn show-records []
  [:div {:class "container"}
   [:div {:class "row"}
    [:div {:class "col-6"}
     (f/form-to [:get "/showAllRecords"]
                (anti-forgery-field)
                [:div {:class "form-group"}
                 [:label {:for "surmem"} [:b "Unesite prezime clana cija iznajmljene knjige zelite da prikazete:"] ]
                 [:input {:type "text" :name "surnameMember" :class "form-control" :id "surmem"}]
                 ]
                [:input {:type "submit"
                         :value "Prikazi knjige"
                         :class "btn btn-primary"
                         }]
                (h "   ")
                [:a {:href "/showRecords"}[:input {:type "button"
                                                 :value "Resetuj sve vrednosti"
                                                 :class "btn btn-warning"}]]
                )

     ]
    [:div {:class "col-10"}

     ]
    [:div {:class "col"}]]]
  )
(defn select-records []
  (try
    (layout/pagelayout "Biblioteka" (menu/menuapp)
                       [:br]
                       [:br]
                       [:br]
                       [:br]
                       [:br]
                       [:br]
                       (show-records))
    (catch Exception e
      (throw (Exception. e))))
  )

(defn show-all-records [surnameMember]
  [:div {:class "container"}
   [:div {:class "row"}
    [:div {:class "col-6"}
     (if (or (nil? surnameMember) (empty? surnameMember))
       ""

       (try
         (if (empty? (db/select-member-by-surname surnameMember))
           [:p {:style "color:red; border-color: #000000;"} [:b (str "Clan " surnameMember " cija iznajmljene knjige zelite da prikazete ne postoji!!!")] ]
           (try
             (= (count (db/select_records surnameMember)) 0)
             [:p {:style "color:green; border-color: #000000;"} [:b (str "Evidencije o iznajmljenim knjigama za  " surnameMember "-a su uspesno prikazana!!!")] ]
             (catch Exception e
               [:p {:style "color:red; border-color: #000000;"} [:b (str "Clan " surnameMember " cija evidencije zelite da prikazete ne postoji!!!")] ]
               )))



         (catch Exception e
           [:p {:style "color:red; border-color: #000000;"} [:b (str "Nisu prikazana evidencije o iznajmljenim knjigama od " surnameMember "-a!!!")] ]


           ))






       )
     (f/form-to [:get "/showAllRecords"]
                (anti-forgery-field)
                [:div {:class "form-group"}
                 [:label {:for "surmem"} [:b "Unesite prezime clana cije evidencije o iznajmljenim knjigama zelite da prikazete:"] ]
                 [:input {:type "text" :name "surnameMember" :class "form-control" :id "surmem" :value surnameMember}]
                 (if (or (nil? surnameMember) (empty? surnameMember) )
                   (empty-surname-member)
                   )
                 ]
                [:input {:type "submit"
                         :value "Prikazi evidencije"
                         :class "btn btn-primary"
                         }]
                (h "   ")
                [:a {:href "/showRecords"}[:input {:type "button"
                                                 :value "Resetuj sve vrednosti"
                                                 :class "btn btn-warning"}]]
                )

     ]
    [:div {:class "col-10"}
     [:h2 [:b "Prikaz svih evidencija o iznajmljenim knjigama" ]]
     [:br]
     [:table {:class "table table-dark"}
      [:thead
       [:tr
        [:th "Naziv knjige"]
        [:th "Ime pisca knjige"]
        [:th "Prezime pisca knjige"]
        [:th "Datum uzimanja knjige"]
        [:th "Datum vracanja knjige"]
        [:th "Napomena"]
        [:th "Izmeni evidenciju"]
        ]]

      (into [:tbody]
            (try
              (for [record (db/select_records surnameMember)]
                [:tr [:td (:nazivpublikacije record)]
                 [:td (:imeautora record)]
                 [:td (:prezimeautora record)]
                 [:td (:datumuzimanjaknjige record)]
                 [:td (:datumvracanjaknjige record)]
                 [:td (:napomena record)]
                 [:td [:a {:href (str "/updateRecord/" (h (:publikacijaid record)) "/" (h (:jmbg record)) ) :style "color:purple;"} "Izmeni"]]])
              (catch Exception e
                [[:b "Dogodila se greska prilikom ucitavanja evidencija!"]]
                ;(throw (Exception. e))
                ))
            )]
     ]
    [:div {:class "col-8"}

     ]]]
  )

(defn select-all-records [nameBook]
  (try
    (layout/pagelayout "Biblioteka" (menu/menuapp)
                       [:br]
                       [:br]
                       [:br]
                       [:br]
                       [:br]
                       [:br]
                       (show-all-records nameBook))
    (catch Exception e
      (throw (Exception. e))))
  )

(defn show-all-the-records [surnameMember]
  [:div {:class "container"}
   [:div {:class "row"}
    [:div {:class "col-6"}
     (if (or (nil? surnameMember) (empty? surnameMember))
       ""
       (try
         (if (empty? (db/select-member-by-surname surnameMember))

           (try
             (db/select_records surnameMember)

             (catch Exception e

               )))


         (catch Exception e



           )))
     (f/form-to [:get "/showAllRecords"]
                (anti-forgery-field)
                [:div {:class "form-group"}
                 [:label {:for "surmem"} [:b "Unesite prezime clana cije dela zelite da prikazete:"] ]
                 [:input {:type "text" :name "surnameMember" :class "form-control" :id "surmem" :value surnameMember}]

                 ]
                [:input {:type "submit"
                         :value "Prikazi evidencije"
                         :class "btn btn-primary"
                         }]
                (h "   ")
                [:a {:href "/showRecords"}[:input {:type "button"
                                                 :value "Resetuj sve vrednosti"
                                                 :class "btn btn-warning"}]]
                )

     ]
    [:div {:class "col-10"}
     [:h2 [:b "Prikaz svih evidencija" ]]
     [:br]
     [:table {:class "table table-dark"}
      [:thead
       [:tr
        [:th {:style "border-style: solid;  border-color: #000000;    background-color: red;    color: black;"} "Naziv knjige"  ]
        [:th {:style "border-style: solid;  border-color: #000000;    background-color: red;    color: black;"} "Ime pisca knjige"  ]
        [:th {:style "border-style: solid;  border-color: #000000;    background-color: red;    color: black;"} "Prezime pisca knjige"  ]
        [:th {:style "border-style: solid;  border-color: #000000;    background-color: red;    color: black;"} "Datum uzmimanja knjige"  ]
        [:th {:style "border-style: solid;  border-color: #000000;    background-color: red;    color: black;"}"Datum vracanja knjige" ]
        [:th {:style "border-style: solid;  border-color: #000000;    background-color: red;    color: black;"}"Napomena"  ]
        [:th {:style "border-style: solid;  border-color: #000000;    background-color: red;    color: black;"} "Izmeni knjigu"]
        ]]

      (into [:tbody]
            (try
              (for [record (db/select_records surnameMember)]
                [:tr [:td {:style "border-style: solid;  border-color: #000000;    background-color: yellow;    color: black;"} (:nazivpublikacije record)]
                 [:td {:style "border-style: solid;  border-color: #000000;    background-color: yellow;    color: black;"} (:datumuzimanjaknjige record)]
                 [:td {:style "border-style: solid;  border-color: #000000;    background-color: yellow;    color: black;"} (:imeautora record)]
                 [:td {:style "border-style: solid;  border-color: #000000;    background-color: yellow;    color: black;"} (:prezimeautora record)]
                 [:td {:style "border-style: solid;  border-color: #000000;    background-color: yellow;    color: black;"} (:datumvracanjaknjige record)]
                 [:td {:style "border-style: solid;  border-color: #000000;    background-color: yellow;    color: black;"} (:napomena record)]
                 [:td {:style "border-style: solid;  border-color: #000000;    background-color: yellow;    color: black;"}  [:a {:href (str "/updateRecord/" (h (:idpublikacije record))) :style "color:purple;"} "Izmeni"]]])
              (catch Exception e
                [[:b "Dogodila se greska prilikom ucitavanja evidencija!"]]
                ;(throw (Exception. e))
                ))
            )]
     ]
    [:div {:class "col-8"}

     ]]]
  )

(defn select-all-the-records[surnameMember]
  (try
    (layout/pagelayout "Biblioteka" (menu/menuapp)
                       [:br]
                       [:br]
                       [:br]
                       [:br]
                       [:br]
                       [:br]
                       (show-all-the-records surnameMember))
    (catch Exception e
      (throw (Exception. e))))
  )


; STRANA ZA UPDATE EVIDENCIJA !!!!!!!!!!!!!!
(defn update-record [publikacijaid jmbg]
  (layout/pagelayout "Biblioteka" (menu/menuapp)
                     [:br]
                     [:div {:class "container"}
                      [:div {:class "row"}
                       [:div {:class "col"}]
                       [:div {:class "col-6"}
                        [:h2 [:b "Izmena evidencije" ]]
                        (try
                          (for [record (db/select-record-by-publicationid-jmbg publikacijaid)]
                            [:div {:class "tab-content" }
                             [:div {:class "tab-pane active" :role "tabpanel"}
                              (f/form-to [:get (str "/updateThisRecord/" publikacijaid "/" jmbg)]
                                         (anti-forgery-field)
                                         [:div {:class "form-group"}
                                          [:label {:for "nb"} [:b "Naziv knjige:"] ]
                                          [:input {:type "text" :name "namebook" :class "form-control" :id "nb" :value (:nazivpublikacije record)}]
                                          ]
                                         [:div {:class "form-group"}
                                          [:label {:for "datet"} [:b "Datum uzimanja knjige:"] ]
                                          [:input {:type "text" :name "dateoftake" :class "form-control" :id "datet" :value (:datumuzimanjaknjige record)}]
                                          ]
                                         [:div {:class "form-group"}
                                          [:label {:for "dater"} [:b "Datum vracanja knjige:"] ]
                                          [:input {:type "text" :name "dateofreturn" :class "form-control" :id "dater" :value (:datumvracanjaknjige record)}]
                                          ]
                                         [:div {:class "form-group"}
                                          [:label {:for "note"} [:b "Napomena:"] ]
                                          [:input {:type "text" :name "notes" :class "form-control" :id "note" :value (:napomena record)}]
                                          ]


                                         [:input {:type "submit"
                                                  :value "Izmeni evidenciju"
                                                  :class "btn btn-primary"
                                                  }]
                                         (h "   ")
                                         [:a {:href (str "/updateRecord/" publikacijaid "/" jmbg) }[:input {:type "button"
                                                                                      :value "Resetuj sve vrednosti"
                                                                                      :class "btn btn-warning"}]])]])
                          (catch Exception e
                            [:p {:style "color:red; border-color: #000000;"} [:b (str "Nazalost, doslo je do greske prilikom ucitavanja evidencije!!!")] ]
                            ))

                        ]
                       [:div {:class "col"}]]])
  )

(defn update-this-record [publikacijaid jmbg namebook dateoftake dateofreturn notes]
  (layout/pagelayout "Biblioteka" (menu/menuapp)
                     [:br]
                     [:div {:class "container"}
                      [:div {:class "row"}
                       [:div {:class "col"}]
                       [:div {:class "col-6"}
                        [:h2 [:b "Izmena evidencije" ]]
                        (try
                          (for [record (db/select-record-by-publicationid-jmbg publikacijaid)]
                            [:div {:class "tab-content" }
                             [:div {:class "tab-pane active" :role "tabpanel"}
                              (f/form-to [:get (str "/updateThisRecord/" publikacijaid "/" jmbg)]
                                         (anti-forgery-field)
                                         [:div {:class "form-group"}
                                          [:label {:for "nb"} [:b "Naziv knjige:"] ]
                                          [:input {:type "text" :name "namebook" :class "form-control" :id "nb" :value namebook}]
                                          (if (or (nil? namebook) (empty? namebook) )
                                            [:label {:for "nb" :style"color:red"} [:b "Morate da unesete naziv knjige!!!"] ]
                                            )
                                          ]

                                         [:div {:class "form-group"}
                                          [:label {:for "datet"} [:b "Datum uzimanja knjige:"]]
                                          [:input {:type "text" :name "dateoftake" :class "form-control" :id "datet" :value dateoftake}]
                                          (if (or (nil? dateoftake) (empty? dateofreturn))
                                            (empty-dateofentry)
                                            (try
                                              (ftime/parse (ftime/formatter "YYYY-MM-dd") dateoftake)
                                              [:label {:for "datein" :style "color:red"} [:b ""]]
                                              (catch Exception e
                                                (dateofin-format))))
                                          ]



                                         [:div {:class "form-group"}
                                          [:label {:for "dater"} [:b "Datum rodjenja:"]]
                                          [:input {:type "text" :name "dateofreturn" :class "form-control" :id "dater" :value dateofreturn}]
                                          (if (or (nil? dateofreturn) (empty? dateofreturn))
                                            (empty-dateout)
                                            (try
                                              (ftime/parse (ftime/formatter "YYYY-MM-dd") dateofreturn)
                                              [:label {:for "dateout" :style "color:red"} [:b ""]]
                                              (catch Exception e
                                                (dateofout-format))))
                                          ]

                                         [:div {:class "form-group"}
                                          [:label {:for "note"} [:b "Status:"] ]
                                          [:input {:type "text" :name "notes" :class "form-control" :id "ne" :value notes}]
                                          (if (or (nil? notes) (empty? notes) )
                                            [:label {:for "nt" :style"color:red"} [:b "Morate da unesete napomenu o ovoj evidenciji)!!!"] ]
                                            )
                                          ]



                                         [:input {:type "submit"
                                                  :value "Izmeni knjigu"
                                                  :class "btn btn-primary"
                                                  }]
                                         (h "   ")
                                         [:a {:href (str "/updateRecord/" publikacijaid "/" jmbg)}[:input {:type "button"
                                                                                     :value "Resetuj sve vrednosti"
                                                                                     :class "btn btn-warning"}]])]]
                            )
                          (catch Exception e
                            ))
                        ]
                       [:div {:class "col"}
                        (if (or (nil? namebook)  (nil? dateoftake) (nil? dateofreturn) (nil? notes) (empty? namebook)  (empty? dateoftake) (empty? dateofreturn) (empty? notes))
                          ""
                          (try
                            (db/update-record publikacijaid jmbg dateoftake dateofreturn notes )
                            (db/update-status-book-for-member-after-update publikacijaid)
                            (db/delete-record-for-member-after-update publikacijaid)
                            [:p {:style "color:green; border-color: #000000;"} [:b (str "Uspesno izmenjena evidencija " namebook "!!!")] ]
                            (catch Exception e
                              [:p {:style "color:red; border-color: #000000;"} [:b (str "Nije izmenjena evidencija!!!" namebook )] ]
                              )))
                        ]]
                      ])
  )







(defn pick-author []
  (layout/pagelayout "Biblioteka" (menu/menuapp)
                 [:br]
                 [:div {:class "container"}
                  [:div {:class "row"}
                   [:div {:class "col"}]
                   [:div {:class "col-6"}
                    [:h3  [:marquee [:b "Povezi autore sa njihovim delima i godinama izdanja"]] ]
                    [:br]
                    (try
                      [:div {:class "tab-content" }
                       [:div {:class "tab-pane active" :role "tabpanel"}
                        (f/form-to [:get (str "/pickTheAuthor")]
                                   (anti-forgery-field)
                                   [:div {:class "form-group"}
                                    [:input {:type "text" :name "first" :style "color:red; border-color:red;" :readonly "true" :value (props :prvi)}] (h "                   ")
                                    [:select {:name "book1" :class ""}
                                     [:option {:value ""} "Izaberi knjigu"]
                                     [:option {:value "RatiMir"} "Rat i mir"]
                                     [:option {:value "NaDriniCuprija"} "Na Drini Cuprija"]
                                     [:option {:value "RomeoIJulija"} "Romeo i Julija"]
                                     [:option {:value "BozanstvenaKomedija"} "Bozanstvena komedija"]
                                     [:option {:value "DonKihot"} "Don Kihot"]
                                     ]
                                    ]
                                   [:div {:class "form-group"}
                                    [:input {:type "text" :name "first1" :style "color:red; border-color:red;" :readonly "true" :value (props :prvi)}] (h "                   ")
                                    [:select {:name "year1" :class ""}
                                     [:option {:value ""} "Izaberi godinu izdanja"]
                                     [:option {:value "1956"} "1956"]
                                     [:option {:value "1954"} "1954"]
                                     [:option {:value "1267"} "1267"]
                                     [:option {:value "1898"} "1898"]
                                     [:option {:value "1569"} "1569"]
                                     ]
                                    ]
                                   [:br]
                                   [:div {:class "form-group"}
                                    [:input {:type "text" :name "second" :style "color:red; border-color:red;" :readonly "true" :value (props :drugi)}] (h "                   ")
                                    [:select {:name "book2" :class ""}
                                     [:option {:value ""} "Izaberi knjigu"]
                                     [:option {:value "RatiMir"} "Rat i mir"]
                                     [:option {:value "NaDriniCuprija"} "Na Drini Cuprija"]
                                     [:option {:value "RomeoIJulija"} "Romeo i Julija"]
                                     [:option {:value "BozanstvenaKomedija"} "Bozanstvena komedija"]
                                     [:option {:value "DonKihot"} "Don Kihot"]]
                                    ]
                                   [:br]
                                   [:div {:class "form-group"}
                                    [:input {:type "text" :name "second2" :style "color:red; border-color:red;" :readonly "true" :value (props :drugi)}] (h "                   ")
                                    [:select {:name "year2" :class ""}
                                     [:option {:value ""} "Izaberi godinu izdanja"]
                                     [:option {:value "1956"} "1956"]
                                     [:option {:value "1954"} "1954"]
                                     [:option {:value "1267"} "1267"]
                                     [:option {:value "1898"} "1898"]
                                     [:option {:value "1569"} "1569"]
                                     ]
                                    ]

                                   [:div {:class "form-group"}
                                    [:input {:type "text" :name "third" :style "color:red; border-color:red;" :readonly "true" :value (props :treci)}] (h "                   ")
                                    [:select {:name "book3" :class ""}
                                     [:option {:value ""} "Izaberi knjigu"]
                                     [:option {:value "RatiMir"} "Rat i mir"]
                                     [:option {:value "NaDriniCuprija"} "Na Drini Cuprija"]
                                     [:option {:value "RomeoIJulija"} "Romeo i Julija"]
                                     [:option {:value "BozanstvenaKomedija"} "Bozanstvena komedija"]
                                     [:option {:value "DonKihot"} "Don Kihot"]]
                                    ]
                                   [:br]
                                   [:div {:class "form-group"}
                                    [:input {:type "text" :name "third3" :style "color:red; border-color:red;" :readonly "true" :value (props :treci)}] (h "                   ")
                                    [:select {:name "year3" :class ""}
                                     [:option {:value ""} "Izaberi godinu izdanja"]
                                     [:option {:value "1956"} "1956"]
                                     [:option {:value "1954"} "1954"]
                                     [:option {:value "1267"} "1267"]
                                     [:option {:value "1898"} "1898"]
                                     [:option {:value "1569"} "1569"]
                                     ]
                                    ]
                                   [:div {:class "form-group"}
                                    [:input {:type "text" :name "fourth" :style "color:red; border-color:red;" :readonly "true" :value (props :cetvrti)}] (h "                   ")
                                    [:select {:name "book4" :class ""}
                                     [:option {:value ""} "Izaberi knjigu"]
                                     [:option {:value "RatiMir"} "Rat i mir"]
                                     [:option {:value "NaDriniCuprija"} "Na Drini Cuprija"]
                                     [:option {:value "RomeoIJulija"} "Romeo i Julija"]
                                     [:option {:value "BozanstvenaKomedija"} "Bozanstvena komedija"]
                                     [:option {:value "DonKihot"} "Don Kihot"]]
                                    ]
                                   [:div {:class "form-group"}
                                    [:input {:type "text" :name "fourth4" :style "color:red; border-color:red;" :readonly "true" :value (props :cetvrti)}] (h "                   ")
                                    [:select {:name "year4" :class ""}
                                     [:option {:value ""} "Izaberi godinu izdanja"]
                                     [:option {:value "1956"} "1956"]
                                     [:option {:value "1954"} "1954"]
                                     [:option {:value "1267"} "1267"]
                                     [:option {:value "1898"} "1898"]
                                     [:option {:value "1569"} "1569"]
                                     ]
                                    ]
                                   [:br]
                                   [:div {:class "form-group"}
                                    [:input {:type "text" :name "fifth" :style "color:red; border-color:red;" :readonly "true" :value (props :peti)}] (h "                   ")
                                    [:select {:name "book5" :class ""}
                                     [:option {:value ""} "Izaberi knjigu"]
                                     [:option {:value "RatiMir"} "Rat i mir"]
                                     [:option {:value "NaDriniCuprija"} "Na Drini Cuprija"]
                                     [:option {:value "RomeoIJulija"} "Romeo i Julija"]
                                     [:option {:value "BozanstvenaKomedija"} "Bozanstvena komedija"]
                                     [:option {:value "DonKihot"} "Don Kihot"]]
                                    ]
                                   [:div {:class "form-group"}
                                    [:input {:type "text" :name "fifth5" :style "color:red; border-color:red;" :readonly "true" :value (props :peti)}] (h "                   ")
                                    [:select {:name "year5" :class ""}
                                     [:option {:value ""} "Izaberi godinu izdanja"]
                                     [:option {:value "1956"} "1956"]
                                     [:option {:value "1954"} "1954"]
                                     [:option {:value "1267"} "1267"]
                                     [:option {:value "1898"} "1898"]
                                     [:option {:value "1569"} "1569"]
                                     ]
                                    ]
                                   [:br]

                                   [:input {:type "submit"
                                            :value "Potvrdi odgovore"
                                            :class "btn btn-primary"
                                            }]
                                   (h "   ")
                                   [:a {:href (str "/pickAuthor")}[:input {:type "button"
                                                                         :value "Nova igra"
                                                                         :class "btn btn-primary"}]])]]
                      (catch Exception e
                        ))
                    ]
                   [:div {:class "col"}
                    ]]
                  ]
                 )
  )

(defn pick-the-author [id1 id2 id3 id4 id5 book1 book2 book3 book4 book5  year1 year2 year3 year4 year5]
  (layout/pagelayout "Biblioteka" (menu/menuapp)
                 [:br]
                 [:div {:class "container"}
                  [:div {:class "row"}
                   [:div {:class "col"}]
                   [:div {:class "col-6"}
                    [:h3  [:marquee [:b "Povezi autore sa njihovim delima i godinama izdanja"]] ]
                    [:br]
                    (try
                      [:div {:class "tab-content" }
                       [:div {:class "tab-pane active" :role "tabpanel"}
                        (f/form-to [:get (str "/pickTheAuthor")]
                                   (anti-forgery-field)
                                   [:div {:style "display:none"}(def k 0)]
                                   [:div {:class "form-group"}
                                    [:input {:type "text" :name "prvi" :class "" :readonly "true" :value id1}] (h "                   ")
                                    [:input {:type "text" :class "" :readonly "true" :value book1}](h "                                 ")
                                    [:input {:type "text" :class "" :readonly "true" :value year1}](h "                                 ")
                                    (if (or (empty? book1 ) (nil? book1) (empty? year1 ) (nil? year1) (empty? (db/select-author-by-name id1 book1 year1)))
                                      [:img {:src "img/no.png" :height"25" :width"25"} [:div {:style "display:none"}] [:p {:style "color:red; border-color: #000000;"} [:b (str "Netacno")] ]]
                                      [:img {:src "img/yes.png" :height"25" :width"25"}[:div {:style "display:none"}(def k (+ k 1))] [:p {:style "color:green; border-color: #000000;"} [:b (str "Tacno")] ] ])
                                    ]
                                   [:br]

                                   [:div {:class "form-group"}
                                    [:input {:type "text" :name "drugi" :class "" :readonly "true" :value id2}] (h "                   ")
                                    [:input {:type "text" :class "" :readonly "true" :value book2}](h "                                 ")
                                    [:input {:type "text" :class "" :readonly "true" :value year2}](h "                                 ")
                                    (if (or (empty? book2 ) (nil? book2) (empty? year2 ) (nil? year2) (empty? (db/select-author-by-name id2 book2 year2)))
                                      [:img {:src "img/no.png" :height"25" :width"25"} [:div {:style "display:none"}] [:p {:style "color:red; border-color: #000000;"} [:b (str "Netacno")] ]]
                                      [:img {:src "img/yes.png" :height"25" :width"25"} [:div {:style "display:none"}(def k (+ k 1))] [:p {:style "color:green; border-color: #000000;"} [:b (str "Tacno")] ]])
                                    ]
                                   [:br]

                                   [:div {:class "form-group"}
                                    [:input {:type "text" :name "treci" :class "" :readonly "true" :value id3}] (h "                   ")
                                    [:input {:type "text" :class "" :readonly "true" :value book3}](h "                                 ")
                                    [:input {:type "text" :class "" :readonly "true" :value year3}](h "                                 ")

                                    (if (or (empty? book3 ) (nil? book3) (empty? year3 ) (nil? year3) (empty? (db/select-author-by-name id3 book3 year3)))
                                      [:img {:src "img/no.png" :height"25" :width"25"} [:div {:style "display:none"}] [:p {:style "color:red; border-color: #000000;"} [:b (str "Netacno")] ]]
                                      [:img {:src "img/yes.png" :height"25" :width"25"} [:div {:style "display:none"}(def k (+ k 1))] [:p {:style "color:green; border-color: #000000;"} [:b (str "Tacno")] ]])
                                    ]
                                   [:br]

                                   [:div {:class "form-group"}
                                    [:input {:type "text" :name "cetvrti" :class "" :readonly "true" :value id4}] (h "                   ")
                                    [:input {:type "text" :class "" :readonly "true" :value book4}](h "                                 ")
                                    [:input {:type "text" :class "" :readonly "true" :value year4}](h "                                 ")
                                    (if (or (empty? book4 ) (nil? book4) (empty? year4 ) (nil? year4) (empty? (db/select-author-by-name id4 book4 year4)))
                                      [:img {:src "img/no.png" :height"25" :width"25"} [:div {:style "display:none"}] [:p {:style "color:red; border-color: #000000;"} [:b (str "Netacno")] ]]
                                      [:img {:src "img/yes.png" :height"25" :width"25"} [:div {:style "display:none"}(def k (+ k 1))] [:p {:style "color:green; border-color: #000000;"} [:b (str "Tacno")] ]])
                                    ]
                                   [:br]

                                   [:div {:class "form-group"}
                                    [:input {:type "text" :name "peti" :class "" :readonly "true" :value id5}] (h "                   ")
                                    [:input {:type "text" :class "" :readonly "true" :value book5}](h "                                 ")
                                    [:input {:type "text" :class "" :readonly "true" :value year5}](h "                                 ")

                                    (if (or (empty? book5 ) (nil? book5) (empty? year5 ) (nil? year5) (empty? (db/select-author-by-name id5 book5 year5)))
                                      [:img {:src "img/no.png" :height"25" :width"25"} [:div {:style "display:none"}] [:p {:style "color:red; border-color: #000000;"} [:b (str "Netacno")] ]]
                                      [:img {:src "img/yes.png" :height"25" :width"25"} [:div {:style "display:none"}(def k (+ k 1))] [:p {:style "color:green; border-color: #000000;"} [:b (str "Tacno")] ] ])
                                    ]
                                   [:br]



                                (show-price k)

                                   [:br]



                                   [:a {:href (str "/pickAuthor")}[:input {:type "button"
                                                                         :value "New game"
                                                                         :class "btn btn-primary"}]])]]
                      (catch Exception e
                        ))
                    ]
                   [:div {:class "col"}
                    ]]
                  ]
                 )
  )


(defn pick-this-author []
  (layout/pagelayout "Biblioteka" (menu/menuapp)
                 [:br]
                 [:div {:class "container"}
                  [:div {:class "row"}
                   [:div {:class "col"}]
                   [:div {:class "col-6"}
                    [:h3  [:marquee [:b "Povezi autore sa njihovim delima i godinama izdanja"]] ]
                    [:br]
                    (try

                      [:div {:class "tab-content" }
                       [:div {:class "tab-pane active" :role "tabpanel"}
                        (f/form-to [:get (str "/pickTheAuthor")]
                                   (anti-forgery-field)
                                   [:div {:style "display:none"}(def i 0)]
                                   [:div {:style "display:none"}(def j 0)]

                                   (for [thisauthor (db/select-authors-random)]
                                     [:div {:class ""}
                                      [:input {:type "text" :name (str "id" (+ i 1))  :style "color:red; border-color:red;" :readonly "true" :value (:prezimeautora thisauthor)}] (h "                   ")
                                      [:select {:name (str "book" (+ i 1)) :class ""}
                                       [:option {:value ""} "Select book"]
                                       (for [book (db/select-books-order-name)]
                                         [:option {:value (:nazivpublikacije book)} (:nazivpublikacije book)]
                                         )

                                       [:div {:style "display:none"}(def i (+ i 1))]]


                                      [:select {:name (str "year" (+ j 1)) :class ""}
                                       [:option {:value ""} "Select year"]
                                       (for [book (db/select-books-order-year)]
                                         [:option {:value (:godinaizdanja book)} (:godinaizdanja book)]
                                         )

                                       [:div {:style "display:none"}(def j (+ j 1))]]
                                      [:br]
                                      ]
                                     )
                                   [:input {:type "submit"
                                            :value "Send answers"
                                            :class "btn btn-primary"
                                            }]


                                   )]]
                      (catch Exception e
                        ))
                    ]
                   [:div {:class "col"}
                    ]]
                  ]
                 )
  )

;;UBACI NOVOG CLANA
(defn insert-new-member-page [jmbg name surname email phone dateofentry]
  (layout/pagelayout "Biblioteka" (menu/menuapp)
                     [:br]
                     [:div {:class "container"}
                      [:div {:class "row"}
                       [:div {:class "col"}]
                       [:div {:class "col-6"}
                        [:h3 [:b "Unos novog clana"]]
                        [:div {:class "tab-content"}
                         [:div {:class "tab-pane active" :role "tabpanel"}
                          (f/form-to [:get "/insertNewMember"]
                                     (anti-forgery-field)

                                     [:div {:class "form-group"}
                                      [:label {:for "jm"} [:b "JMBG novog clana:"]]
                                      [:input {:type "text" :name "jmbg" :class "form-control" :id "jm" :value jmbg} ]
                                      (if (or (nil? jmbg) (empty? jmbg))
                                        (empty-member-jmbg)
                                        )
                                      ]

                                     [:div {:class "form-group"}
                                      [:label {:for "n"} [:b "Ime novog clana:"]]
                                      [:input {:type "text" :name "name" :class "form-control" :id "n" :value name} ]
                                      (if (or (nil? name) (empty? name))
                                        (empty-member-name)
                                        )
                                      ]
                                     [:div {:class "form-group"}
                                      [:label {:for "s"} [:b "Prezime novog clana:"]]
                                      [:input {:type "text" :name "surname" :class "form-control" :id "s" :value surname} ]
                                      (if (or (nil? surname) (empty? surname))
                                        (empty-member-surname)
                                        )
                                      ]

                                     [:div {:class "form-group"}
                                      [:label {:for "ml"} [:b "Email:"]]
                                      [:input {:type "email" :name "email" :class "form-control" :id "ml" :value email}]
                                      (if (or (nil? email) (empty? email))
                                        (empty-mail)
                                        )




                                      ]

                                     [:div {:class "form-group"}
                                      [:label {:for "ph"} [:b "Broj telefona:"]]
                                      [:input {:type "text" :name "phone" :class "form-control" :id "ph" :value phone} ]
                                      (if (or (nil? phone) (empty? phone))
                                        (empty-phone)
                                        )
                                      ]


                                     [:div {:class "form-group"}
                                      [:label {:for "den"} [:b "Datum uclanjena:"]]
                                      [:input {:type "text" :name "dateofentry" :class "form-control" :id "den" :value dateofentry} ]
                                      (if (or (nil? dateofentry) (empty? dateofentry))
                                        (empty-dateofentry)
                                        (try
                                          (ftime/parse (ftime/formatter "YYYY-MM-dd") dateofentry)
                                          [:label {:for "enr" :style "color:red"} [:b ""]]
                                          (catch Exception e
                                            (dateofentry-format))))
                                      ]





                                     [:input {:type  "submit"
                                              :value "Ubaci novog clana"
                                              :class "btn btn-success"
                                              }]
                                     (h "   ")
                                     [:a {:href "/insertMember"} [:input {:type  "button"
                                                                          :value "Resetuj  sve vrednosti"
                                                                          :class "btn btn-warning"}]])]]]
                       [:div {:class "col"}
                        (if (or (nil? jmbg) (nil? name) (nil? surname) (nil? email) (nil? phone) (nil? dateofentry) (empty? jmbg) (empty? name) (empty? surname) (empty? email) (empty? phone) (empty? dateofentry) )
                          ""
                          (try
                            (if (empty? (db/select-member-by-surname surname))
                              (try
                                (db/insert-new-member jmbg name surname email phone dateofentry)
                                (success-insert-new-member surname)


                                (catch Exception e
                                  [:p {:style "color:red; border-color: #000000;"} [:b (str "Nije ubacen novi clan!!!" surname)]]

                                  ))
                              )

                            (catch Exception e

                              (exist-name-of-member surname)
                              )))
                        ]]
                      ])
  )

(defn insert-member-page []
  (layout/pagelayout "Biblioteka" (menu/menuapp)
                     [:br]
                     [:div {:class "container"}
                      [:div {:class "row"}
                       [:div {:class "col"}]
                       [:div {:class "col-6"}
                        [:h3 [:b "Unos novog autora"]]
                        [:div {:class "tab-content"}
                         [:div {:class "tab-pane active" :role "tabpanel"}
                          (f/form-to [:get "/insertNewMember"]
                                     (anti-forgery-field)

                                     [:div {:class "form-group"}
                                      [:label {:for "jm"} [:b "JMBG novog clana"]]
                                      [:input {:type "text" :name "jmbg" :class "form-control" :id "jm"}]
                                      ]

                                     [:div {:class "form-group"}
                                      [:label {:for "n"} [:b "Ime novog clana"]]
                                      [:input {:type "text" :name "name" :class "form-control" :id "n"}]
                                      ]

                                     [:div {:class "form-group"}
                                      [:label {:for "s"} [:b "Prezime novog clana"]]
                                      [:input {:type "text" :name "surname" :class "form-control" :id "s"}]
                                      ]

                                     [:div {:class "form-group"}
                                      [:label {:for "ml"} [:b "Email"]]
                                      [:input {:type "email" :name "email" :class "form-control" :id "ml"}]

                                      ]

                                     [:div {:class "form-group"}
                                      [:label {:for "ph"} [:b "Telefon"]]
                                      [:input {:type "text" :name "phone" :class "form-control" :id "ph"}]

                                      ]

                                     [:div {:class "form-group"}
                                      [:label {:for "den"} [:b "Datum uclanjenja"]]
                                      [:input {:type "text" :name "dateofentry" :class "form-control" :id "den"}]

                                      ]





                                     [:input {:type  "submit" :read-only "false"
                                              :value "Ubaci novog clana"
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







(defroutes app-routes
           (GET "/" [] (index))
           (GET "/award" [] (award-pdf))
           (GET "/showAuthors" [] (try (select-authors) (catch Exception e
                                                      (error-load-author e))))
           (GET "/insertAuthor" [] (insert-author-page))
           (GET "/insertNewAuthor" [name surname dateofbirth country mainbook]
             (insert-new-author-page name surname dateofbirth country mainbook))

           (GET "/insertMember" [] (insert-member-page))
           (GET "/insertNewMember" [jmbg name surname email phone  dateofentry  ]
             (insert-new-member-page jmbg name surname email phone dateofentry ))


           (GET "/insertNewDataMember" [] (try
                                   (insert-data-for-member-page)
                                   (catch Exception e
                                     )))
           (GET "/insertNewDataForMember" [surnamemember nameofbook dateoftake notes ]
                                                                        (try
                                                                       (insert-new-data-memberpage surnamemember nameofbook dateoftake notes )
                                                                       (catch Exception e
                                                                         )))






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

           (GET "/showRecords" [] (try (select-records) (catch Exception e
                                                      )))
           (GET "/showAllRecords" [surnameMember] (try (select-all-records surnameMember) (catch Exception e
                                                                                        )))
           (GET "/showAllTheRecords/:surnameMember" [surnameMember] (try (select-all-the-records surnameMember) (catch Exception e
                                                                                                              )))





           (GET "/insertBook" [] (try
                                     (insert-book)
                                     (catch Exception e
                                       )))
           (GET "/insertNewBook" [namebook genre year author] (try
                                                                                    (insert-new-book namebook genre year author)
                                                                                    (catch Exception e
                                                                                      )))


           (GET "/deleteBook" [] (try (select-books-for-delete) (catch Exception e
                                                                      )))
           (GET "/deleteThisBook" [namebook] (try (select-books-which-delete namebook) (catch Exception e)))

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
           (GET "/updateThisBook/:id" [id namebook genre year status author] (try
                                                                                           (update-this-book id namebook genre year status author)
                                                                                           (catch Exception e
                                                                                             )))






           (GET "/updateRecord/:publikacijaid/:jmbg" [publikacijaid jmbg] (try
                                         (update-record publikacijaid jmbg)
                                         (catch Exception e
                                           )))
           (GET "/updateThisRecord/:publikacijaid/:jmbg" [publikacijaid jmbg namebook dateoftake dateofreturn notes] (try
                                                                               (update-this-record publikacijaid jmbg namebook dateoftake dateofreturn notes)
                                                                               (catch Exception e
                                                                                 )))




           (GET "/pickAuthor" [] (try
                                 (pick-this-author)
                                 (catch Exception e
                                   )))
           (GET "/pickTheAuthor" [id1 id2 id3 id4 id5 book1 book2 book3 book4 book5 year1 year2 year3 year4 year5] (try
                                                                                                                             (pick-the-author id1 id2 id3 id4 id5 book1 book2 book3 book4 book5 year1 year2 year3 year4 year5)
                                                                                                                             (catch Exception e
                                                                                                                               )))


           (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))




