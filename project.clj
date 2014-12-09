(defproject fawkes "0.0.9-clj-SNAPSHOT"
  :description "This is a description"
  :dependencies [[org.clojure/clojure "1.7.0-master-SNAPSHOT"]
                 [org.bukkit/bukkit   "1.8-R0.1-SNAPSHOT"]
                 [org.reflections/reflections "0.9.9-RC1"]
                 [cheshire "5.3.1"]]
  :repl-options {:init :nil :caught clj-stacktrace.repl/pst*}
  :repositories [["me" "http://int.ivy.io/~cmoore/repository/"]]
  :filespecs [{:type :path :path "src/plugin.yml"}]
  :plugins [[lein-auto "0.1.1"]]
  :aot :all)
