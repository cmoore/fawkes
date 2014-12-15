;; -*- Mode: Clojure; eval: (hs-hide-all) -*-

(ns ivy.fawkes.events
  
  (:require [cljminecraft.logging :as log]
            
            [ivy.fawkes.blockloader :as blocker]
            [ivy.fawkes.util :as u])
  
  (:import [org.bukkit.block Chest Biome]
           [org.bukkit.entity Entity EntityType Projectile Player Monster]
           [org.bukkit Material Bukkit ChatColor]
           [org.bukkit.event EventPriority]
           [org.bukkit.event.player PlayerInteractEvent]
           [org.bukkit.event.block Action]
           [org.bukkit.event.entity EntityDamageByEntityEvent EntityDamageEvent CreatureSpawnEvent EntityDeathEvent]
           [org.bukkit.metadata FixedMetadataValue]
           [org.bukkit.inventory ItemStack]))

(defonce ^:dynamic fawkes (atom nil))

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
                  (.sendMessage player (format "Metadata: %s" (.value (.get metadata 0))))
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
  (cond (.hasMetadata entity "NPC") 50
        (instance? Projectile entity) (when (instance? Player (.getShooter entity))
                                        (find-mob-level (.getShooter entity)))
        (instance? Monster entity) (let [level (.getMetadata entity "ivy.level")]
                                     (if (.isEmpty level)
                                       1
                                       (.. level (get 0) (asInt))))
        
        (instance? Player entity) (or (.getLevel entity) 1)
        :else 1))

(defn on-entity-death [^EntityDeathEvent event]
  (let [killed (.getEntity event)
        killer (.getKiller killed)]
    (when (and (instance? Monster killed)
               (instance? Player killer))
      
      (let [killed-level (find-mob-level killed)
            killer-level (.getLevel killer)]
        
        (when (<= killed-level 5)
          (u/give-exp killer 1))
        (when (and (> killed-level 5) (<= killed-level 9))
          (u/give-exp killer 2))
        (when (and (> killed-level 9) (<= killed-level 17))
          (u/give-exp killer 3))
        (when (and (> killed-level 17) (<= killed-level 28))
          (u/give-exp killer 5))))))

(defn on-entity-damage [^EntityDamageByEntityEvent event]
  (when (instance? EntityDamageByEntityEvent event)
    (let [attacker (.getDamager event)
          defender (.getEntity event)
          damage (.getDamage event)
          final-damage (.getFinalDamage event)
          attacker-level (find-mob-level attacker)
          defender-level (find-mob-level defender)
          level-difference (- attacker-level defender-level)
          t-new-damage (+ final-damage (* level-difference 0.5))
          new-damage (cond (<= t-new-damage 0) 1
                           :else t-new-damage)]
      (.info (.getLogger @fawkes) (format "%s (%s) HIT %s (%s) for %s/%s -> %s"
                                          attacker attacker-level
                                          defender defender-level
                                          damage final-damage
                                          new-damage))
      (.setDamage event new-damage))))

(defn rand-range [low high]
  (+ (rand-int (- (+ 1 high) low)) low))

(defn spawn-creature [level entity biome]
  (.setCustomName entity (format "§e(%s) %s" level (.getType entity)))
  (.setCustomNameVisible entity true)
  (.setMetadata entity "ivy.level" (FixedMetadataValue. @fawkes level)))

(defn on-entity-spawn [event]
  (let [entity (.getEntity event)
        location (.getLocation entity)
        world (.getWorld location)
        loc-x (.getBlockX location)
        loc-y (.getBlockY location)
        biome (.getBiome world loc-x loc-y)
        high-y (.getHighestBlockYAt world location)]
    (do
      (when (= (.getType (.getEntity event)) EntityType/RABBIT)
        (spawn-creature 50 (.getEntity event) biome))
      (when (and (instance? CreatureSpawnEvent event)
                 (instance? Monster (.getEntity event)))
        (let [location (.getLocation (.getEntity event))
              world (.getWorld location)
              loc-x (.getBlockX location)
              loc-y (.getBlockY location)
              biome (.getBiome world loc-x loc-y)
              entity (.getEntity event)]
          (cond
            
            (= biome Biome/FOREST) (spawn-creature (rand-range 4 8) entity biome)
            (= biome Biome/JUNGLE) (spawn-creature (rand-range 3 5) entity biome)
            (= biome Biome/JUNGLE_HILLS) (spawn-creature (rand-range 2 4) entity biome)
            (= biome Biome/PLAINS) (spawn-creature (rand-range 8 15) entity biome)
            
            :else (do
                    (log/warn "Request for biome %s%s%s fell through." ChatColor/RED biome ChatColor/RESET)
                    (spawn-creature 1 entity biome))))))))

(defn handle-event [f e]
  (if-let [response (f e)]
    (do
      (if (:msg response)
        (.sendMessage e response)))))

(defn start [plugin]
  (reset! fawkes plugin)
  
  (.info (.getLogger @fawkes) "Loading events.")
 
  (u/register-event plugin "org.bukkit.event.entity.EntityDeathEvent" #'on-entity-death)
  (u/register-event plugin "org.bukkit.event.entity.EntityDamageByEntityEvent" #'on-entity-damage)
  (u/register-event plugin "org.bukkit.event.entity.CreatureSpawnEvent" #'on-entity-spawn)
  (u/register-event plugin "org.bukkit.event.player.PlayerInteractEvent" #'on-player-interact))
