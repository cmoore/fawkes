
(ns ivy.fawkes.ext.beton.bronze
  (:import [pl.betoncraft.betonquest.core QuestEvent]
           [org.bukkit Bukkit Material]
           [org.bukkit.enchantments Enchantment]
           [org.bukkit.inventory ItemStack]
           [org.bukkit.entity Player]
           [org.bukkit.event Listener])

  (:require [ivy.fawkes.util :as u]
            [ivy.fawkes.ext.essentials :as ess])
  
  (:gen-class :name ivy.fawkes.ext.beton.BronzeReward
              :init bronzereward
              :extends pl.betoncraft.betonquest.core.QuestEvent
              :implements [org.bukkit.event.Listener]))

(defn make-item [player {item :item
                         name :name
                         enchant :enchant
                         level :level
                         money :money
                         money-amount :money-amount}]
  
  (cond (and money money-amount) (ess/add-money (.getName player) money-amount)
        item (let [meta (.getItemMeta item)]
               (when (and enchant level)
                 (.addEnchant meta enchant level false))
               (when item
                 (.setLore meta ["Bronze Reward"]))
          
               (when name
                 (.setDisplayName meta name))
          
               (.setItemMeta item meta)
               item)))

(defn -bronzereward [player instructions]
  (.info (Bukkit/getLogger) (format "Giving bronze reward to %s." player))

  (let [player (.getPlayer (Bukkit/getServer) player)
        bronze [

                {:item (ItemStack. Material/IRON_PICKAXE 1)
                 :name "Bronze Whacker"
                 :enchant Enchantment/DIG_SPEED
                 :level 1}
                
                {:item (ItemStack. Material/IRON_SWORD 1)
                 :name "Bronze Slapper"
                 :enchant Enchantment/DAMAGE_ALL
                 :level 1}
                
                {:item (ItemStack. Material/IRON_AXE 1)
                 :name "Bronze Axer"
                 :enchant Enchantment/DIG_SPEED
                 :level 1}
                
                {:item (ItemStack. Material/IRON_SPADE 1)
                 :name "Bronze Digger"
                 :enchant Enchantment/DIG_SPEED
                 :level 1}
                
                {:item (ItemStack. Material/IRON_HELMET 1)
                 :name "Bronze Brainbucket"
                 :enchant Enchantment/DURABILITY
                 :level 1}

                {:item (ItemStack. Material/IRON_CHESTPLATE)
                 :name "Bronze Chest"
                 :enchant Enchantment/DURABILITY
                 :level 1}

                {:item (ItemStack. Material/IRON_LEGGINGS)
                 :name "Iron Legs"
                 :enchant Enchantment/DURABILITY
                 :level 1}

                {:item (ItemStack. Material/IRON_BOOTS)
                 :name "Iron Boots"
                 :enchant Enchantment/DURABILITY
                 :level 1}

                {:item (ItemStack. Material/EMERALD 10)}
                
                {:money "1" :money-amount 10.0}
                
                ]]

    (dotimes [x 4]
      (u/add-to-inventory player (make-item player (rand-nth bronze)))))
  
  (.info (Bukkit/getLogger) (format "Done!")))
