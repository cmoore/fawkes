;; -*- Mode: Clojure; eval: (hs-hide-all) -*-

(ns ivy.fawkes.events
  
  (:require [cljminecraft.events :as events]
            [ivy.fawkes.blockloader :as blocker])
  
  (:import [org.bukkit Material Bukkit]
           [org.bukkit.entity Entity EntityType Projectile Player]
           [org.bukkit.block Chest]
           [org.bukkit.inventory ItemStack]
           [org.bukkit.metadata FixedMetadataValue]
           [org.bukkit.event.player PlayerInteractEvent]
           [org.bukkit.event.block Action]
           [org.bukkit.event.entity EntityDamageByEntityEvent EntityDamageEvent]

           [com.sk89q.worldedit Vector]
           [com.sk89q.worldedit.regions Region]
           [com.sk89q.worldguard.bukkit WorldGuardPlugin]
           [com.sk89q.worldguard.protection.managers RegionManager]
           [com.sk89q.worldguard.protection.regions ProtectedRegion])
  
  (:use [clojure.string :only [join]]))

(defonce ^:dynamic fawkes (atom nil))

(defn get-worldguard []
  (let [plugin (.getPlugin (.getPluginManager (Bukkit/getServer)) "WorldGuard")]
    (when (and (not (nil? plugin))
               (instance? plugin WorldGuardPlugin))
      plugin)))

(defn region-for-entity [^Entity entity]
  (let [pt (.toVector (.getLocation entity))
        worldguard (get-worldguard)
        rmanager (.getRegionManager worldguard (.getWorld (.getLocation entity)))
        px (.getApplicableRegions rmanager pt)]
    
    (cond (< (.size px) 1) nil
          :else (.. px iterator next getId))))

(defn say-user [event message]
  (.sendMessage (.getPlayer event) message))

(defn on-player-interact [event]
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
  (when (.hasMetadata "NPC" entity)
    5)

  (when (= (.getType entity) EntityType/LIGHTNING)
    5)

  (when (= (.getType entity) EntityType/PLAYER)
    (.getLevel entity))

  (when (instance? entity Projectile)
    (when (instance? (.getShooter entity) Player)
      (find-mob-level (.getShooter entity))))

  (let [level-values (.getMetadata entity "ivy.level")]
    (cond (.isEmpty level-values) 0
          :else (.get level-values 0))))

(defn on-entity-damage [event]
  (when (instance? event EntityDamageEvent)
    (.info (.getLogger @fawkes) "Wrong damage event."))
  (when (instance? event EntityDamageByEntityEvent)
    (let [attacker (.getDamager event)
          defender (.getEntity event)
          damage (.getDamage event)
          final-damage (.getFinalDamage event)
          attacker-level (find-mob-level attacker)
          defender-level (find-mob-level defender)
          level-difference (- defender-level attacker-level)
          new-damage (+ final-damage (* level-difference 0.5))]
      (.info @fawkes (format "%s (%d) HIT %s (%d) for %d/%d -> %d"
                             attacker attacker-level
                             defender defender-level
                             damage final-damage
                             new-damage))
      (.setDamage event new-damage))))

(defn events []
  [(events/event "player.player-interact" #'on-player-interact)
   (events/event "entity.entity-damage-by-entity" #'on-entity-damage)])

(defn start [plugin]
  (reset! fawkes plugin)
  (.info (.getLogger @fawkes) "Loading events.")
  (events/register-eventlist @fawkes (events)))
