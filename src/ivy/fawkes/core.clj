;; -*- Mode: Clojure; eval: (hs-hide-all) -*-

;; TODO: Guards need to be marked to give no xp.

(ns ivy.fawkes.core
  
  (:require [ivy.fawkes.events :as handlers]
            [ivy.fawkes.commands :as commands])
  
  (:gen-class :name ivy.fawkes.Main
              :extends org.bukkit.plugin.java.JavaPlugin))

(defonce ^:dynamic fawkes (atom nil))

(defn -onEnable [plugin]
  
  (handlers/start plugin)
  (commands/start plugin)

  (reset! fawkes plugin))

(defn -onDisable [this])
