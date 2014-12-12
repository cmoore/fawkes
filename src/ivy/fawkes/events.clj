;; -*- Mode: Clojure; eval: (hs-hide-all) -*-

(ns ivy.fawkes.events
  
  (:require [cljminecraft.events :as events]
            [ivy.fawkes.blockloader :as blocker]
            [monger.core :as mg]
            [monger.collection :as mc]
            [clojure.pprint :as pp])
  
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
(defonce ^:dynamic mongo (atom nil))

(defn parse-int [s]
  (Integer. (re-find #"\d+" s)))

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
  ;(pp/pprint (supers (class entity)))

  (cond (.hasMetadata entity "NPC") 5
        (instance? Projectile entity) (when (instance? Player (.getShooter entity))
                                        (find-mob-level (.getShooter entity)))
        (instance? Monster entity) (let [level (.getMetadata entity "ivy.level")]
                                     (if (.isEmpty level)
                                       1
                                       (.. level (get 0) (asInt))))
        (instance? Player entity) (or (.getLevel entity) 1)
        :else 1))

(defn on-entity-damage [^EntityDamageByEntityEvent event]
  (when (instance? EntityDamageByEntityEvent event)
    (let [attacker (.getDamager event)
          defender (.getEntity event)
          damage (.getDamage event) ; anelson5@me.com
          final-damage (.getFinalDamage event)
          attacker-level (find-mob-level attacker)
          defender-level (find-mob-level defender)
          level-difference (- attacker-level defender-level)
          t-new-damage (+ final-damage (* level-difference 0.5))
          new-damage (cond (< t-new-damage 0) 0
                           :else t-new-damage)]
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
          db (mg/get-db @mongo "fawkes")
          docs (mc/find-maps db "range" { :region region})]
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
                    (.setMetadata entity "ivy.level" (FixedMetadataValue. @fawkes "1"))
                    (.info (.getLogger @fawkes) (format "Spawned (1) %s in global." (.getType entity))))))))

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

(defn start [plugin connection]
  (reset! fawkes plugin)
  (reset! mongo connection)
  
  (.info (.getLogger @fawkes) "Loading events.")
  (register-event "org.bukkit.event.entity.EntityDamageByEntityEvent" #'on-entity-damage)
  (register-event "org.bukkit.event.entity.CreatureSpawnEvent" #'on-entity-spawn)
  (register-event "org.bukkit.event.player.PlayerInteractEvent" #'on-player-interact))
