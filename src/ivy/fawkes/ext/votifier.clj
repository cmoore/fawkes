
(ns ivy.fawkes.ext.votifier
  (:require [ivy.fawkes.util :as u]
            [ivy.fawkes.bukkit.event :as event])
  
  (:import [org.bukkit Bukkit]
           [com.vexsoftware.votifier.model VotifierEvent Vote]))

(defn on-votifier-event [event]
  (let [vote (.getVote event)
        username (.getUsername vote)]
    (.broadcastMessage (Bukkit/getServer)
                       (format "%s voted!  They will receive a Bronze Crate Key!" username))))

(defn start [plugin]
  (event/register-event plugin "com.vexsoftware.votifier.model.VotifierEvent" #'on-votifier-event))
