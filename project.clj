(defproject com.cemerick/url "0.0.3"
  :description "Makes working with URLs in Clojure a little more pleasant."
  :url "http://github.com/cemerick/url"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [org.clojure/core.incubator "0.1.0"]
                 [pathetic "0.4.0"]]
  
  :aliases  {"all" ["with-profile" "dev,1.2:dev:dev,1.4"]}
  :profiles {:dev {:plugins [[lein-clojars "0.8.0"]]}
             :1.2 {:dependencies [[org.clojure/clojure "1.2.0"]]}
             :1.4 {:dependencies [[org.clojure/clojure "1.4.0-beta5"]]}})
