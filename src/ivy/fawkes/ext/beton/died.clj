;; -*- Mode: Clojure; eval: (hs-hide-all) -*-

(ns ivy.fawkes.ext.beton.died
  
  (import [pl.betoncraft.betonquest.core Condition]
          [org.bukkit Bukkit])

  (:gen-class :name ivy.fawkes.ext.beton.Died
              :extends pl.betoncraft.betonquest.core.Condition))

(defn -isMet [player instructions]
  (.info (Bukkit/getLogger) (format "isMet called with %s %s" player instructions)))
