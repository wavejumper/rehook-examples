(ns rehook.core
  (:require
   [rehook.state :as rehook]
   [rehook.dom :refer-macros [html]]
   ["react" :as react]
   ["react-dom" :as react-dom]
   [reagent.core :as reagent]
   [benchmark :as Benchmark]
   [rehook.reagent :as r]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Vanilla react (with Hiccup/Sablono)

(defn rehook-component []
  (let [[greeting set-greeting] (rehook/use-state "Hello world")]
    ;(.error js/console "Greetings from rehook")
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
      ;(.error js/console "Greetings from reagent")
      [:div {:on-click #(reset! greeting "I clicked the thing...")}
       @greeting])))

(def suite (Benchmark/Suite.))

#_(doto suite
  (.add "reagent"
        (fn []
          (reagent/render
           [reagent-component]
           (.getElementById js/document "reagent"))))
  (.add "rehook"
        (fn []
          (react-dom/render
           (html [:> rehook-component {}])
           (.getElementById js/document "rehook"))))
  (.on "cycle"
       (fn [event]
         (.log js/console (str (aget event "target")))))
  (.run #js {:async true}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; 'rehook'

(comment
 (defonce db
   (atom {:greeting "Hello world"}))

 (defn use-db-state [path]
   (rehook/use-atom db path))

 (defn greeting []
   (use-db-state [:greeting]))

 (defn rehook-component []
   (let [[greeting set-greeting] (greeting)]
     (html
      (do
        (.error js/console "Greetings from rehook")
        [:div {:on-click #(set-greeting "I clicked the thing...")}
         greeting]))))

 (react-dom/render
  (->el rehook-component)
  (.getElementById js/document "rehook")))

