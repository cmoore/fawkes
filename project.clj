
(defproject fawkes "0.0.9-clj-SNAPSHOT"
  :description "This is a description"
  
  :dependencies [[org.clojure/clojure "1.7.0-ivy"]
                 
                 [org.bukkit/bukkit "1.8-R0.1-SNAPSHOT"]

                 ; External services for hooks etc.
                 [pl.betoncraft/betonquest "1.4-dev"]
                 [com.sk89q/worldedit "6.0-SNAPSHOT"]
                 [com.sk89q/worldguard "6.0.0-SNAPSHOT"]
                 [com.earth2me/essentials "2.x-SNAPSHOT"]
                 [com.vexsoftware/votifier "1.1"]

                 [clj-stacktrace "0.2.7"]]

  :injections [(let [orig (ns-resolve (doto 'clojure.stacktrace require)
                                      'print-cause-trace)
                     new (ns-resolve (doto 'clj-stacktrace.repl require)
                                     'pst)]
                 (alter-var-root orig (constantly (deref new))))]
  
  :repositories [["me" {:url "http://192.168.0.210/~cmoore/repository/"
                        :checksum :ignore}]
                 ["sonna" "https://oss.sonatype.org/content/repositories/snapshots"]
                 ["citizens" "http://repo.citizensnpcs.co/"]]
  
  :plugins [[lein-auto "0.1.1"]
            [lein-autoreload "0.1.0"]]
  
  :aot [ivy.fawkes.core
        ivy.fawkes.ext.beton.bronze
        ivy.fawkes.ext.beton.resetxp
        ivy.fawkes.ext.beton.testevent])
