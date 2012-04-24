# url

This is a library that makes working with URLs in Clojure a little more
pleasant.

The `cemerick.url/url` function returns an instance of the
`cemerick.url.URL` record type that allows you to easily work with each
datum within the provided URL:

```clojure
=> (use 'cemerick.url)
nil
=> (-> (url "https://api.stripe.com/v1/charges")
     (assoc :username "vtUQeOtUnYr7PGCLQ96Ul4zqpDUO4sOE")
     str)
"https://vtUQeOtUnYr7PGCLQ96Ul4zqpDUO4sOE:@api.stripe.com/v1/charges"
```

`url` will also accept additional paths to be resolved against the path
in the base URL:

```clojure
=> (url "https://api.twitter.com/")
#cemerick.url.URL{:protocol "https", :username nil, :password nil,
                  :host "api.twitter.com", :port -1, :path nil, :query nil,
                  :anchor nil}
=> (url "https://api.twitter.com/" "1" "users" "profile_image" "cemerick")
#cemerick.url.URL{:protocol "https", :username nil, :password nil,
                  :host "api.twitter.com", :port -1,
                  :path "/1/users/profile_image/cemerick", :query nil, :anchor nil}
=> (str *1)
"https://api.twitter.com/1/users/profile_image/cemerick"
=> (str (url "https://api.twitter.com/1/users/profile_image/cemerick" "../../lookup.json"))
"https://api.twitter.com/1/users/lookup.json"
```

Note that `url` does not perform any url-encoding of paths.  Use
`cemerick.url/url-encode` to url-encode any paths/path components prior
to passing them to `url`.  e.g.:

```clojure
=> (def download-root "http://foo.com/dl")
#'cemerick.test-url/download-root
=> (str (url download-root "/"))
"http://foo.com"
=> (str (url download-root (url-encode "/")))
"http://foo.com/dl/%2F"
=> (str (url download-root (url-encode "/logical/file/path")))
"http://foo.com/dl/%2Flogical%2Ffile%2Fpath"
```

The `:query` slot can be a string or a map of params:

```clojure
=> (str (assoc *2 :query {:a 5 :b 6}))
"https://api.twitter.com/1/users/profile_image/cemerick?a=5&b=6"
```

## "Installation"

url is available in Clojars. Add this `:dependency` to your Leiningen
`project.clj`:

```clojure
[com.cemerick/url "0.0.4"]
```

Or, add this to your Maven project's `pom.xml`:

```xml
<repository>
  <id>clojars</id>
  <url>http://clojars.org/repo</url>
</repository>

<dependency>
  <groupId>com.cemerick</groupId>
  <artifactId>url</artifactId>
  <version>0.0.4</version>
</dependency>
```

url is compatible with Clojure 1.2.0 - 1.4.0.

## Need Help?

Ping `cemerick` on freenode irc or
[twitter](http://twitter.com/cemerick) if you have questions or would
like to contribute patches.

## License

Copyright ©2012 [Chas Emerick](http://cemerick.com)

Distributed under the Eclipse Public License, the same as Clojure.
Please see the `epl-v10.html` file at the top level of this repo.
