;; -*- Mode: Clojure; eval: (hs-hide-all) -*-

(ns ivy.fawkes.commands
  (:refer-clojure :exclude [update])
  
   (:use [clojure.string :only (join)])

  (:import [org.bukkit Material]
           [org.bukkit.block Chest BlockState]
           [org.bukkit.entity Player]
           [org.bukkit.inventory ItemStack])
  
  (:require [clojure.core :as core]
            [ivy.fawkes.util :as u]
            [cljminecraft.commands :as cmd]))

(use '[ivy.fawkes.events :only [region-for-entity]])

(defonce ^:dynamic fawkes (atom nil))

(defn get-random-item []
  (let [values (Material/values)]
    (nth values (rand (count values)))))

(defn make-key [name lore amount]
  (let [new-key (ItemStack. Material/TRIPWIRE_HOOK (u/parse-int amount))
        key-meta (.getItemMeta new-key)]
    (.setLore key-meta [lore])
    (.setDisplayName key-meta name)
    (.setItemMeta new-key key-meta)
    new-key))

; /fkey <name> <amount>
(defn handle-fkey [sender type name amount]
  (let [new-key (cond (= type "bronze")  (make-key "Bronze Key of Lootey Loots" "Bronze Key" amount)
                      (= type "silver")  (make-key "Silver Key of Pretty Good Loots" "Silver Key" amount)
                      (= type "gold")    (make-key "Gold Key of Sweet Loots" "Gold Key" amount)
                      (= type "godtier") (make-key "God Tier Loot Key" "God Tier Key" amount))]
    
    (let [the-player (first (filter (fn [player]
                                      (.info (.getLogger @fawkes) (format "Player: %s" (.getName player)))
                                      (.equals name (.getName player)))
                                    (.getPlayers (.getWorld sender))))]
      (if the-player
        (do
          (u/add-to-inventory the-player new-key)
          (.sendMessage sender "Done."))
        (.sendMessage sender "I can't find that player.")))))

(defn handle-fks [sender subcommand]
  (when (.hasPermission sender "fawkes.fks")

    (when (= subcommand "test")
      (.sendMessage sender "Looking good.")
      true)
    
    ;; (when (= subcommand "chest-scan")
    ;;   (bl/confirm-blocks sender (.getWorld sender)))
    
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
  
  (cmd/register-command instance "fkey" #'handle-fkey :string :string :string)
  (cmd/register-command instance "fks" #'handle-fks :string))
