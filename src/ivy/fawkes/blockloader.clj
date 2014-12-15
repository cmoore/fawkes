;; -*- Mode: Clojure; eval: (hs-hide-all) -*-

(ns ivy.fawkes.blockloader
  
  (:require [ivy.fawkes.util :as util]
            
            [monger.core :as mg]
            [monger.collection :as mc])
  
  (:import [org.bukkit World Material]
           [org.bukkit.block Block]))


(defonce ^:dynamic fawkes (atom nil))
(defonce ^:dynamic mongo (atom nil))

(defn confirm-blocks [player ^World world]
  (let [db (mg/get-db @mongo "fawkes")
        coll "metadata"
        docs (mc/find-maps db coll { :world (.getName world)})]
    (.sendMessage player (format "Found %d records." (count docs)))
    (let [real-blocks (doall
                       (filter (fn [record]
                                 (let [block_x (get record :block_x)
                                       block_y (get record :block_y)
                                       block_z (get record :block_z)
                                       metaname (get record :metaname)
                                       metavalue (get record :metavalue)]
                                   (let [block (.getBlockAt world block_x block_y block_z)]
                                     (util/log fawkes (format "Block type is: %s" (.toString (.getType block))))
                                     (.equals (.getType block) Material/CHEST))))
                               docs))]
      (.sendMessage player (format "Found %d real chests." (count real-blocks))))))

(defn drop-block [^Block block]
  (let [location (.getLocation block)
        db (mg/get-db @mongo "fawkes")
        collection "metadata"]
    (mc/remove db collection {:block_x (.getBlockX location)
                              :block_y (.getBlockY location)
                              :block_z (.getBlockZ location)})))

(defn find-block [^Block block]
  (let [location (.getLocation block)
        db (mg/get-db @mongo "fawkes")
        collection "metadata"]
    (mc/find-maps db collection {:block_x (.getBlockX location)
                                 :block_y (.getBlockY location)
                                 :block_z (.getBlockZ location)})))

(defn save-block [^Block block ^String metaname ^String metavalue]
  (let [location (.getLocation block)
        bx (.getBlockX location)
        by (.getBlockY location)
        bz (.getBlockZ location)
        x (.getX location)
        y (.getY location)
        z (.getZ location)
        world (.getName (.getWorld location))
        db (mg/get-db @mongo "fawkes")]
    ; It doesn't matter if it's there.
    ; we almost certainly want to remove it.
    (drop-block block)
    (mc/insert db "metadata" {:block_x bx
                              :block_y by
                              :block_z bz
                              :x x
                              :y y
                              :z z
                              :world world
                              :metaname metaname
                              :metavalue metavalue})))

(defn start [instance]
  (reset! fawkes instance)
  (reset! mongo (mg/connect)))
