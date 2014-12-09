(ns io.ivy.bukkit.config
  "Provides a thin wrapper for bukkit config"
  (:require [io.ivy.bukkit.logging :as logging])
  (:require [io.ivy.bukkit.util :as util]))

(defn config-defaults 
  "Loads the bukkit config file for the given plugin and sets defaults, returns a configuration object"
  [plugin]
  (.saveDefaultConfig plugin))

(defn defcn [type]
  `(defn ~(symbol (str "get-" (name type))) [~(symbol "plugin") ~(symbol "path")]
     (try
       (~(symbol (str ".get" (util/camelcase (name type)))) (.getConfig ~(symbol "plugin")) ~(symbol "path"))
       (catch Exception ~(symbol "e") nil))))

(defmacro defcns [& types]
  (let [forms (map defcn types)]
    `(do ~@forms)))

;; This creates the functions (get-... plugin path), eg
;; (get-string plugin path) to get specific config
;; entries with the correct types.

(defcns string int boolean double long list string-list integer-list boolean-list double-list float-list long-list byte-list character-list short-list map-list vector offline-player item-stack configuration-section)
