# biblioteka

FIXME

## Prerequisites

You will need [Leiningen][1] 1.7.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Running


**About start application and path to database:**

We start the application to lein ring server on the path of the project.Database is mysql with username:root
and password:
Password is empty.
In database have default port:(localhost:3306/bibliteka)



```
(ns biblioteka.models.db
...

(def db-map {:classname   "com.mysql.jdbc.Driver"
             :subprotocol "mysql"
             :subname     "//localhost:3306/bibliteka"
             :user        "root"
             :password    ""})	     
	    ...  
	    
	    
	    )

```


**Some about work application:**


>After start the application, we have three cards in *menu:* **Home page,Authors and Publication(Book)**.
>>In card **Authors** we perform basic CRUD operations on the table autor in database bibliteka.
First:**INSERT** author-we have form for entry data about new author in database bibliteka in table autor.
Second:**SELECT** authors-display in the table all the authors from the database which order by dateofbirth
Also we perform **UPDATE**:change data about choose author from table and save new data about this choose author in the table and database.
Third:**DELETE** publication-we delete choose book from database.

>In card Publication we also perform basic CRUD operations on the table publikacija in database bibliteka.
>>We connect table publikacija with table autor with foreign key by autorid.
In this menu card we can **INSERT** new book where have form for entry data about new book. In this form also we have to choose surname author for book which insert, but just authors which exists in database. Then we have **SELECT** command which display in the table all the books from the database which order by name book, but first we have field to insert surname author which books want to show.
With **DELETE** command we delete choose book from database. At the end with **UPDATE**:change data about choose book from table and save new data about this choose book in the table and database.

>We add new card **Promotion** where have one quiz. You have five random authors in database and you must connect **authors** with their **book** and **year of publication**. If you match all authors correct you got free month promotion.
Also,in this card we work with pdf template **[clj-pdf "2.2.33"]** where we make pdf file like proof for this promotion.
>>All CRUD operation which describe is here

>
```
(ns biblioteka.models.db
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
	    ...
	    ...
	    
	    
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
  
  ...
  ...
  ...
  
  )
```

>>Also,all business logic which describe is here

>

```
(ns biblioteka.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.util.response :as ring]
	    
	    ...
	    ...
	    
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
		  
		  ...
		  ...
		  
		  (GET "/showBooks" [] (try (select-books) (catch Exception e
                                                          )))
           (GET "/showAllBooks" [surnameAuthor] (try (select-all-books surnameAuthor) (catch Exception e
                                                                             )))
           (GET "/showAllTheBooks/:surnameAuthor" [surnameAuthor] (try (select-all-the-books surnameAuthor) (catch Exception e
                                                                                         )))


           (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
```


		 



## License

Copyright © 2018 FIXME
