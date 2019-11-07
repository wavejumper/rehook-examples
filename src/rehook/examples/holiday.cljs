(ns rehook.examples.holiday
  (:require [rehook.core :as rehook]
            [rehook.hicada :refer-macros [html]]
            ["react" :as react]
            ["react-dom" :as react-dom]
            ["@material-ui/core/styles" :refer [makeStyles]]
            ["@material-ui/core" :refer [Button TextField FormControl]]))

(defonce db
  (atom {:view :main-component}))

(def stylesheet
  {:container {:display  :flex
               :flexWrap :wrap}
   :textField {:width 200}})

(def use-styles
  (let [styles (makeStyles (clj->js stylesheet))]
    (fn []
      (js->clj (styles) :keywordize-keys true))))

(defn form []
  (let [classes (use-styles)]
    (html
     [:form {:className (:container classes)}

      [:> TextField {:id              "date"
                     :label           "Date"
                     :type            "date"
                     :className       (:textField classes)
                     :defaultValue    "2017-05-04"
                     :InputLabelProps {:shrink true}}]



      [:> Button {:varient "contained"
                  :color   "primary"}
       "Submit"]])))

(defn main-component []
  (let [[view set-view] (rehook/use-atom-path db [:view])]
    (html
     (condp = view
       :main-component [:div {:on-click #(set-view :new-date)} "Add new date"
                        [:pre (pr-str @db)]]
       :new-date       [:> form {}]))))

(react-dom/render
 (html [:> main-component {}])
 (.getElementById js/document "rehook"))