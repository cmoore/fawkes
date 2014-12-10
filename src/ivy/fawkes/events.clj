;; -*- Mode: Clojure; eval: (hs-hide-all) -*-

(ns ivy.fawkes.events
  
  (:require [cljminecraft.events :as events]
            [ivy.fawkes.blockloader :as blocker])
  
  (:import [org.bukkit Material]
           [org.bukkit.entity Player]
           [org.bukkit.block Chest]
           [org.bukkit.inventory ItemStack]
           [org.bukkit.metadata FixedMetadataValue]
           [org.bukkit.event.player PlayerInteractEvent]
           [org.bukkit.event.block Action])
  
  (:use [clojure.string :only [join]]))

(defonce ^:dynamic fawkes (atom nil))


(defn say-user [event message]
  (.sendMessage (.getPlayer event) message))

(defn on-player-interact [event]
  (let [player (.getPlayer event)
        block (.getClickedBlock event)
        in-hand (.getItem event)]
    (when (and in-hand block)
      (when (and (=
                  (.equals (.getType block) Material/CHEST)
                  (.equals (.getType in-hand) Material/STICK)))
        
        (when (.getLore (.getItemMeta in-hand))
          (let [lore (.get (.getLore (.getItemMeta in-hand)) 0)]
            
            (when (= lore "view")
              (let [metadata (.getMetadata (.getState block) "ivy.loot")]
                (if (> (.size metadata) 0)
                  (.sendMessage player (join " " ["Metadata:" (.value (.get metadata 0))]))
                  (.sendMessage player "No metadata!"))))
            
            (when (= lore "regular")
              (blocker/save-block block "ivy.loot" "regular")
              (.setMetadata block "ivy.loot" (new FixedMetadataValue @fawkes "regular"))
              (.sendMessage player "Loot type set."))
            
            (when (= lore "large")
              (blocker/save-block block "ivy.loot" "large")
              (.setMetadata block "ivy.loot" (new FixedMetadataValue @fawkes "large"))
              (.sendMessage player "Loot type set."))
            
            (when (= lore "murca")
              (blocker/save-block block "ivy.loot" "murca")
              (.setMetadata block "ivy.loot" (new FixedMetadataValue @fawkes "murca"))
              (.sendMessage player "Loot type set."))))))))

(defn events []
  [(events/event "player.player-interact" #'on-player-interact)])

(defn start [plugin]
  (reset! fawkes plugin)
  (.info (.getLogger @fawkes) "Loading events.")
  (events/register-eventlist @fawkes (events)))
