
(ns ivy.fawkes.beton.bronze
  (:import [pl.betoncraft.betonquest.core QuestEvent]
           [org.bukkit Bukkit Material]
           [org.bukkit.enchantments Enchantment]
           [org.bukkit.inventory ItemStack]
           [org.bukkit.entity Player]
           [org.bukkit.event Listener])
  
  (:gen-class :name ivy.fawkes.beton.BronzeReward
              :init bronzereward
              :extends pl.betoncraft.betonquest.core.QuestEvent
              :implements [org.bukkit.event.Listener]))

(defn -bronzereward [player instructions]
  (.info (Bukkit/getLogger) (format "Giving bronze reward to %s." player))

  (let [player (.getPlayer (Bukkit/getServer) player)
        bronze [

                {:item (ItemStack. Material/IRON_PICKAXE 1)
                 :name "Bronze Whacker"
                 :enchantment Enchantment/DIG_SPEED
                 :enchantlevel 1}
                
                {:item (ItemStack. Material/IRON_SWORD 1)
                 :name "Bronze Slapper"
                 :enchantment Enchantment/DAMAGE_ALL
                 :enchantlevel 1}
                
                {:item (ItemStack. Material/IRON_AXE 1)
                 :name "Bronze Axer"
                 :enchantment Enchantment/DIG_SPEED
                 :enchantlevel 1}
                
                {:item (ItemStack. Material/IRON_SPADE 1)
                 :name "Bronze Digger"
                 :enchantment Enchantment/DIG_SPEED
                 :enchantlevel 1}
                
                {:item (ItemStack. Material/IRON_HELMET 1)
                 :name "Bronze Brainbucket"
                 :enchantment Enchantment/DURABILITY
                 :enchantlevel 1}

                {:item (ItemStack. Material/IRON_CHESTPLATE)
                 :name "Bronze Chest"
                 :enchantment Enchantment/DURABILITY
                 :enchantlevel 1}

                {:item (ItemStack. Material/IRON_LEGGINGS)
                 :name "Iron Legs"
                 :enchantment Enchantment/DURABILITY
                 :enchantlevel 1}

                {:item (ItemStack. Material/IRON_BOOTS)
                 :name "Iron Boots"
                 :enchantment Enchantment/DURABILITY
                 :enchantlevel 1}

                {:item (ItemStack. Material/EMERALD 10)}
                
                ]]

    (dotimes [x 4]
      (let [{item :item name :name enchant :enchantment elevel :enchantlevel} (rand-nth bronze)]
        (let [meta (.getItemMeta item)]
          
          (when (and enchant elevel)
            (.addEnchant meta enchant elevel false))
          
          (.setLore meta ["Bronze Reward"])
          
          (when name
            (.setDisplayName meta name))
          (.setItemMeta item meta)
          (.addItem (.getInventory player) (doto (make-array ItemStack 1)
                                                       (aset 0 item)))))))
  (.info (Bukkit/getLogger) (format "Done!")))
