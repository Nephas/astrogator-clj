(ns astrogator.input.actions
  (:require [astrogator.state.selectors :as s]
            [astrogator.util.string.string :as string]
            [astrogator.generation.expandable :as exp]
            [astrogator.gui.message :as m]
            [astrogator.gui.camera :as cam]
            [astrogator.physics.move.transit :as t]
            [astrogator.util.log :as log]))

(defn explore [state]
  (if (exp/same? (s/get-refbody state) (s/get-player-orbit-body state))
    (let [body (s/get-refbody state)
          poem (string/join (get-in body [:descriptors :poem]) "\n")]
      (m/push-message state poem))
    state))

(defn focus-ship [state]
  (let [orbit-body (s/get-player-orbit-body state)]
    (if (and (some? orbit-body) (not (exp/same? (s/get-refbody state) orbit-body)))
      (cam/change-refbody state orbit-body)
      state)))



(defn start-transit [state injector target origin]
  (if (= target origin) state
                        (do (log/info "ship on transit to: " target)
                            (update-in state s/playership-path injector target origin))))

(defn transit [state]
  (let [camera (:camera state)
        interstellar? (= :sector (:scale camera))]
    (if interstellar?
      (let [target (:targetsystem camera)
            origin (:refsystem camera)]
        (start-transit state t/start-interstellar target origin))
      (let [target (:targetbody camera)
            origin (get-in (s/get-playership state) [:orbit :parent])]
        (start-transit state t/start-interplanetary target origin)))))