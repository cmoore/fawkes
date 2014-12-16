
(ns ivy.fawkes.bukkit.event
  (:import [org.bukkit Bukkit]
           [org.bukkit.event EventPriority]))

(defn handle-event [f e]
  (if-let [response (f e)]
    (do
      (if (:msg response)
        (.sendMessage e response)))))

(defn register-event [fawkes event-name func]
  (let [manager (.getPluginManager (Bukkit/getServer))]
    (.registerEvent manager
                    (resolve (symbol event-name))
                    (proxy [org.bukkit.event.Listener] [])
                    EventPriority/NORMAL
                    (proxy [org.bukkit.plugin.EventExecutor] []
                      (execute [l e] (handle-event func e)))
                    fawkes)))
