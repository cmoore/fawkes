;; -*- Mode: Clojure; eval: (hs-hide-all) -*-

;; TODO: Guards need to be marked to give no xp.

(ns ivy.fawkes.core
  (:import [pl.betoncraft.betonquest BetonQuest])
  
  (:require [ivy.fawkes.events :as events]
            [ivy.fawkes.commands :as commands]
            [ivy.fawkes.blockloader :as blockloader])
  
  (:gen-class :name ivy.fawkes.Main
              :extends org.bukkit.plugin.java.JavaPlugin))

(defn -onEnable [plugin]
  (events/start plugin)
  (commands/start plugin)
  (blockloader/start plugin)

  (.saveDefaultConfig plugin)
  
  (.registerEvents (BetonQuest/getInstance) "ivy.testevent" ivy.fawkes.beton.TestEvent)
  (.registerEvents (BetonQuest/getInstance) "ivy.bronzereward" ivy.fawkes.beton.BronzeReward))

(defn -onDisable [this])
