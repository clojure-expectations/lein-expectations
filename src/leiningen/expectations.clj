(ns leiningen.expectations
  (:use [leiningen.compile :only [eval-in-project]]
        [leiningen.util.ns :only [namespaces-in-dir]])
  (:import (java.io File)))

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
                (filter (matching-ns? args)))
        results (doto (File/createTempFile "lein" "result") .deleteOnExit)
        path (.getAbsolutePath results)]
    (eval-in-project
     project
     `(do
        (expectations/disable-run-on-shutdown)
        (doseq [n# '~ns]
          (require n# :reload))
        (let [summary# (expectations/run-all-tests)]
          (with-open [w# (-> (java.io.File. ~path)
                             (java.io.FileOutputStream.)
                             (java.io.OutputStreamWriter.))]
            (.write w# (pr-str summary#))))
        (shutdown-agents))
     nil
     nil
     '(require ['expectations]))
    (if (and (.exists results) (pos? (.length results)))
      (let [summary (read-string (slurp path))
            success? (zero? (+ (:fail summary) (:error summary)))]
        (if success? 0 1))
      1)))

