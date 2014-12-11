;; -*- Mode: Clojure; eval: (hs-hide-all) -*-

(ns ivy.fawkes.commands

  (:use [clojure.string :only (join)])

  (:import [org.bukkit Material]
           [org.bukkit.block Chest BlockState]
           [org.bukkit.entity Player]
           [org.bukkit.inventory ItemStack]
           [com.mongodb MongoOptions ServerAddress])
  
  (:require [ivy.fawkes.util :as u]
            [ivy.fawkes.blockloader :as bl]

            [monger.core :as mg]
            [monger.collection :as mc]
            [cljminecraft.commands :as cmd]))

(use '[ivy.fawkes.events :only [region-for-entity]])


(defonce ^:dynamic fawkes (atom nil))


(defn get-random-item []
  (let [values (Material/values)]
    (nth values (rand (count values)))))

(defn handle-frange [sender min max]
    (let [conn (mg/connect)
          db (mg/get-db conn "fawkes")
          collection "range"
          region (region-for-entity sender)]
      (cond (= region "global") (do
                                  (.sendMessage sender "Cowardly refusing to set a range on the global zone.")
                                  nil)
            :else (do
                    (mc/remove db collection {:region region})
                    (mc/insert db collection {:region region
                                              :min min
                                              :max max})
                    (.sendMessage sender "Mob level range for region set.")))))

(defn handle-fks [sender subcommand]
  (when (.hasPermission sender "fawkes.fks")
    
    (when (= subcommand "test")
      (.sendMessage sender "Looking good.")
      true)
    
    (when (= subcommand "chest-scan")
      (bl/confirm-blocks sender (.getWorld sender)))
    
    (when (= subcommand "reloot")
      (doall (map (fn [chunk]
                    (doall (map (fn [blockstate]
                                  (when (= (.getType blockstate) Material/CHEST)
                                    (when (< 0 (.size (.getMetadata blockstate "ivy.loot")))
                                      (.sendMessage sender "§9Found a tagged chest."))))
                                (.getTileEntities chunk))))
                  (.getLoadedChunks (.getWorld sender))))
      true)

    (when (= subcommand "sticks")
      (let [regular-stick (ItemStack. Material/STICK 1)
            regular-meta (.getItemMeta regular-stick)
            
            high-stick (ItemStack. Material/STICK 1)
            high-meta (.getItemMeta high-stick)
            
            murca-stick (ItemStack. Material/STICK 1)
            murca-meta (.getItemMeta murca-stick)

            view-stick (ItemStack. Material/STICK 1)
            view-meta (.getItemMeta view-stick)]
        
        (.setLore regular-meta ["regular"])
        (.setDisplayName regular-meta "Stick of Loots")
        (.setItemMeta regular-stick regular-meta)

        (.setLore high-meta ["large"])
        (.setDisplayName high-meta "Stick of Good Loots.")
        (.setItemMeta high-stick high-meta)

        (.setLore murca-meta ["murca"])
        (.setDisplayName murca-meta "Stick of AMAZING LOOTS")
        (.setItemMeta murca-stick murca-meta)

        (.setLore view-meta ["view"])
        (.setDisplayName view-meta "Stick of viewing.")
        (.setItemMeta view-stick view-meta)
        
        (let [inventory (.getInventory sender)]
                                        ; variable length arguments wtf omg.
          (.addItem inventory (doto (make-array ItemStack 4)
                                (aset 0 regular-stick)
                                (aset 1 high-stick)
                                (aset 2 murca-stick)
                                (aset 3 view-stick))))))))

(defn start [instance]
  (reset! fawkes instance)
  (cmd/register-command instance "frange" #'handle-frange :string :string)
  (cmd/register-command instance "fks" #'handle-fks :string))
