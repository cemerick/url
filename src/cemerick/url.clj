(ns cemerick.url
  (:import java.net.URLEncoder)
  (:require [pathetic.core :as pathetic])
  (:use [clojure.core.incubator :only (-?> -?>>)]
        [clojure.string :only (replace)])
  (:refer-clojure :exclude (replace)))

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
           path
           (when query (str \? (if (string? query)
                                 query
                                 (map->query query))))))))

(defn url
  "Returns a new URL record for the given url string(s)."
  ([url]
    (if (instance? URL url)
      url
      (let [url (java.net.URL. url)
            [user pass] (.split ^String (or (.getUserInfo url) "") ":" 2)]
        (URL. (.toLowerCase (.getProtocol url))
              (and (seq user) user)
              (and (seq pass) pass)
              (.getHost url)
              (.getPort url)
              (.getPath url)
              (query->map (.getQuery url))))))
  ([base-url & path-segments]
    (let [base-url (if (instance? URL base-url) base-url (url base-url))
          path (->> (mapcat #(.split ^String % "/") path-segments)
                 (map url-encode)
                 (cons (:path base-url))
                 (interpose \/)
                 (apply str))]
      (assoc base-url :path (pathetic/normalize path)))))

(def ^{:doc "Same as `url`, but returns a string of the result.
Useful for concisely navigating around a URL with relative paths,
or to easily normalize a single url string."}
      url-str (comp str url))
