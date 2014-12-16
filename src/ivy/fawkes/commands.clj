;; -*- Mode: Clojure; eval: (hs-hide-all) -*-

(ns ivy.fawkes.commands

  (refer-clojure :exclude [update])
  
  (:import [org.bukkit Material Bukkit]
           [org.bukkit.command CommandExecutor CommandSender TabExecutor]
           [org.bukkit.block Chest BlockState]
           [org.bukkit.entity Player]
           [org.bukkit.inventory ItemStack])
 
  (:require [ivy.fawkes.util :as u]
            [ivy.fawkes.block :as bl]))

(defonce ^:dynamic fawkes (atom nil))

(defn make-key [name lore amount]
  (let [new-key (ItemStack. Material/TRIPWIRE_HOOK (u/parse-int amount))
        key-meta (.getItemMeta new-key)]
    (.setLore key-meta [lore])
    (.setDisplayName key-meta name)
    (.setItemMeta new-key key-meta)
    new-key))

(defn handle-fks [sender subcommand]
  (when (.hasPermission sender "fawkes.fks")

    (when (= subcommand "test")
      (doall (map (fn [block]
                    (u/log fawkes "Found one."))
                  (bl/find-all-blocks (.getWorld sender) Material/REDSTONE_ORE)))
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
                                (aset 3 view-stick)))))))
  false)

(defn handle-fkey [sender type player amount]
  (let [new-key (cond (= type "bronze")  (make-key "Bronze Key of Lootey Loots" "Bronze Key" amount)
                      (= type "silver")  (make-key "Silver Key of Pretty Good Loots" "Silver Key" amount)
                      (= type "gold")    (make-key "Gold Key of Sweet Loots" "Gold Key" amount)
                      (= type "godtier") (make-key "God Tier Loot Key" "God Tier Key" amount))]
    
    (let [the-player (first (filter (fn [player]
                                      (.equals name (.getName player)))
                                    (.getPlayers (.getWorld sender))))]
      (if the-player
        (do
          (u/add-to-inventory the-player new-key)
          (.sendMessage sender "Done."))
        (.sendMessage sender "I can't find that player.")))))

(defn start [instance]
  (reset! fawkes instance)

  (doto (.getCommand instance "fks")
    (.setExecutor (proxy [CommandExecutor] []
                    (onCommand [sender command label args]
                      (handle-fks sender (first args))))))
  
  (doto (.getCommand instance "fkey")
    (.setExecutor (proxy [CommandExecutor] []
                    (onCommand [sender command label args]
                      (let [type (nth args 0)
                            player (nth args 1)
                            amount (nth args 2)]
                        (handle-fkey sender type player amount))))))

  (doto (.getCommand instance "fkx")
    (.setExecutor (proxy [CommandExecutor] []
                    (onCommand [sender command label args]
                      (.info (Bukkit/getLogger) (format "fkx called with cmd: %s alias: %s args: %s" command alias args))
                      true))))
  
  ;  (cmd/register-command instance "fkey" #'handle-fkey :string :string :string)
  ; (cmd/register-command instance "fks" #'handle-fks :string)
  )
