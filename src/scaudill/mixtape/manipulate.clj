(ns scaudill.mixtape.manipulate)

(defn next-val
  "Get the next value from a sequential series identified by `key`"
  [k coll]
  (inc (apply max (map k coll))))

(defn append-playlist
  "Takes data and a playlist data structure to append. Adds the playlist with a
   new playlist id."
  [data playlist]
  (let [playlist-id (next-val :id (:playlists data))]
    (update data :playlists conj (assoc playlist :id playlist-id))))

(defn add-song-to-playlist
  "Add the song specified by its id to the playlist specified by its id."
  [data {:keys [playlist_id song_id]}]
  ;; grouping the playlists by id makes updating the data easier to reason about:
  (let [grouped-playlists (group-by :id (:playlists data))
        ;; actually do the update, ensuring that the playlist exists
        updated-playlists (if (get grouped-playlists playlist_id)
                            (update-in grouped-playlists [playlist_id 0 :song_ids] conj song_id)
                            grouped-playlists)
        ;; then "de-group" again:
        playlists (map first (vals updated-playlists))]
    (assoc data :playlists playlists)))

(defn remove-playlist
  "Remove a playlist, identified by its id."
  [data id]
  (let [matching (fn [id playlist] (= (:id playlist) id))]
    (update data :playlists #(remove (partial matching id) %))))

