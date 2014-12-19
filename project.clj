
(defproject fawkes "0.0.9-clj-SNAPSHOT"
  :description "This is a description"
  
  :dependencies [[org.clojure/clojure "1.6.0"]

                 [org.clojure/tools.nrepl "0.2.5"]
                 [com.novemberain/monger "2.0.0"]
                 [org.bukkit/bukkit "1.8-R0.1-SNAPSHOT"]

                 [org.clojure/tools.nrepl "0.2.7-SNAPSHOT"]
                 [cider/cider-nrepl "0.8.1"]
                 
                 ; External services for hooks etc.
                 [pl.betoncraft/betonquest "1.4-dev"]
                 [com.sk89q/worldedit "6.0-SNAPSHOT"]
                 [com.sk89q/worldguard "6.0.0-SNAPSHOT"]
                 [com.earth2me/essentials "2.x-SNAPSHOT"]
                 [com.vexsoftware/votifier "1.1"]
                 [cljminecraft/cljminecraft "1.8-R0.1-SNAPSHOT"]]

  :repl-options {:nrepl-middleware
                 [cider.nrepl.middleware.apropos/wrap-apropos
                  cider.nrepl.middleware.classpath/wrap-classpath
                  cider.nrepl.middleware.complete/wrap-complete
                  cider.nrepl.middleware.info/wrap-info
                  cider.nrepl.middleware.inspect/wrap-inspect
                  cider.nrepl.middleware.macroexpand/wrap-macroexpand
                  cider.nrepl.middleware.ns/wrap-ns
                  cider.nrepl.middleware.resource/wrap-resource
                  cider.nrepl.middleware.stacktrace/wrap-stacktrace
                  cider.nrepl.middleware.test/wrap-test
                  cider.nrepl.middleware.trace/wrap-trace
                  cider.nrepl.middleware.undef/wrap-undef]}
  
  :repositories [["me" {:url "http://192.168.0.210/~cmoore/repository/"
                        :checksum :ignore}]
                 ["sonna" "https://oss.sonatype.org/content/repositories/snapshots"]
                 ["citizens" "http://repo.citizensnpcs.co/"]]

  :java-source-paths ["java"]
  
  :plugins [[lein-auto "0.1.1"]
            [lein-autoreload "0.1.0"]]
  
  :aot [ivy.fawkes.ext.beton.died
        ivy.fawkes.ext.beton.bronze
        ivy.fawkes.ext.beton.resetxp
        ivy.fawkes.ext.beton.testevent])
