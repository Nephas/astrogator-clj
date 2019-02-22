(ns astrogator.generation.expandable)

(defprotocol Seed "This is an abstract representation of an Object which can be expanded using 'expand'."
  (expand [this]))

(defn expand-if-possible [body]
  (if (satisfies? Seed body) (expand body) body))
