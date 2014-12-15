;; -*- Mode: Clojure; eval: (hs-hide-all) -*-

;; TODO: Guards need to be marked to give no xp.

(ns ivy.fawkes.core
  (:import [pl.betoncraft.betonquest BetonQuest])
  
  (:require [ivy.fawkes.events :as events]
            [ivy.fawkes.commands :as commands]
            [ivy.fawkes.block :as block]
            [ivy.fawkes.ext.votifier :as votifier]

            [ivy.fawkes.ext.beton.resetxp]
            [ivy.fawkes.ext.beton.bronze]
            [ivy.fawkes.ext.beton.testevent])
  
  (:gen-class :name ivy.fawkes.Main
              :extends org.bukkit.plugin.java.JavaPlugin))

(defn -onEnable [plugin]
  (events/start plugin)
  (commands/start plugin)
  (block/start plugin)
  (votifier/start plugin)
  
  (.saveDefaultConfig plugin)

  (.registerEvents (BetonQuest/getInstance) "ivy.resetxp" ivy.fawkes.ext.beton.ResetXP)
  (.registerEvents (BetonQuest/getInstance) "ivy.testevent" ivy.fawkes.ext.beton.TestEvent)
  (.registerEvents (BetonQuest/getInstance) "ivy.bronzereward" ivy.fawkes.ext.beton.BronzeReward))

(defn -onDisable [this])
