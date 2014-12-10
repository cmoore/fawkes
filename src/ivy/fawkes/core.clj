;; -*- Mode: Clojure; eval: (hs-hide-all) -*-

;; TODO: Guards need to be marked to give no xp.

(ns ivy.fawkes.core
  
  (:require [cljminecraft.logging :as log]
            [ivy.fawkes.events :as handlers]
            [ivy.fawkes.commands :as commands]

            [clojure.tools.nrepl.server :refer (start-server stop-server)]
            [cider.nrepl :refer (cider-nrepl-handler)])
  
  (:gen-class :name ivy.fawkes.Main
              :extends org.bukkit.plugin.java.JavaPlugin))

(defonce ^:dynamic fawkes (atom nil))
(defonce ^:dynamic repl-handle (atom nil))

(defn -onEnable [plugin]
  (.info (.getLogger plugin) "*** Starting up...")
  (handlers/start plugin)
  (commands/start plugin)

  (.info (.getLogger plugin) "Starting nrepl on 127.0.0.1:4545...")
  (reset! repl-handle (start-server :host "127.0.0.1" :port 4545 :handler cider-nrepl-handler))

  (.info (.getLogger plugin) "Binding local instance...")
  (reset! fawkes plugin)

  (.info (.getLogger plugin) "done."))

(defn -onDisable [this]
  (.info (.getLogger @fawkes) "*** Shutting down...")
  
  (.info (.getLogger @fawkes) "Stopping nrepl...")
  (when @repl-handle
    (stop-server @repl-handle))
  (.info (.getLogger @fawkes) "done.")
  
  (log/info "Shutting down..."))
