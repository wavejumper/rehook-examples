(ns rehook.core
  (:require
   [rehook.state :as rehook]
   [rehook.dom :refer-macros [html]]
   ["react" :as react]
   ["react-dom" :as react-dom]
   [reagent.core :as reagent]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Vanilla react (with Hiccup)

(defn rehook-component []
  (let [[greeting set-greeting] (rehook/use-state "Hello world")]
    (.error js/console "Greetings from rehook")
    (html
     [:div {:on-click #(set-greeting "I clicked the thing...")}
      greeting])))

(react-dom/render
 (html [:> rehook-component {}])
 (.getElementById js/document "rehook"))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Vanilla reagent

(defn reagent-component []
  (let [greeting (reagent/atom "Hello world")]
    (fn []
      (.error js/console "Greetings from reagent")
      [:div {:on-click #(reset! greeting "I clicked the thing...")}
       @greeting])))

(reagent/render
 [reagent-component]
 (.getElementById js/document "reagent"))