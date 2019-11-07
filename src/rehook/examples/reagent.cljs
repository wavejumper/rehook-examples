(ns rehook.examples.reagent
  (:require [rehook.core :as rehook]
            [rehook.hicada :refer-macros [html]]
            ["react" :as react]
            ["react-dom" :as react-dom]
            [reagent.core :as reagent]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Vanilla react (with Hiccup)

(defn rehook-component []
  (let [[greeting set-greeting] (rehook/use-state "Hello from rehook")]
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
  (let [greeting (reagent/atom "Hello from reagent")]
    (fn []
      (.error js/console "Greetings from reagent")
      [:div {:on-click #(reset! greeting "I clicked the thing...")}
       @greeting])))

(reagent/render
 [reagent-component]
 (.getElementById js/document "reagent"))