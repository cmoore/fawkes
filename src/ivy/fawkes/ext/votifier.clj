
(ns ivy.fawkes.ext.votifier
  (:require [ivy.fawkes.util :as u]
            
            [monger.core :as mg]
            [monger.collection :as mc])
  
  (:import [org.bukkit Bukkit]
           [com.vexsoftware.votifier.model VotifierEvent Vote]))

(def ^:dynamic mongo (atom nil))

(defn on-votifier-event [event]
  (let [vote (.getVote event)
        username (.getUsername vote)]
    (.broadcastMessage (Bukkit/getServer)
                       (format "%s voted!  They will receive a Bronze Crate Key!" username))
    (mc/insert (mg/get-db @mongo "fawkes")
               "votes"
               {:username username
                :timestamp (.getTimeStamp vote)
                :service (.getServiceName vote)})))

(defn start [plugin]
  (reset! mongo (mg/connect))
  (u/register-event plugin "com.vexsoftware.votifier.model.VotifierEvent" #'on-votifier-event))
