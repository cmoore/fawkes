
(ns ivy.fawkes.ext.beton.testevent
  
  (:import [pl.betoncraft.betonquest.core QuestEvent]
           [org.bukkit Bukkit]
           [org.bukkit.event Listener])
  
  (:gen-class :name ivy.fawkes.ext.beton.TestEvent
              :init testevent
              :extends pl.betoncraft.betonquest.core.QuestEvent
              :implements [org.bukkit.event.Listener]))

(defn -testevent [player instructions]
  (println "Yes, ok. fine.")
  (.info (Bukkit/getLogger) "OMG YES IT WORKS"))
