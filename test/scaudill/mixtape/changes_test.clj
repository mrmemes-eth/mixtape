(ns scaudill.mixtape.changes-test
  (:require [clojure.test :refer :all]
            [scaudill.mixtape.changes :as changes]))

(deftest apply-changes-test
  (testing "adding playlists"
    (let [data {:playlists [{:id 1}]}
          changes {:add_playlist [{:user_id 3 :song_ids [3 12 15]}]}
          expected {:playlists [{:id 1} {:id 2 :user_id 3 :song_ids [3 12 15]}]}]
      (is (= expected (changes/apply-changes data changes)))))
  (testing "removing playlists"
    (let [data {:playlists [{:id 1 :user_id 3 :song_ids [3 12 15]}
                            {:id 2 :user_id 2 :song_ids [2]}
                            {:id 3 :user_id 7 :song_ids [15 15 15 15]}]}
          changes {:remove_playlist [1 3]}
          expected {:playlists [{:id 2 :user_id 2 :song_ids [2]}]}]
      (is (= expected (changes/apply-changes data changes)))))
  (testing "add song to playlist"
    (let [data {:playlists [{:id 1 :user_id 3 :song_ids [3 12 15]}
                            {:id 2 :user_id 2 :song_ids [2]}
                            {:id 3 :user_id 7 :song_ids [15 15 15 15]}]}
          changes {:add_song_to_playlist [{:playlist_id 1 :song_id 15}
                                          {:playlist_id 2 :song_id 15}
                                          {:playlist_id 3 :song_id 15}]}
          expected {:playlists [{:id 1 :user_id 3 :song_ids [3 12 15 15]}
                                {:id 2 :user_id 2 :song_ids [2 15]}
                                {:id 3 :user_id 7 :song_ids [15 15 15 15 15]}]}]
      (is (= expected (changes/apply-changes data changes))))))
