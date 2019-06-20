(ns astrogator.render.body.body
  )

(defprotocol Drawable "draw the object given camera transformation data"
  (draw-detail [this camera] "high detail view")
  (draw-surface [this camera] "generate all substructures of this object")
  (draw-distant [this camera] "very low detail view (< 5 primitives)"))
