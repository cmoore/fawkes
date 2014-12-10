;; -*- Mode: Clojure; eval: (hs-hide-all) -*-

(ns ivy.fawkes.commands

  (:use [clojure.string :only (join)])

  (:import [org.bukkit Material]
           [org.bukkit.entity Player]
           [org.bukkit.inventory ItemStack])
  
  (:require [ivy.fawkes.util :as u]
            [cljminecraft.commands :as cmd]))


(defonce ^:dynamic fawkes (atom nil))

(defn make-sticks [^Player player]
  (let [stick (.new ItemStack Material/STICK 1)
        meta (.getItemMeta stick)
        lore (.new java.util.ArrayList)]
    (.add lore "regular")
    (.setLore meta lore)
    (.setDisplayName meta "Stick of Loots.")
    (.setItemMeta stick meta)

    (.. player getInventory addItem stick)))


(defn get-random-item []
  (let [values (Material/values)]
    (nth values (rand (count values)))))

(defn handle-fks [sender subcommand]
  (when (= subcommand "test")
    (.sendMessage sender "Looking good.")
    true)
  (when (= subcommand "reloot")
    
    (map (fn [chest]
           (.info (.getLogger @fawkes) "Chest...")
           (when (.getMetadata chest "ivy.loot")
             (.info (.getLogger @fawkes) "FOUND A TAGGED CHEST")
             (let [inventory (.getInventory chest)
                   times (rand-int 10)]
               (.clear inventory)
               (dotimes [_ 8] (let [istack (.new ItemStack (get-random-item))]
                                (.sendMessage sender "Adding an item.")
                                (.addItem inventory istack))))))
         
         (u/find-all-chests (.getWorld sender)))
    
    (.sendMessage sender "Reloot command.")
    true)
  false)

(defn start [instance]
  (reset! fawkes instance)
  (cmd/register-command instance "fks" #'handle-fks :string))
