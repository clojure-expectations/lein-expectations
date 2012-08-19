(ns sample-expectations2
  (:use expectations))

(expect 1 1)

(expect 1 (do (Thread/sleep 1000) 1))
(expect 1 (do (Thread/sleep 1000) 1))
(expect 1 (do (Thread/sleep 1000) 1))
(expect 1 (do (Thread/sleep 1000) 1))
(expect 1 (do (Thread/sleep 1000) 1))
(expect 1 (do (Thread/sleep 1000) 1))
