(ns rehook.reagent
  (:refer-clojure :exclude [atom])
  (:require ["react-dom" :as react-dom]
            [rehook.state :as state]))

(defn dom-node [this]
  (react-dom/findDOMNode this))

(defn atom [initial-value]
  (let [[val setter] (state/use-state initial-value)]
    (reify
      cljs.core/ISwap
      (-swap! [o f]
        ()))))