(ns scaudill.mixtape.parse-test
  (:require [clojure.test :refer :all]
            [scaudill.mixtape.parse :as parse]))

(def json (parse/parse "resources/mixtape.json"))

(deftest general-parsing-test
  (let [users (:users json)]
    (is (= 7 (count users)))
    (is (= {:id 1 :name "Albin Jaye"}
           (first users))))
  (let [playlists (get json :playlists)
        playlist {:user_id 3
                  :song_ids [3 12 15]}]
    (is (= 3 (count playlists)))
    (is (= {:id 1
            :user_id 2
            :song_ids [8 32]}
           (first playlists))))
  (let [songs (:songs json)
        all-the-stars (first (filter (fn [{:keys [id]}] (= id 15)) songs))]
    (is (= 40 (count songs)))
    (is (= {:id 15
            :artist "Kendrick Lamar"
            :title "All The Stars"}
           all-the-stars))))
