;; -*- Mode: Clojure; eval: (hs-hide-all) -*-

;; TODO: Guards need to be marked to give no xp.

(ns ivy.fawkes.core
  
  (:require [ivy.fawkes.events :as events]
            [ivy.fawkes.commands :as commands]
            [ivy.fawkes.blockloader :as blockloader]
            [monger.core :as mg])
  
  (:gen-class :name ivy.fawkes.Main
              :extends org.bukkit.plugin.java.JavaPlugin))

(defonce ^:dynamic fawkes (atom nil))
(defonce ^:dynamic mongo (atom nil))

(defn -onEnable [plugin]
  (let [connection (mg/connect)]
    
    (events/start plugin connection)
    (commands/start plugin connection)
    (blockloader/start plugin connection)
    
    (reset! fawkes plugin)
    (reset! mongo connection)))

(defn -onDisable [this])
