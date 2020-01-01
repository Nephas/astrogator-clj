(ns astrogator.render.draw.body)

(defprotocol Drawable "draw the object given camera transformation data"
  (draw-detail [this camera] "high detail view")
  (draw-surface [this camera] "draw the hexmap surface")
  (draw-distant [this camera] "very low detail view (< 5 primitives)")
  (draw-trail [this camera] "draw a dotted trail from the trail array"))
