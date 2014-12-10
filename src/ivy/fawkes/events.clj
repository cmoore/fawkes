
(ns ivy.fawkes.events
  (:require [cljminecraft.events :as events])
  (:import [org.bukkit Material]
           [org.bukkit.entity Player]
           [org.bukkit.block Chest]
           [org.bukkit.inventory ItemStack]
           [org.bukkit.metadata FixedMetadataValue]
           [org.bukkit.event.player PlayerInteractEvent]
           [org.bukkit.event.block Action])
  
  (:use [clojure.string :only [join]]))

(defonce ^:dynamic fawkes (atom nil))

(defn say-user [event message]
  (.sendMessage (.getPlayer event) message))

(defn on-player-interact [event]
  (.info (.getLogger @fawkes) "Event fired.")
  (let [player (.getPlayer event)
        block (.getClickedBlock event)
        in-hand (.getItem event)]
    (when in-hand
      (when (and (=
                  (.equals (.getType block) Material/CHEST)
                  (.equals (.getType in-hand) Material/STICK)))
        
        (when (.getLore (.getItemMeta in-hand))
          (let [lore (.get (.getLore (.getItemMeta in-hand)) 0)]
            (when (= lore "view")
              (say-user event "View stick!"))
            (when (= lore "regular")
              (say-user event "Regular loot"))
            (when (= lore "large")
              (say-user event "Large loot"))
            (when (= lore "murca")
              (say-user event "Murca loot"))))))))

(defn mark-chest-regular [instance ^Chest chest]
  (.setMetaData chest "ivy.loot" (.new FixedMetadataValue instance "regular")))

(defn mark-chest-large [instance ^Chest chest]
  (.setMetaData chest "ivy.loot" (.new FixedMetadataValue instance "large")))

(defn mark-chest-murca [instance ^Chest chest]
  (.setMetaData chest "ivy.loot" (.new FixedMetadataValue instance "murca")))

(defn events []
  [(events/event "player.player-interact" #'on-player-interact)])

(defn start [plugin]
  (reset! fawkes plugin)
  (.info (.getLogger @fawkes) "Loading events.")
  (events/register-eventlist @fawkes (events)))

(defn make-sticks [^Player player]
  (let [stick (.new ItemStack Material/STICK 1)
        meta (.getItemMeta stick)
        lore (.new java.util.ArrayList)]
    (.add lore "regular")
    (.setLore meta lore)
    (.setDisplayName meta "Stick of Loots.")
    (.setItemMeta stick meta)

    (.. player getInventory addItem stick)))
