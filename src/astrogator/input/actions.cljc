(ns astrogator.input.actions
  (:require [astrogator.state.selectors :as s]
            [astrogator.util.string.string :as string]
            [astrogator.generation.expandable :as exp]
            [astrogator.gui.message :as m]
            [astrogator.gui.camera :as cam]
            [astrogator.physics.move.transit :as t]
            [astrogator.physics.trafo :as trafo]
            [astrogator.util.log :as log]
            [astrogator.state.selectors :as sel]
            [astrogator.generation.body.ship :as pl]))

(defn explore [state]
  (log/info "explore playership orbit parent")
  (if (exp/same? (s/get-refbody state) (s/get-player-orbit-body state))
    (let [body (s/get-refbody state)
          poem (string/join (get-in body [:descriptors :poem]) "\n")]
      (m/push-message state poem))
    state))

(defn focus-ship [state]
  (let [orbit-body (s/get-player-orbit-body state)]
    (if (and (some? orbit-body) (not (exp/same? (s/get-refbody state) orbit-body)))
      (do (log/info "focus playership")
          (cam/change-refbody state orbit-body))
      state)))

(defn refuel-ship [state]
  (let [orbit-body (s/get-player-orbit-body state)]
    (if (and (some? orbit-body) (= :gas (:type orbit-body)))
      (do (log/info "refuel playership")
          (-> state
              (m/push-message "Gracefully, the fuel parachute descends into the upper atmosphere.")
              (assoc-in (conj sel/playership-path :dv) pl/max-dv)))
      (m/push-message state "There is no appropriate fuel source nearby."))))

(defn start-transit [state target origin offset dist scope]
  (let [target-body  (s/get-body-by-path target (s/get-expanded-refsystem))]
    (if (or (not (s/planet? target-body)) (= target origin))
      (do (log/info "invalid transit target: " target)
          state)
      (let [transit (t/brachistochrone 0.1 dist origin target offset scope)]
        (do (log/info "ship on transit to: " target)
            (-> state
                (m/push-message (scope m/transit-msg))
                (update-in s/playership-path #(-> %
                                                  (assoc-in [:orbit] nil)
                                                  (assoc-in [:ai-mode] scope)
                                                  (assoc-in [:transit] transit)))))))))

(defn transit [state]
  (let [camera (:camera state)
        ship (s/get-playership state)
        interstellar? (= :sector (:scale camera))]
    (cond (and interstellar? (some? (:targetsystem camera)))
          (let [target-seed (:targetsystem camera)
                origin-seed (:refsystem camera)
                target (s/get-system-by-seed target-seed)
                origin (s/get-system-by-seed origin-seed)
                offset (:mappos ship)
                dist (trafo/dist target origin)]
            (start-transit state target-seed origin-seed offset dist :interstellar))
          (and (not interstellar?) (some? (:targetbody camera)))
          (let [target-path (:targetbody camera)
                origin-path (get-in (s/get-playership state) [:orbit :parent])
                system (s/get-expanded-refsystem state)
                origin (get-in system origin-path)
                target (get-in system target-path)
                offset (trafo/sub (:mappos ship) (:mappos origin))
                dist (trafo/dist origin target)]
            (start-transit state target-path origin-path offset dist :interplanetary))
          true state)))