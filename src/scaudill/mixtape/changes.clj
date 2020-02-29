(ns scaudill.mixtape.changes
  (:require [scaudill.mixtape.manipulate :as manipulate]))

(def command-fns
  "Maps commands from our JSON to functions that will apply the desired changes."
  {:add_playlist manipulate/append-playlist
   :add_song_to_playlist manipulate/add-song-to-playlist
   :remove_playlist manipulate/remove-playlist})

(defn apply-individual-commands
  "Given `data`, it applys the change specified by `change-command` to each set
  of arguments in `payloads`."
  [data change-command payloads]
  (reduce (partial (change-command command-fns)) data payloads))

(defn apply-changes
  "Given the mixtape `data`, and the set of `changes`, return the `data` with
   the `changes` applied."
  [data changes]
  (reduce-kv apply-individual-commands data changes))
