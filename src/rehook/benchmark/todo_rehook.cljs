(ns rehook.benchmark.todo-rehook
  (:require [rehook.core :as rehook]
            [rehook.dom :refer-macros [html]]
            ["react-dom" :as react-dom]
            [benchmark :as Benchmark]))

(defonce todos (atom (sorted-map)))

(defonce counter (atom 0))

(defonce todo-filter (atom {:filter :all}))

(defn add-todo [text]
  (let [id (swap! counter inc)]
    (swap! todos assoc id {:id id :title text :done false})))

(defn toggle [id] (swap! todos update-in [id :done] not))
(defn save [id title] (swap! todos assoc-in [id :title] title))
(defn delete [id] (swap! todos dissoc id))

(defn mmap [m f a] (->> m (f a) (into (empty m))))
(defn complete-all [v] (swap! todos mmap map #(assoc-in % [1 :done] v)))
(defn clear-done [] (swap! todos mmap remove #(get-in % [1 :done])))

(defonce init (do
                (add-todo "Rename Cloact to Reagent")
                (add-todo "Add undo demo")
                (add-todo "Make all rendering async")
                (add-todo "Allow any arguments to component functions")
                (complete-all true)))

(defn todo-input [props]
  (let [{:keys [title onSave on-stop id class placeholder] :as props} (js->clj props :keywordize-keys true)
        [val setter] (rehook/use-state (or title ""))
        stop #(do (setter "")
                  (if on-stop (on-stop)))
        save #(let [v (-> val str clojure.string/trim)]
                (if-not (empty? v) (onSave v))
                (stop))]

    (html
     [:input {:type        "text"
              :value       val
              :id          id :class class :placeholder placeholder
              :on-blur     save
              :on-change   #(setter (-> % .-target .-value))
              :on-key-down #(case (.-which %)
                              13 (save)
                              27 (stop)
                              nil)}])))

(defn todo-stats [props]
  (let [{:keys [active done]} (js->clj props :keywordize-keys true)
        [filt set-filt] (rehook/use-atom-path todo-filter [:filter])]
    (html
     [:div
      [:span#todo-count
       [:strong active] " " (case active 1 "item" "items") " left"]
      [:ul#filters
       [:li [:a {:class (if (= :all filt) "selected")
                 :on-click #(set-filt :all)}
             "All"]]
       [:li [:a {:class (if (= :active filt) "selected")
                 :on-click #(set-filt :active)}
             "Active"]]
       [:li [:a {:class (if (= :done filt) "selected")
                 :on-click #(set-filt :done)}
             "Completed"]]]
      (when (pos? done)
        [:button#clear-completed {:on-click clear-done}
         "Clear completed " done])])))

(defn todo-item [props]
  (let [{:keys [id done title]} (js->clj props :keywordize-keys true)
        [editing setter] (rehook/use-state false)]
    (html
      [:li {:class (str (if done "completed ")
                        (if editing "editing"))}
       [:div.view
        [:input.toggle {:type "checkbox" :checked done
                        :on-change #(toggle id)}]
        [:label {:on-double-click #(setter true)} title]
        [:button.destroy {:on-click #(delete id)}]]
       (when editing
         [:> todo-input {:class "edit" :title title
                         :on-save #(save id %)
                         :on-stop #(setter false)}])])))

(defn todo-app []
  (let [[filt _]  (rehook/use-atom-path todo-filter [:filter])
        [todos _] (rehook/use-atom todos)
        items     (vals todos)
        done      (->> items (filter :done) count)
        active    (- (count items) done)]
    (html
     [:div
      [:section#todoapp
       [:header#header
        [:h1 "todos (rehook)"]
        [:> todo-input {:id          "new-todo"
                        :placeholder "What needs to be done?"
                        :on-save     add-todo}]]
       (when (-> items count pos?)
         [:div
          [:section#main
           [:input#toggle-all {:type      "checkbox" :checked (zero? active)
                               :on-change #(complete-all (pos? active))}]
           [:label {:for "toggle-all"} "Mark all as complete"]
           (let [filtered-items (filter (case filt
                                          :active (complement :done)
                                          :done :done
                                          :all identity)
                                        items)]
             [:ul#todo-list
              (mapv (fn [todo]
                      (html [:> todo-item (clj->js (assoc todo :key (:id todo)))]))
                    filtered-items)])]
          [:footer#footer
           [:> todo-stats {:active active :done done}]]])]
      [:footer#info
       [:p "Double-click to edit a todo"]]])))

(def suite (Benchmark/Suite.))

(doto suite
  (.add "rehook"
        (fn []
          (react-dom/render (html [:> todo-app {}]) (js/document.getElementById "app"))))
  (.on "cycle"
       (fn [event]
         (.log js/console (str (aget event "target")))))
  (.run #js {:async true}))


