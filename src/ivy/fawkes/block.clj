;; -*- Mode: Clojure; eval: (hs-hide-all) -*-

(ns ivy.fawkes.block
  (:refer-clojure :exclude [update])
  
  (:require [ivy.fawkes.util :as util]
            [monger.core :as mg]
            [monger.collection :as mc]
            [cljminecraft.logging :as log])
  
  (:import [org.bukkit World Material]
           [org.bukkit.block Block]))

(defonce ^:dynamic fawkes (atom nil))
(defonce ^:dynamic mongo (atom nil))


(defn block-for-record [world record]
  (let [block_x (get record :block_x)
        block_y (get record :block_y)
        block_z (get record :block_z)
        metaname (get record :metaname)
        metavalue (get record :metavalue)]
    (.getBlockAt world block_x block_y block_z)))

(defn chest-records [world]
  (let [db (mg/get-db @mongo "fawkes")
        coll "metadata"
        docs (mc/find-maps db coll { :world (.getName world)})]
    docs))

(defn confirm-blocks [player world]
  (let [records (chest-records world)
        blocks (doall (map (fn [rc]
                             (let [b (block-for-record rc)]
                               (when (.equals (.getType b) Material/CHEST)
                                 b)))
                           records))]
    (.sendMessage player (format "Found %d records." (count records)))
    (.sendMessage player (format "Found %d chests." (count blocks)))))

(defn drop-block [^Block block]
  (let [location (.getLocation block)
        db (mg/get-db @mongo "fawkes")
        collection "metadata"]
    (mc/remove db collection {:block_x (.getBlockX location)
                              :block_y (.getBlockY location)
                              :block_z (.getBlockZ location)})))

(defn prune-chests [world]
  (let [records (chest-records world)]
    (doall (map (fn [record]
                  (let [block (block-for-record world record)]
                    (when (not (.equals (.getType block) Material/CHEST))
                      (let [db (mg/get-db @mongo "fawkes")]
                        (mc/remove db "fawkes" record)))))
                records))))

(defn get-chests [world]
  (doall
   (map (fn [record]
          (block-for-record world record))
        (chest-records world))))

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
