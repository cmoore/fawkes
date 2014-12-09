
(ns io.ivy.fawkes.loot
  
  (:require [io.ivy.bukkit.logging :as log]
            [io.ivy.bukkit.commands :as cmd])
  
  (:import [org.bukkit.block Chest]
           [org.bukkit.command Command CommandExecutor CommandSender]
           [org.bukkit.entity Entity Player]
           [org.bukkit Material World]
           [org.bukkit.inventory Inventory ItemStack]
           [org.bukkit.inventory.meta ItemMeta]))

(defn find-all-chests [^World world]
  (map (fn [chunk]
         (map (fn [blockstate]
                (when (= (.getType blockstate) Material/CHEST)
                  blockstate))
              (.getTileEntities chunk)))
       (.getLoadedChunks world)))
