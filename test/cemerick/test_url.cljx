(ns cemerick.test-url
  #+clj (:import java.net.URL)
  #+clj (:use cemerick.url
              clojure.test)
  #+cljs (:require-macros [cemerick.cljs.test :refer (are is deftest with-test run-tests testing)])
  #+cljs (:use [cemerick.url :only [url map->query query->map map->URL]])
  #+cljs (:require [cemerick.cljs.test :as t]))

(def url-str (comp str url))

(deftest test-map-to-query-str
  (are [x y] (= x (map->query y))
       "a=1&b=2&c=3" {:a 1 :b 2 :c 3}
       "a=1&b=2&c=3" {:a "1"  :b "2" :c "3"}
       "a=1&b=2" {"a" "1" "b" "2"}
       "a=" {"a" ""}))

(deftest url-roundtripping
  (let [aurl (url "https://username:password@some.host.com/database?query=string")]
    (is (= "https://username:password@some.host.com/database?query=string" (str aurl)))
    (is (== -1 (:port aurl)))
    (is (= "username" (:username aurl)))
    (is (= "password" (:password aurl)))
    (is (= "https://username:password@some.host.com" (str (assoc aurl :path nil :query nil))))))

(deftest url-segments
  (is (= "http://localhost:5984/a/b" (url-str "http://localhost:5984" "a" "b")))
  (is (= "http://localhost:5984/a/b/c" (url-str "http://localhost:5984" "a" "b" "c")))
  (is (= "http://localhost:5984/a/b/c" (url-str (url "http://localhost:5984" "a") "b" "c"))))

(deftest port-normalization
  #+clj (is (== -1 (-> "https://foo" url-str URL. .getPort)))
  (is (= "http://localhost" (url-str "http://localhost")))
  (is (= "http://localhost" (url-str "http://localhost:80")))
  (is (= "http://localhost:8080" (url-str "http://localhost:8080")))
  (is (= "https://localhost" (url-str "https://localhost")))
  (is (= "https://localhost" (url-str "https://localhost:443")))
  (is (= "https://localhost:8443" (url-str "https://localhost:8443")))
  (is (= "http://localhost" (str (map->URL {:host "localhost" :protocol "http"})))))

(deftest query-params
  (are [query map] (is (= map (query->map query)))
    "a=b" {"a" "b"}
    "a=1&b=2&c=3" {"a" "1" "b" "2" "c" "3"}
    "a=" {"a" ""}
    "a" {"a" ""}
    nil nil
    "" nil))

(deftest user-info-edgecases
  (are [user-info url-string] (= user-info ((juxt :username :password) (url url-string)))
    ["a" nil] "http://a@foo"
    ["a" nil] "http://a:@foo"
    ["a" "b:c"] "http://a:b:c@foo"))

(deftest path-normalization
  (is (= "http://a/" (url-str "http://a/b/c/../..")))
  
  (is (= "http://a/b/c" (url-str "http://a/b/" "c")))
  (is (= "http://a/b/c" (url-str "http://a/b/.." "b" "c")))
  (is (= "http://a/b/c" (str (url "http://a/b/..////./" "b" "c" "../././.." "b" "c"))))
  (is (= "http://a/" (str (url "http://a/b/..////./" "b" "c" "../././.." "b" "c" "/"))))
  
  (is (= "http://a/x" (str (url "http://a/b/c" "/x"))))
  (is (= "http://a/" (str (url "http://a/b/c" "/"))))
  (is (= "http://a/" (str (url "http://a/b/c" "../.."))))
  (is (= "http://a/x" (str (url "http://a/b/c" "../.." "." "./x")))))

(deftest anchors
  (is (= "http://a#x" (url-str "http://a#x")))
  (is (= "http://a?b=c#x" (url-str "http://a?b=c#x")))
  (is (= "http://a?b=c#x" (-> "http://a#x" url (assoc :query {:b "c"}) str))))

(deftest no-bare-?
  (is (= "http://a" (-> "http://a?b=c" url (update-in [:query] dissoc "b") str))))

