(defproject biblioteka "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.5.1"]
                 [ring/ring-defaults "0.2.1"]
                 [hiccup "2.0.0-alpha1"]
                 [ring/ring-jetty-adapter "1.6.1"]
                 [ring/ring-core "1.6.1"]
                 [org.clojure/java.jdbc "0.7.6"]
                 [mysql/mysql-connector-java "5.1.18"]
                 [clj-time "0.6.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [hiccup-table "0.2.0"]
                 [hiccup-bootstrap "0.1.2"]]
  :plugins [[lein-ring "0.9.7"]]
  :ring {:handler biblioteka.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.0"]]}}
  :jvm-opts ["-Djdbc.drivers=com.mysql.jdbc.Driver"])
