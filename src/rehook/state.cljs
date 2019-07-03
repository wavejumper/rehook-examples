(ns rehook.state
  (:require ["react" :as react]))

(defn use-state
  [initial-value]
  (array-seq (react/useState initial-value)))

(defn use-effect
  ([f]
   (react/useEffect f))
  ([f deps]
   (react/useEffect f (apply array deps))))

(defn use-atom
  [a path]
  (let [[val setter] (use-state (get-in @a path))
        id (str (random-uuid))]

    (use-effect
     (fn []
       (add-watch a id
                  (fn [_ _ prev-state next-state]
                    (when-let [next-state-at-path (get-in next-state path)]
                      (when (not= (get-in prev-state path) next-state-at-path)
                        (setter next-state-at-path)))))

       #(remove-watch a id))

     ;; If you want to run an effect and clean it up only once (on mount and unmount),
     ;; you can pass an empty array ([]) as a second argument
     [])

    [val #(swap! a assoc-in path %)]))