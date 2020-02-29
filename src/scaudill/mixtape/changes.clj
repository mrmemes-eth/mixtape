(ns scaudill.mixtape.changes
  (:require [scaudill.mixtape.manipulate :as manipulate]))

(def command-matrix
  {:add_playlist manipulate/append-playlist
   :add_song_to_playlist manipulate/add-song-to-playlist
   :remove_playlist manipulate/remove-playlist})

(defn apply-changes
  [data changes]
  (first ;; this `first` is a bit gross, but maybe not unforgivably so?
   (for [[command command-data] changes
         :let [command-fn (command command-matrix)]]
     (reduce (partial command-fn) data command-data))))
