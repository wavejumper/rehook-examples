(ns rehook.examples.reframe
  (:require [rehook.core :as rehook]
            [rehook.hicada :refer-macros [html]]
            ["react-dom" :as react-dom]))

;; ============================================================================
;; Impl

(defonce db
  (atom {}))

(defonce ^:private subscriptions
  (atom {}))

(defonce ^:private events
  (atom {}))

(defn reg-sub
  [id f]
  (swap! subscriptions assoc id
         (fn [v]
           (first (rehook/use-atom-fn db #(f % v) (constantly nil))))))

(defn subscribe
  [[id & args :as query-vector]]
  (if-let [subscription (get @subscriptions id)]
    (subscription query-vector)
    (throw (js/Error. (str "No subscription found for id " id)))))

(defn reg-event-db
  [id handler]
  (swap! events assoc id handler))

(defn dispatch
  [[id & args :as event]]
  (if-let [handler (get @events id)]
    (swap! db handler event)
    (throw (js/Error. (str "No event handler found for id " id)))))

;; ============================================================================
;; Example

(reg-event-db
 :send-message
 (fn [db [_ & args]]
   (update db :messages conj args)))

(reg-sub
 :messages
 (fn [db _]
   (get db :messages [])))

(defn reframe-component []
  (let [messages (subscribe [:messages])]
    (html
     [:div
      [:div {:on-click #(dispatch [:send-message (str (random-uuid))])} "Add a random UUID"]
      [:pre (.stringify js/JSON (clj->js messages) nil 2)]])))

(react-dom/render
 (html [:> reframe-component {}])
 (.getElementById js/document "rehook"))