(ns ivy.fawkes.ext.worldguard
  
  (:import [org.bukkit Bukkit]
           [org.bukkit.entity Entity]
           
           [com.sk89q.worldedit Vector]
           [com.sk89q.worldedit.regions Region]
           [com.sk89q.worldguard.bukkit WorldGuardPlugin]
           [com.sk89q.worldguard.protection ApplicableRegionSet]
           [com.sk89q.worldguard.protection.managers RegionManager]
           [com.sk89q.worldguard.protection.regions ProtectedRegion]))

(defn get-worldguard []
  (let [plugin (.getPlugin (.getPluginManager (Bukkit/getServer)) "WorldGuard")]
    (when (and (not (nil? plugin))
               (instance? WorldGuardPlugin plugin))
      plugin)))

(defn region-for-entity [^Entity entity]
  (let [worldguard (get-worldguard)
        rmanager (.getRegionManager worldguard (.getWorld (.getLocation entity)))
        px (.getApplicableRegions rmanager (.getLocation entity))]
    
    (cond (< (.size px) 1) "global"
          :else (.. px iterator next getId))))
