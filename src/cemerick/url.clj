(ns cemerick.url
  (:import java.net.URLEncoder)
  (:use [clojure.core.incubator :only (-?> -?>>)]))

(defn- url-encode
  [string]
  (-?> string str (URLEncoder/encode "UTF-8") (.replace "+" "%20")))

(defn- map->query
  [m]
  (-?>> (seq m)
        sort                     ; sorting makes testing a lot easier :-)
        (map (fn [[k v]]
               [(url-encode (name k))
                "="
                (url-encode (str v))]))
        (interpose "&")
        flatten
        (apply str)))

(defn- query->map
  [qstr]
  (when qstr
    (-?>> (.split qstr "&")
      seq
      (map #(.split % "="))
      (map vec)
      (into {}))))

(defn- port-str
  [protocol port]
  (when (and (not= -1 port)
             (not (and (== port 80) (= protocol "http")))
             (not (and (== port 443) (= protocol "https"))))
    (str ":" port)))

(defn- url-creds
  [username password]
  (when username
    (str username ":" password)))

(defrecord URL
  [protocol username password host port path query]
  Object
  (toString [this]
    (let [creds (url-creds username password)]
      (str protocol "://"
           creds
           (when creds \@)
           host
           (port-str protocol port)
           \/ path
           (when query (str \? (if (string? query)
                                 query
                                 (map->query query))))))))

(defn url
  ([db]
    (if (instance? URL db)
      db
      (let [url (java.net.URL. db)
            [_ user pass] (re-matches #"([^:]+):(.*$)" (or (.getUserInfo url) ""))]
        (URL. (.toLowerCase (.getProtocol url))
              user
              pass
              (.getHost url)
              (.getPort url)
              (-> url .getPath (.replaceAll "^/" ""))
              (query->map (.getQuery url))))))
  ([base & path-segments]
    (let [base (if (instance? URL base) base (url base))]
      (assoc base
        :path (->> (map url-encode path-segments)
                (interpose \/)
                (apply str (when (seq (:path base))
                             (str (:path base) \/))))))))

