(defproject lein-expectations "0.0.8-SNAPSHOT"
  :description "Leiningen plugin to run tests written using the expectations library."
  :url "https://github.com/gar3thjon3s/lein-expectations"
  :dependencies [[expectations/expectations "1.4.5"]]
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :expectations/show-finished-ns true
  :expectations/show-finished-expectation true
  :eval-in-leiningen true)
