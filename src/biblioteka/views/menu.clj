(ns biblioteka.views.menu
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [hiccup.core :refer :all]
            [hiccup.page :refer [doctype include-css]]
            [hiccup.page :refer :all]
            [hiccup.form :as f]
            [hiccup.bootstrap.page :refer :all]
            [hiccup.bootstrap.element :as hbe]
            [hiccup.element :refer [link-to]]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
            [ring.util.response :refer [redirect]]
            ))


(defn menuapp []
  [:nav {:class "navbar navbar-expand-lg navbar-light bg-light"}
   [:a {:class "navbar-brand" :href "/"} "Pocetna"]
   [:button {:type "button" :class "navbar-toggler" :data-toggle "collapse"
             :data-target "#navbarNavDropdown" :aria-expanded "false" :aria-controls "navbarNavDropdown"
             :aria-label "Toggle navigation"}
    [:span {:class "navbar-toggler-icon"}]]
   [:div {:class "collapse navbar-collapse" :id "navbarNavDropdown"}
    [:ul {:class "navbar-nav"}
     [:li {:class "nav-item dropdown"}
      [:a {:class "nav-link dropdown-toggle" :href "/books" :id "navbarDropdownMenuLink" :data-toggle "dropdown" :aria-hashpopup "true" :aria-expanded "false"} "Publikacije"]
      [:div {:class "dropdown-menu" :aria-labelledby "navbarDropdownMenuLink"}
       [:a {:class "dropdown-item" :href "/showBooks"} "Prikazi publikacije"]
       [:a {:class "dropdown-item" :href "/insertBook"} "Unesi publikaciju"]
       [:a {:class "dropdown-item" :href "/deleteBook"} "Obrisi publikaciju"]
       ]]
     ]
    ]])
