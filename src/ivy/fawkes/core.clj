;; TODO: Guards need to be marked to give no xp.


(ns ivy.fawkes.core
  
  (:require [cljminecraft.logging :as log]
            [ivy.fawkes.events :as handlers]
            [ivy.fawkes.loot :as loot])
  
  (:gen-class :name ivy.fawkes.Main
              :extends org.bukkit.plugin.java.JavaPlugin))

(defonce ^:dynamic fawkes (atom nil))

(defn -onEnable [plugin]
  (.info (.getLogger plugin) "*** Starting up...")
  (handlers/start plugin)
  (reset! fawkes plugin))

(defn -onDisable [this]
  (.info (.getLogger @fawkes) "*** Shutting down...")
  (log/info "Shutting down..."))


;; (defn find-mob-level [^Entity entity]
;;   (when (.hasMetaData entity "NPC")
;;     5)

;;   (let [type (.getType entity)]
;;     (when (.equals type EntityType/LIGHTNING)
;;       5)
;;     (when (.equals type EntityType/PLAYER)
;;       (.getLevel entity))

;;     (when (instance? entity Projectile)
;;       (when (instance? (.getShooter entity) Player)
;;         (find-mob-level (.getShooter entity))))

;;     (let [level_values (.getMetadata entity "ivy.level")]
;;       (when (.isEmpty level_values)
;;         1)
;;       (.asInt (.get level_values 0)))))


