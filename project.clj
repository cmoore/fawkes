(defproject fawkes "0.0.9-clj-SNAPSHOT"
  :description "This is a description"
  :dependencies [[clj-stacktrace "0.2.7"]
                 [cljminecraft "1.8-R0.1-SNAPSHOT"]
                 [org.clojure/clojure "1.7.0-master-SNAPSHOT"]
                 [org.bukkit/bukkit   "1.8-R0.1-SNAPSHOT"]
                 [org.reflections/reflections "0.9.9-RC1"]
                 [cheshire "5.3.1"]]
  :injections [(let [orig (ns-resolve (doto 'clojure.stacktrace require)
                                      'print-cause-trace)
                     new (ns-resolve (doto 'clj-stacktrace.repl require)
                                     'pst)]
                                        (alter-var-root orig (constantly (deref new))))]
  :repl-options {:init :nil :caught clj-stacktrace.repl/pst*}
  :repositories [["me" "http://192.168.0.210/~cmoore/repository/"]]
  :filespecs [{:type :path :path "src/plugin.yml"}]
  :plugins [[lein-auto "0.1.1"]]
  :aot :all)
