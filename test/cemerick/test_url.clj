(ns cemerick.test-url
  (:import java.net.URL)
  (:use cemerick.url
        clojure.test))

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
  (is (= "http://localhost:5984/foo/bar" (str (url "http://localhost:5984" "foo" "bar"))))
  (is (= "http://localhost:5984/foo/bar/baz" (str (url "http://localhost:5984" "foo" "bar" "baz"))))
  (is (= "http://localhost:5984/foo/bar/baz" (str (url (url "http://localhost:5984" "foo") "bar" "baz")))))

(deftest port-normalization
  (is (== -1 (-> "https://foo" url str URL. .getPort)))
  (is (= "http://localhost" (str (url "http://localhost"))))
  (is (= "http://localhost" (str (url "http://localhost:80"))))
  (is (= "http://localhost:8080" (str (url "http://localhost:8080"))))
  (is (= "https://localhost" (str (url "https://localhost"))))
  (is (= "https://localhost" (str (url "https://localhost:443"))))
  (is (= "https://localhost:8443" (str (url "https://localhost:8443")))))

(deftest dupe-slash-normalization
  (is (= "http://foo/bar/baz" (str (url "http://foo/bar/" "baz")))))