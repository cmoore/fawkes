;; -*- Mode: Clojure; eval: (hs-hide-all) -*-

;; TODO: Guards need to be marked to give no xp.

(ns ivy.fawkes.core
  (:import [pl.betoncraft.betonquest BetonQuest]
           [pl.betoncraft.betonquest.core Condition])

  (:require [ivy.fawkes.events :as events]
            [ivy.fawkes.commands :as commands]
            [ivy.fawkes.block :as block]
            [ivy.fawkes.loot :as loot]
            [ivy.fawkes.ext.votifier :as votifier]

            [ivy.fawkes.bukkit.event :as event]
            [ivy.fawkes.bukkit.command :as cmd]
            
            [ivy.fawkes.ext.beton.resetxp]
            [ivy.fawkes.ext.beton.bronze]
            [ivy.fawkes.ext.beton.died]
            [ivy.fawkes.ext.beton.testevent]))

(defn on-enable [plugin]
  (events/start plugin)
  (commands/start plugin)
  (block/start plugin)
  (votifier/start plugin)
  (loot/start plugin)

  (.saveDefaultConfig plugin)

  (let [instance (BetonQuest/getInstance)]
    (.registerEvents instance "ivy.resetxp" ivy.fawkes.ext.beton.ResetXP)
    (.registerEvents instance "ivy.testevent" ivy.fawkes.ext.beton.TestEvent)
    (.registerEvents instance "ivy.bronzereward" ivy.fawkes.ext.beton.BronzeReward)
    (.registerConditions instance "ivy.died" ivy.fawkes.ext.beton.Died)))

(defn on-disable [plugin])
