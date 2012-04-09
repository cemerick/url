(ns cemerick.test-url
  (:import java.net.URL)
  (:use cemerick.url
        clojure.test))

(def url-str (comp str url))

(deftest test-map-to-query-str
  (are [x y] (= x (#'cemerick.url/map->query y))
       "a=1&b=2&c=3" {:a 1 :b 2 :c 3}
       "a=1&b=2&c=3" {:a "1"  :b "2" :c "3"}
       "a=1&b=2" {"a" "1" "b" "2"}))

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
  (is (== -1 (-> "https://foo" url-str URL. .getPort)))
  (is (= "http://localhost" (url-str "http://localhost")))
  (is (= "http://localhost" (url-str "http://localhost:80")))
  (is (= "http://localhost:8080" (url-str "http://localhost:8080")))
  (is (= "https://localhost" (url-str "https://localhost")))
  (is (= "https://localhost" (url-str "https://localhost:443")))
  (is (= "https://localhost:8443" (url-str "https://localhost:8443"))))

(deftest user-info-edgecases
  (are [user-info url-string] (= user-info ((juxt :username :password) (url url-string)))
    ["a" nil] "http://a@foo"
    ["a" nil] "http://a:@foo"
    ["a" "b:c"] "http://a:b:c@foo"))

(deftest path-normalization
  (is (= "http://a" (url-str "http://a/b/c/../..")))
  
  (is (= "http://a/b/c" (url-str "http://a/b/" "c")))
  (is (= "http://a/b/c" (url-str "http://a/b/.." "b" "c")))
  (is (= "http://a/b/c" (str (url "http://a/b/..////./" "b" "c" "../././.." "b" "c"))))
  (is (= "http://a" (str (url "http://a/b/..////./" "b" "c" "../././.." "b" "c" "/"))))
  
  (is (= "http://a/x" (str (url "http://a/b/c" "/x"))))
  (is (= "http://a" (str (url "http://a/b/c" "/"))))
  (is (= "http://a" (str (url "http://a/b/c" "../.."))))
  (is (= "http://a/x" (str (url "http://a/b/c" "../.." "." "./x")))))
