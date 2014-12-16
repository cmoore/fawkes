
(ns ivy.fawkes.block
  (:require [ivy.fawkes.util :as u])
  
  (:import [org.bukkit World Material]
           [org.bukkit.block Block]))

(defonce ^:dynamic mongo (atom nil))
(defonce ^:dynamic fawkes (atom nil))

(defn find-all-blocks [world type]
  (flatten
   (map (fn [chunk]
          (filter (fn [x]
                    (= (.getType x) type))
                  (.getTileEntities chunk)))
        (.getLoadedChunks world))))

(defn find-all-chests [^World world]
  ;; (let [chest (fn [bs]
  ;;               (when (= (.getType bs) Material/CHEST)
  ;;                 bs))]
  ;;(find-all-blocks world chest)
  (find-all-blocks world #(= (.getType %) Material/CHEST)))
  ;
  ;; (find-all-blocks world (fn [b]
  ;;                          (when (= (.getType b) Material/CHEST)
  ;;                            b)))

(defn start [instance]
  (reset! fawkes instance))
