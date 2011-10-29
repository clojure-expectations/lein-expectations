(ns leiningen.expectations
  (:use [leiningen.compile :only [eval-in-project]]
        [leiningen.util.ns :only [namespaces-in-dir]]))

(defn matching-ns?
  [to-match]
  (let [to-match (map re-pattern to-match)]
    (fn [ns]
      (if (empty? to-match)
        ns
        (->> (for [m to-match]
               (re-matches m (name ns)))
             (some identity))))))

(defn expectations
  "Executes expectation tests in your project.
   By default all test namespaces will be run, or you can specify
   which namespaces to run using regex syntax to filter."
  [project & args]
  (let [ns (->> (namespaces-in-dir (:test-path project))
                (filter (matching-ns? args)))]
    (eval-in-project
     project
     `(do
        (doseq [n# '~ns]
          (require n# :reload)))
     nil
     nil
     '(require ['expectations]))))

