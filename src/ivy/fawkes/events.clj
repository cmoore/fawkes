;; -*- Mode: Clojure; eval: (hs-hide-all) -*-

(ns ivy.fawkes.events
  
  (:require [cljminecraft.events :as events]
            [ivy.fawkes.blockloader :as blocker]
            [monger.core :as mg]
            [monger.collection :as mc])
  
  (:import [org.bukkit Material Bukkit]
           [org.bukkit.entity Entity EntityType Projectile Player Monster]
           [org.bukkit.block Chest]
           [org.bukkit.inventory ItemStack]
           [org.bukkit.metadata FixedMetadataValue]
           [org.bukkit.event EventPriority]
           [org.bukkit.event.player PlayerInteractEvent]
           [org.bukkit.event.block Action]
           [org.bukkit.event.entity EntityDamageByEntityEvent EntityDamageEvent CreatureSpawnEvent]

           [com.sk89q.worldedit Vector]
           [com.sk89q.worldedit.regions Region]
           [com.sk89q.worldguard.bukkit WorldGuardPlugin]
           [com.sk89q.worldguard.protection ApplicableRegionSet]
           [com.sk89q.worldguard.protection.managers RegionManager]
           [com.sk89q.worldguard.protection.regions ProtectedRegion])
  
  (:use [clojure.string :only [join]]))

(defonce ^:dynamic fawkes (atom nil))

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

(defn say-user [event message]
  (.sendMessage (.getPlayer event) message))

(defn on-player-interact [^PlayerInteractEvent event]
  (let [player (.getPlayer event)
        block (.getClickedBlock event)
        in-hand (.getItem event)]
    (when (and in-hand block)
      (when (and (=
                  (.equals (.getType block) Material/CHEST)
                  (.equals (.getType in-hand) Material/STICK)))
        
        (when (.getLore (.getItemMeta in-hand))
          (let [lore (.get (.getLore (.getItemMeta in-hand)) 0)]
            
            (when (= lore "view")
              (let [metadata (.getMetadata (.getState block) "ivy.loot")]
                (if (> (.size metadata) 0)
                  (.sendMessage player (join " " ["Metadata:" (.value (.get metadata 0))]))
                  (.sendMessage player "No metadata!"))))
            
            (when (= lore "regular")
              (blocker/save-block block "ivy.loot" "regular")
              (.setMetadata block "ivy.loot" (new FixedMetadataValue @fawkes "regular"))
              (.sendMessage player "Loot type set."))
            
            (when (= lore "large")
              (blocker/save-block block "ivy.loot" "large")
              (.setMetadata block "ivy.loot" (new FixedMetadataValue @fawkes "large"))
              (.sendMessage player "Loot type set."))
            
            (when (= lore "murca")
              (blocker/save-block block "ivy.loot" "murca")
              (.setMetadata block "ivy.loot" (new FixedMetadataValue @fawkes "murca"))
              (.sendMessage player "Loot type set."))))))))

(defn find-mob-level [^Entity entity]
  (cond (instance? Projectile entity) (when (instance? Player (.getShooter entity))
                                        (find-mob-level (.getShooter entity)))
        (instance? Player entity) (.getLevel entity)
        :else (let [level-value (.getMetaData entity "ivy.level")]
                (cond (.isEmpty level-value) 1
                      :else (.get level-value 0)))))

(defn on-entity-damage [^EntityDamageByEntityEvent event]
  (when (instance? EntityDamageByEntityEvent event)
    (let [attacker (.getDamager event)
          defender (.getEntity event)
          damage (.getDamage event)
          final-damage (.getFinalDamage event)
          attacker-level (find-mob-level attacker)
          defender-level (find-mob-level defender)
          level-difference (- defender-level attacker-level)
          new-damage (+ final-damage (* level-difference 0.5))]
      (.info (.getLogger @fawkes) (format "%s (%s) HIT %s (%s) for %s/%s -> %s"
                                          attacker attacker-level
                                          defender defender-level
                                          damage final-damage
                                          new-damage))
      (.setDamage event new-damage))))

(defn on-entity-spawn [event]
  (when (and (instance? CreatureSpawnEvent event)
             (instance? Monster (.getEntity event)))
    (let [entity (.getEntity event)
          region (region-for-entity entity)
          conn (mg/connect)
          db (mg/get-db conn "fawkes")
          docs (mc/find-maps db "range" { :region region})
          parse-int (fn [s] (Integer. (re-find #"\d+" s)))]
      (cond (first docs) (let [range-doc (first docs)
                               min-level (parse-int (get range-doc :min))
                               max-level (parse-int (get range-doc :max))
                               level (+ (rand-int (- max-level min-level)) min-level)]
                           (.setCustomName entity (format "(%s) %s" level (.getType entity)))
                           (.setCustomNameVisible entity true)
                           (.setMetadata entity "ivy.level" (FixedMetadataValue. @fawkes level))
                           (.info (.getLogger @fawkes) (format "Spawned (%s) %s in %s" level (.getType entity) region)))
            :else (do
                    (.setCustomName entity (format "(1) %s" (.getType entity)))
                    (.setCustomNameVisible entity true)
                    (.setMetadata entity "ivy.level" (FixedMetadataValue. @fawkes "1")))))))

(defn handle-event [f e]
  (if-let [response (f e)]
    (do
      (if (:msg response)
        (.sendMessage e response)))))

(defn register-event [event-name func]
  (let [manager (.getPluginManager (Bukkit/getServer))]
    (.registerEvent manager
                    (resolve (symbol event-name))
                    (proxy [org.bukkit.event.Listener] [])
                    EventPriority/NORMAL
                    (proxy [org.bukkit.plugin.EventExecutor] []
                      (execute [l e] (handle-event func e)))
                    @fawkes)))

(defn start [plugin]
  (reset! fawkes plugin)
  (.info (.getLogger @fawkes) "Loading events.")
  (register-event "org.bukkit.event.entity.EntityDamageByEntityEvent" #'on-entity-damage)
  (register-event "org.bukkit.event.entity.CreatureSpawnEvent" #'on-entity-spawn)
  (register-event "org.bukkit.event.player.PlayerInteractEvent" #'on-player-interact))
