
(ns ivy.fawkes.loot
  (:import [org.bukkit Bukkit Material]
           [org.bukkit.enchantments Enchantment]
           [org.bukkit.inventory ItemStack])
  
  (:require [ivy.fawkes.ext.essentials :as ess]
            [monger.core :as mg]
            [monger.collection :as mc]))

(defonce ^:dynamic mongo (atom nil))
(defonce ^:dynamic fawkes (atom nil))

(defn make-item [player {item-type :item
                         name :name
                         enchant :enchant
                         level :level
                         ilevel :ilevel
                         money :money
                         money-amount :money-amount}]
  
  (cond (and money money-amount) (ess/add-money (.getName player) money-amount)
        item-type (let [item (ItemStack. (Material/valueOf item-type) 1)
                        meta (.getItemMeta item)]
                    (when (and enchant level)
                      (.addEnchant meta (.get (.getField Enchantment enchant) nil) level false))
                    (when item
                      (.setLore meta ["Bronze Reward"]))
          
                    (when name
                      (.setDisplayName meta name))

                    (when (and name item)
                      (.setItemMeta item meta))
                    item)))

(defn get-random-loot [ilevel]
  (rand-nth
   (mc/find-maps (mg/get-db @mongo "fawkes")
                 "loot"
                 {:ilevel ilevel})))

(defn start [instance]
  (reset! fawkes instance)
  (reset! mongo (mg/connect)))
