(defproject com.cemerick/url "0.0.9-SNAPSHOT"
  :description "Makes working with URLs in Clojure a little more pleasant."
  :url "http://github.com/cemerick/url"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [pathetic "0.5.0"]
                 [com.cemerick/clojurescript.test "0.0.4"]]

  :source-paths ["src" "generated-src"]
  :test-paths ["generated-test"]
  :aliases  {"all" ["with-profile"  "dev"]}
  :plugins [[com.keminglabs/cljx "0.3.0"]
            [lein-cljsbuild "0.3.2"]]
  :profiles {:dev {:dependencies [[com.keminglabs/cljx "0.3.0"]]
                   :plugins [[lein-clojars "0.8.0"]]}}
  :cljx {:builds [{:source-paths ["src"]
                   :output-path "generated-src"
                   :rules :clj}
                  {:source-paths ["src"]
                   :output-path "generated-src"
                   :rules :cljs}
                  {:source-paths ["test"]
                   :output-path "generated-test"
                   :rules :clj}
                  {:source-paths ["test"]
                   :output-path "generated-test"
                   :rules :cljs}]}
  :cljsbuild {:builds [{:source-paths ["generated-src" "generated-test"]
                        :compiler {:output-to "target/cljs/testable.js"}
                        :optimizations :whitespace
                        :pretty-print true}]
              :test-commands {"unit-tests" ["runners/phantomjs.js" "target/cljs/testable.js"]}})
