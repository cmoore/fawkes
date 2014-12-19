;; -*- Mode: Clojure; eval: (hs-hide-all) -*-

(ns ivy.fawkes.events
  
  (:require [ivy.fawkes.bukkit.event :as event]
            [ivy.fawkes.block :as block]
            [ivy.fawkes.util :as u]
            [monger.core :as mg]
            [monger.collection :as mc])
  
  (:import [org.bukkit.block Chest Biome]
           [org.bukkit.entity Entity EntityType Projectile Player Monster]
           [org.bukkit Material Bukkit ChatColor]
           [org.bukkit.event EventPriority]
           [org.bukkit.event.player PlayerInteractEvent PlayerRespawnEvent]
           [org.bukkit.event.block Action]
           [org.bukkit.event.world ChunkLoadEvent]
           [org.bukkit.event.entity EntityDamageByEntityEvent EntityDamageEvent CreatureSpawnEvent EntityDeathEvent]
           [org.bukkit.metadata FixedMetadataValue]
           [org.bukkit.inventory ItemStack]))

(defonce ^:dynamic fawkes (atom nil))
(defonce ^:dynamic mongo (atom nil))

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
              (block/save-block block "ivy.loot" "regular")
              (.setMetadata block "ivy.loot" (new FixedMetadataValue @fawkes "regular"))
              (.sendMessage player "Loot type set."))
            
            (when (= lore "large")
              (block/save-block block "ivy.loot" "large")
              (.setMetadata block "ivy.loot" (new FixedMetadataValue @fawkes "large"))
              (.sendMessage player "Loot type set."))
            
            (when (= lore "murca")
              (block/save-block block "ivy.loot" "murca")
              (.setMetadata block "ivy.loot" (new FixedMetadataValue @fawkes "murca"))
              (.sendMessage player "Loot type set."))))))))

(defn on-chunk-load [event]
  (let [chunk (.getChunk event)]
    (doseq [tentity (.getTileEntities chunk)]
      (.info (.getLogger @fawkes) "Tile Entity...")
      (when (.equals (.getType tentity) Material/DIAMOND_ORE)
        (.info (.getLogger @fawkes) "Diamond ore."))
      (when (.equals (.getType tentity) Material/REDSTONE_ORE)
        (.info (.getLogger @fawkes) "Redstone ore."))
      (when (.equals (.getType tentity) Material/LAPIS_ORE)
        (.info (.getLogger @fawkes) "Lapis ore located.")
        true))))

;; (defn on-player-respawn [event]
;;   (let [player (.getPlayer event)
;;         name (.getName player)
;;         dead-level (wcar* (car/get (format "fawkes.death.%s.level" name)))]
;;     (when dead-level
;;       (wcar* (car/del (format "fawkes.death.%s.level" name)))
;;       (.info (.getLogger @fawkes) (format "Restoring level to %s from death event." name))
;;       (.setLevel player (u/parse-int dead-level)))))

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
  (let [killed (.getEntity event)]

    
    ;; (when (instance? Player killed)
    ;;   (let [the-level (.getLevel killed)]
    ;;     (.info (.getLogger @fawkes) (format "Player killed - saving level."))
    ;;     (wcar* (car/set (format "fawkes.death.%s.level" (.getName killed)) (- the-level 1)))))

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
            (u/give-exp killer 5)))))))

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
            
            (= biome Biome/FOREST) (spawn-creature (u/rand-range 4 8) entity biome)
            (= biome Biome/JUNGLE) (spawn-creature (u/rand-range 3 5) entity biome)
            (= biome Biome/JUNGLE_HILLS) (spawn-creature (u/rand-range 2 4) entity biome)
            (= biome Biome/PLAINS) (spawn-creature (u/rand-range 8 15) entity biome)
            (= biome Biome/TAIGA) (spawn-creature (u/rand-range 8 15) entity biome)
            (= biome Biome/RIVER) (spawn-creature (u/rand-range 3 7) entity biome)
            
            :else (do
                    (u/info "Request for biome %s fell through." biome)
                    (spawn-creature 1 entity biome))))))))

(defn handle-event [f e]
  (if-let [response (f e)]
    (do
      (if (:msg response)
        (.sendMessage e response)))))

(defn start [plugin]
  (reset! fawkes plugin)
  (reset! mongo (mg/connect))
 
  (u/info "Loading events...")

  (event/register-event plugin "org.bukkit.event.player.PlayerInteractEvent" #'on-player-interact)
  ;(event/register-event plugin "org.bukkit.event.player.PlayerRespawnEvent" #'on-player-respawn)
  (event/register-event plugin "org.bukkit.event.entity.EntityDeathEvent" #'on-entity-death)
  (event/register-event plugin "org.bukkit.event.entity.EntityDamageByEntityEvent" #'on-entity-damage)
  (event/register-event plugin "org.bukkit.event.entity.CreatureSpawnEvent" #'on-entity-spawn))
