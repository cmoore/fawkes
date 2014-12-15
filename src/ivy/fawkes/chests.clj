
(ns ivy.fawkes.chests
  (:require [ivy.fawkes.util :as util]
            [monger.core :as mg]
            [monger.collection :as mc])
  
  (:import [org.bukkit World Material]
           [org.bukkit.block Block]))


(defonce ^:dynamic mongo (atom nil))
(defonce ^:dynamic fawkes (atom nil))

(defn find-all-chests [^World world]
  (flatten
   (map (fn [chunk]
          (map (fn [blockstate]
                 (when (= (.getType blockstate) Material/CHEST)
                   blockstate))
               (.getTileEntities chunk)))
        (.getLoadedChunks world))))
