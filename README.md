# astrogator

## Build & Deploy

* build server: `lein with-profile corejar uberjar`
* build cljs-resources: `lein cljsbuild once`
* deploy: `git push heroku master`

## License

Copyright © 2016 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

##TODO
Move to Heroku-cloud (clj native)
https://www.heroku.com/
https://www.heroku.com/java?lang=clojure
https://devcenter.heroku.com/articles/getting-started-with-clojure
https://stackoverflow.com/questions/51356930/how-to-deploy-and-run-clojurescript-application-in-heroku