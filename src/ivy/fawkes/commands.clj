;; -*- Mode: Clojure; eval: (hs-hide-all) -*-

(ns ivy.fawkes.commands

  (refer-clojure :exclude [update])
  
  (:import [org.bukkit Material Bukkit]
           [org.bukkit.command CommandExecutor CommandSender TabExecutor]
           [org.bukkit.block Chest BlockState]
           [org.bukkit.entity Player]
           [org.bukkit.inventory ItemStack])
 
  (:require [ivy.fawkes.util :as u]
            [ivy.fawkes.block :as bl]
            [ivy.fawkes.loot :as loot]

            [cljminecraft.commands :as cmd]
            [cljminecraft.logging :as log]

            [clojure.tools.nrepl.server :as nrepl]
            [cider.nrepl :refer (cider-nrepl-handler)]))

(defonce ^:dynamic fawkes (atom nil))
(defonce ^:dynamic nrepl (atom nil))

(defn start-nrepl []
  (when (not @nrepl)
    (reset! nrepl (nrepl/start-server :port 7888 :handler cider-nrepl-handler))))

(defn stop-nrepl []
  (when @nrepl
    (nrepl/stop-server @nrepl)
    (reset! nrepl nil)))

(defn make-key [name lore amount]
  (let [new-key (ItemStack. Material/TRIPWIRE_HOOK (u/parse-int amount))
        key-meta (.getItemMeta new-key)]
    (.setLore key-meta [lore])
    (.setDisplayName key-meta name)
    (.setItemMeta new-key key-meta)
    new-key))

(defn handle-fkey [sender type name amount]
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

(defn handle-fks [sender subcommand]
  (when (.hasPermission sender "fawkes.fks")

    (when (= subcommand "reloot")
      (bl/reloot-chests)
      (.sendMessage sender "Done!"))
    
    (when (= subcommand "repl")
      (if @nrepl
        (do (log/info "Stopping repl.")
            (stop-nrepl))
        (do (log/info "Starting repl.")
            (start-nrepl)))
      true)
    
    (when (= subcommand "prune")
      (bl/prune-chests (.getWorld sender)))
        
    (when (= subcommand "scan")
      (bl/confirm-blocks sender (.getWorld sender))
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
