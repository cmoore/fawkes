
(ns ivy.fawkes.ext.beton.resetxp
  (:import [pl.betoncraft.betonquest.core QuestEvent]
           [org.bukkit Bukkit]
           [org.bukkit.event Listener])
  
  (:gen-class :name ivy.fawkes.ext.beton.ResetXP
              :init resetxp
              :extends pl.betoncraft.betonquest.core.QuestEvent
              :implements [org.bukkit.event.Listener]))

(defn -resetxp [player instructions]
  (let [player (.getPlayer (Bukkit/getServer) player)]
    (.setLevel player 1)))
