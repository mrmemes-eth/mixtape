(ns scaudill.mixtape.manipulate-test
  (:require [clojure.test :refer :all]
            [scaudill.mixtape.parse :as parse]
            [scaudill.mixtape.manipulate :as manipulate]))

(def json (parse/parse "resources/mixtape.json"))

(deftest next-val-test
  (is (= 4 (manipulate/next-val :id [{:id 1} {:id 2} {:id 3}])))
  (is (= 200 (manipulate/next-val :ok [{:ok 199}]))))

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
            result (manipulate/append-playlist json playlist)]
        (is (= 4 (count (:playlists result))))
        (is (= {:id 4
                :user_id 3
                :song_ids [3 12 15]}
               (last (:playlists result))))))))

(deftest remove-playlist-test
  (let [playlists (:playlists (manipulate/remove-playlist json 1))]
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
    (is (= expected (manipulate/add-song-to-playlist data 2 10)))))
