(defproject com.cemerick/url "0.0.7"
  :description "Makes working with URLs in Clojure a little more pleasant."
  :url "http://github.com/cemerick/url"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/core.incubator "0.1.0"]
                 [pathetic "0.4.0"]]
  
  :aliases  {"all" ["with-profile" "dev,1.2:dev,1.3:dev:dev,1.5"]}
  :profiles {:dev {:plugins [[lein-clojars "0.8.0"]]}
             :1.2 {:dependencies [[org.clojure/clojure "1.2.0"]]}
             :1.3 {:dependencies [[org.clojure/clojure "1.3.0"]]}
             :1.5 {:dependencies [[org.clojure/clojure "1.5.0-alpha6"]]}})
