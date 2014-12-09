;; TODO: Guards need to be marked to give no xp.


(ns io.ivy.fawkes.core

  (:import [org.bukkit Material]
           [org.bukkit.block Chest]
           [org.bukkit.entity Player Projectile Entity EntityType])
  
  (:require [io.ivy.bukkit.logging :as log]
            [io.ivy.bukkit.bukkit :as bukkit]
            [io.ivy.bukkit.events :as events]
            [io.ivy.bukkit.core :as core]
            
            [io.ivy.fawkes.events :as handlers]
            [io.ivy.fawkes.loot :as loot])
  
  (:gen-class :name io.ivy.fawkes.Main
              :extends org.bukkit.plugin.java.JavaPlugin))

(defonce ^:dynamic fawkes (atom nil))

(defn -onEnable [plugin]
  (.info (.getLogger plugin) "*** Starting up...")
  (handlers/start plugin)
  (reset! fawkes plugin))

(defn -onDisable [this]
  (.info (.getLogger @fawkes) "*** Shutting down...")
  (log/info "Shutting down..."))


(defn find-mob-level [^Entity entity]
  (when (.hasMetaData entity "NPC")
    5)

  (let [type (.getType entity)]
    (when (.equals type EntityType/LIGHTNING)
      5)
    (when (.equals type EntityType/PLAYER)
      (.getLevel entity))

    (when (instance? entity Projectile)
      (when (instance? (.getShooter entity) Player)
        (find-mob-level (.getShooter entity))))

    (let [level_values (.getMetadata entity "ivy.level")]
      (when (.isEmpty level_values)
        1)
      (.asInt (.get level_values 0)))))
