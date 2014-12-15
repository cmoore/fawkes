
(ns ivy.fawkes.ext.essentials
  (:import [com.earth2me.essentials.api Economy]))

(defn add-money [name amount]
  (Economy/add name amount))
