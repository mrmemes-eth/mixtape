(ns scaudill.mixtape.changes
  (:require [scaudill.mixtape.manipulate :as manipulate]))

(def command-matrix
  {:add_playlist manipulate/append-playlist
   :add_song_to_playlist manipulate/add-song-to-playlist
   :remove_playlist manipulate/remove-playlist})

(defn command-applicator
  [acc command command-data]
  (reduce (partial (command command-matrix)) acc command-data))

(defn apply-changes
  [data changes]
  (reduce-kv command-applicator data changes))
