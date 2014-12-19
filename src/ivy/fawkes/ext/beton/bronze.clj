;; -*- Mode: Clojure; eval: (hs-hide-all) -*-

(ns ivy.fawkes.ext.beton.bronze
  (:import [pl.betoncraft.betonquest.core QuestEvent]
           [org.bukkit Bukkit Material]
           [org.bukkit.enchantments Enchantment]
           [org.bukkit.inventory ItemStack]
           [org.bukkit.entity Player]
           [org.bukkit.event Listener])

  (:require [ivy.fawkes.util :as u]
            [ivy.fawkes.loot :as loot])
  
  (:gen-class :name ivy.fawkes.ext.beton.BronzeReward
              :init bronzereward
              :extends pl.betoncraft.betonquest.core.QuestEvent
              :implements [org.bukkit.event.Listener]))


(defn -bronzereward [player instructions]
  (.info (Bukkit/getLogger) (format "Giving bronze reward to %s." player))

  (let [player (.getPlayer (Bukkit/getServer) player)]
    (dotimes [x 4]
      (let [item (loot/make-item player (loot/get-random-loot "bronze"))]
        (when item
          (u/add-to-inventory player item)))))
  
  (.info (Bukkit/getLogger) (format "Done!")))
