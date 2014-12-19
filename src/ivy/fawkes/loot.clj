
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


;; (defn inject-loot []
;;   (let [db (mg/get-db @mongo "fawkes")]
;;     (doall
;;      (map (fn [record]
;;             (mc/insert db "loot" record))
;;           bronze))))

;; (defn inject-more-loot []
;;   (let [db (mg/get-db @mongo "fawkes")]
;;     (doall
;;      (map (fn [record]
;;             (mc/insert db "loot" record))
;;           [{:item "GOLDEN_PICKAXE"
;;             :name "Gold Pickensticker"
;;             :enchant "DIG_SPEED"
;;             :ilevel "bronze"
;;             :level 2}
;;            {:item "GOLDEN_AXE"
;;             :name "Gold Whackencutter"
;;             :enchant "DIG_SPEED"
;;             :ilevel "bronze"
;;             :level 2}
;;            {:item "GOLDEN_SHOVEL"
;;             :name "Gold Scoopypokey"
;;             :enchant "DIG_SPEED"
;;             :ilevel "bronze"
;;             :level 2}
;;            {:item "SADDLE"
;;             :name "Rooty Scooty on a Horsey"
;;             :ilevel "bronze"}
;;            {:item "BOW"
;;             :name "Rooty Tooty Point and Shooty"
;;             :enchant "ARROW_INFINITE"
;;             :ilevel "bronze"
;;             :level 1}
;;            {:item "FISHING_ROD"
;;             :enchant "LURE"
;;             :ilevel "bronze"
;;             :level 1}]))))


;; (def bronze [{:item "IRON_PICKAXE"
;;               :name "Bronze Whacker"
;;               :enchant "DIG_SPEED"
;;               :ilevel "bronze"
;;               :level 1}
                
;;              {:item "IRON_SWORD"
;;               :name "Bronze Slapper"
;;               :enchant "DAMAGE_ALL"
;;               :ilevel "bronze"
;;               :level 1}
                
;;              {:item "IRON_AXE"
;;               :name "Bronze Axer"
;;               :enchant "DIG_SPEED"
;;               :ilevel "bronze"
;;               :level 1}
                
;;              {:item "IRON_SPADE"
;;               :name "Bronze Digger"
;;               :enchant "DIG_SPEED"
;;               :ilevel "bronze"
;;               :level 1}
                
;;              {:item "IRON_HELMET"
;;               :name "Bronze Brainbucket"
;;               :enchant "DURABILITY"
;;               :ilevel "bronze"
;;               :level 1}

;;              {:item "IRON_CHESTPLATE"
;;               :name "Bronze Chest"
;;               :enchant "DURABILITY"
;;               :ilevel "bronze"
;;               :level 1}

;;              {:item "IRON_LEGGINGS"
;;               :name "Iron Legs"
;;               :enchant "DURABILITY"
;;               :ilevel "bronze"
;;               :level 1}

;;              {:item "IRON_BOOTS"
;;               :name "Iron Boots"
;;               :enchant "DURABILITY"
;;               :ilevel "bronze"
;;               :level 1}])
