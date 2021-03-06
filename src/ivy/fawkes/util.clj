;; -*- Mode: Clojure; eval: (hs-hide-all) -*-

(ns ivy.fawkes.util
  (:import [org.bukkit Bukkit World Material]
           [org.bukkit.event EventPriority]
           [org.bukkit ChatColor]
           [org.bukkit.entity EntityType Projectile Entity Player]
           [org.bukkit.inventory ItemStack]))

(defn rand-range [low high]
  (+ (rand-int (- (+ 1 high) low)) low))

(defn parse-int [s]
  (Integer. (re-find #"\d+" s)))

(defn add-to-inventory [player item]
  (.addItem (.getInventory player) (doto (make-array ItemStack 1)
                                     (aset 0 item))))

(defn olog [message]
  (let [fawkes (.getPlugin (Bukkit/getPluginManager) "Fawkes")]
    (.info (.getLogger fawkes) message)))

(defn logsend [message]
  (if-let [sender (Bukkit/getConsoleSender)]
    (.sendMessage sender message)))

(defmacro info [fmt & args]
  `(logsend (format ~(str ChatColor/GREEN (.getName *ns*) ChatColor/RESET ":" ChatColor/BLUE (:line (meta &form)) ChatColor/RESET " - " fmt) ~@args)))

(defn find-mob-level [^Entity entity]
  (when (.hasMetaData entity "NPC")
    5)

  (let [type (.getType entity)]
    (when (.equals type EntityType/LIGHTNING)
      5)
    (when (.equals type EntityType/PLAYER)
      (.getLevel entity))

    (when (instance? entity Projectile)
      (when (instance? (.getShooter entity) Player)
        (find-mob-level (.getShooter entity))))

    (let [level_values (.getMetadata entity "ivy.level")]
      (when (.isEmpty level_values)
        1)
      (.asInt (.get level_values 0)))))

(defn give-exp [player amount]
  (.giveExp player amount)
  (.sendMessage player (format "You receive %s experience!" amount)))

(defn get-random-item []
  (let [values (Material/values)]
    (nth values (rand (count values)))))
