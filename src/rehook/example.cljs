(ns rehook.example
  (:require
   [rehook.state :as rehook]
   [rehook.dom :refer-macros [html]]
   ["react" :as react]
   ["react-dom" :as react-dom]))

(defn effect-component []
  (let [[n _] (rehook/use-state (rand-int 100))]
    (rehook/use-effect
     (fn []
       (prn "Child: this should get called once...")
       (constantly nil))
     [])

    (rehook/use-effect
     (fn []
       (prn "Child: this should get called everytime parent gets updated")
       (constantly nil)))

    (rehook/use-effect
     (fn []
       (prn "Child: I should get called on each re-render cos JS equality sucks")
       (constantly nil))
     [{:foo :bar}])

    (rehook/use-effect
     (fn []
       (prn "Child: I should only be called once when pr-str'd")
       (constantly nil))
     [(pr-str {:foo :bar})])

    (html [:p "...also here is a random number: " n])))

(defn tick []
  (let [[curr-tick set-tick] (rehook/use-state 0)]
    (rehook/use-effect
     (fn []
       (let [ticker (js/setTimeout #(set-tick (inc curr-tick)) 3000)]
         #(js/clearTimeout ticker))))
    (html [:div "Tick... " curr-tick])))

(defn simple-component []
  (let [[first-name set-first-name] (rehook/use-state "Tom")
        [surname set-surname]       (rehook/use-state "Crowley")
        [explosion? detonate]       (rehook/use-state false)]

    (rehook/use-effect
     (fn foo []
       (prn "Parent: I only get called once...")
       (constantly nil))
     [])

    (rehook/use-effect
     (fn []
       (prn "Parent: First name updated")
       (constantly nil))
     [first-name])

    (rehook/use-effect
     (fn []
       (prn "Parent: Surname updated")
       (constantly nil))
     [surname])

    (html
     [:div
      [:h1
       [:span "Hello "]
       (when explosion?
         (let [[error-str _] (rehook/use-state "I should throw an exception if evaluated...")]
           [:div error-str]))
       [:span {:on-click #(set-first-name "Chad")} first-name]
       " "
       [:span {:on-click #(set-surname "Harris")} surname]
       [:span {:on-click #(detonate true)} "... click here to blow up"]
       [:> effect-component {}]]

      [:h2 [:> tick {}]]])))

(react-dom/render
 (html [:> simple-component {}])
 (.getElementById js/document "rehook"))