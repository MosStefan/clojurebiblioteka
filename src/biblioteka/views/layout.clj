(ns biblioteka.views.layout
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
            [ring.util.response :refer [redirect]]))

(defn page1 [title & content]
  (str
    (html
      (doctype :html5)
      [:html
       [:head
        [:title title]


        (include-css "css/table.css")
        (include-css "css/background.css")
        (include-css "css/bootstrap.css")
        (include-css "css/bootstrap-responsive.css")
        (include-js "js/bootstrap.js")

        ]
       [:body content]])))

(defn pagelayout [title & content]
  (str
    (html
      (doctype :html5)
      [:html
       [:head
        [:title title]
        [:link {:rel "icon" :href "img/lord of ring.jpg"}]

        (include-css "css/background.css")
        (include-css "css/table.css")
        (include-css "https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css")
        (include-js "https://code.jquery.com/jquery-3.2.1.slim.min.js")
        (include-js "https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js")
        (include-js "https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js")
       [:body content]]])))





