(ns leiningen.expectations
  (:import (java.io File))
  (:require [leiningen.core.main]))

(def ^:dynamic *exit-after-tests* true)

(defn eval-in-project
  "Support eval-in-project in both Leiningen 1.x and 2.x."
  [project form init]
  (let [[eip two?] (or (try (require 'leiningen.core.eval)
                            [(resolve 'leiningen.core.eval/eval-in-project)
                             true]
                            (catch java.io.FileNotFoundException _))
                       (try (require 'leiningen.compile)
                            [(resolve 'leiningen.compile/eval-in-project)]
                            (catch java.io.FileNotFoundException _)))]
    (if two?
      (eip project form init)
      (eip project form nil nil init))))

(defn namespaces-in-dir
  "Support namespaces-in-dir in both Leiningen 1.x and 2.x."
  [dir]
  (let [nid (or (try (require 'leiningen.util.ns)
                            (resolve 'leiningen.util.ns/namespaces-in-dir)
                            (catch java.io.FileNotFoundException _))
                       (try (require 'bultitude.core)
                            (resolve 'bultitude.core/namespaces-in-dir)
                            (catch java.io.FileNotFoundException _)))]
    (nid dir)))

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
  (let [paths (if (:test-path project)
                [(:test-path project)]
                (:test-paths project))
        ns (->> (mapcat namespaces-in-dir paths)
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
     '(require ['expectations]))
    (if (and (.exists results) (pos? (.length results)))
      (when-not (let [summary (read-string (slurp path))]
                  (zero? (+ (:fail summary) (:error summary))))
        (leiningen.core.main/abort))
      (leiningen.core.main/abort "Unable to read results."))))
