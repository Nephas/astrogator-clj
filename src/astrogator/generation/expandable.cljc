(ns astrogator.generation.expandable)

(defprotocol Seed "This is an abstract representation of an Object which can be expanded using 'expand'."
  (expand [this] "generate all substructures of this object")
  (same? [this other] "compare whether two expandables represent the same object"))

(defn expand-if-possible [body]
  (if (satisfies? Seed body) (expand body) body))

(defn equal-by-seed [obj other-obj]
  (= (:seed obj) (:seed other-obj)))