
(ns ivy.fawkes.beton.bronze
  (:import [pl.betoncraft.betonquest.core QuestEvent]
           [org.bukkit Bukkit Material]
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
        bronze [{ :item (ItemStack. Material/DIAMOND_PICKAXE 1) :name "Bronze Whacker" }
                { :item (ItemStack. Material/DIAMOND_SWORD 1) :name "Bronze Slapper" }
                { :item (ItemStack. Material/DIAMOND_AXE 1) :name "Bronze Axer"}
                { :item (ItemStack. Material/DIAMOND_SPADE 1) :name "Bronze Digger"}]]
    (doall (map (fn [{item :item name :name}]
                  (let [meta (.getItemMeta item)]
                    (.setLore meta ["Bronze Reward"])
                    (.setDisplayName meta name)
                    (.setItemMeta item meta)
                    (.addItem (.getInventory player) (doto (make-array ItemStack 1)
                                                       (aset 0 item)))))
                bronze)))
  (.info (Bukkit/getLogger) (format "Done!")))
