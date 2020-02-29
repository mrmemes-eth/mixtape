(ns scaudill.mixtape-test
  (:require [clojure.test :refer :all]
            [scaudill.mixtape :as mixtape]
            [clojure.java.io :as io]
            [clojure.data.json :as json]))

(def json (mixtape/parse "resources/mixtape.json"))

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

(deftest next-val-test
  (is (= 4 (mixtape/next-val :id [{:id 1} {:id 2} {:id 3}])))
  (is (= 200 (mixtape/next-val :ok [{:ok 199}]))))

(deftest add-playlist-test
  (let [playlists (get json :playlists)]
    (testing "before adding playlist"
      (is (= 3 (count playlists)))
      (is (= {:id 3
              :user_id 7
              :song_ids [7 12 13 16 2]}
             (last playlists))))
    (testing "after adding playlist"
      (let [playlist {:user_id 3
                      :song_ids [3 12 15]}
            result (mixtape/append-playlist json playlist)]
        (is (= 4 (count (:playlists result))))
        (is (= {:id 4
                :user_id 3
                :song_ids [3 12 15]}
               (last (:playlists result))))))))

(deftest remove-playlist-test
  (let [playlists (:playlists (mixtape/remove-playlist json 1))]
    (is (= 2 (count playlists)))
    (is (= {:id 2
            :user_id 3
            :song_ids [6 8 11]}
           (first playlists)))
    (is (= [2 3] (map :id playlists)))))

(deftest add-song-to-playlist-test
  (let [data {:playlists [{:id 1
                           :user_id 3
                           :song_ids [6 8 11]}
                          {:id 2
                           :user_id 4
                           :song_ids [6 8 13]}]}
        expected {:playlists [{:id 1
                               :user_id 3
                               :song_ids [6 8 11]}
                              {:id 2
                               :user_id 4
                               :song_ids [6 8 13 10]}]}]
    (is (= expected (mixtape/add-song-to-playlist data 2 10)))))
