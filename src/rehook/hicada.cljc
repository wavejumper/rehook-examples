(ns rehook.hicada
  #?(:clj  (:require [hicada.compiler])
     :cljs (:require ["react" :as react])))

#?(:clj  (defn create-element [& args] args))
#?(:cljs (defn create-element [& args] (apply react/createElement args)))

(defmacro html
  [body]
  (hicada.compiler/compile body {:create-element  'rehook.hicada/create-element
                                 :transform-fn    (comp)
                                 :array-children? false}
                           {} &env))