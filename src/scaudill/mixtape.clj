(ns scaudill.mixtape
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.tools.cli :as cli-tools])
  (:gen-class))

(defn- id-like?
  [n]
  (re-find #"^\d+$" n))

(defn value-fn
  "This attempts to deserialize values into their (likely) source types... in
  the future, we could use the `property-name` arg to special case handling, if
  necessary."
  [_ value]
  (cond
    (string? value) (if (id-like? value)
                      (Integer/parseInt value)
                      value)
    (vector? value) (mapv (partial value-fn nil) value)
    (map? value) (reduce-kv (fn [m k v]
                              (assoc m k (value-fn k v)))
                            {} value)
    :else value))

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

(defn remove-playlist
  [data id]
  (let [matching (fn [id playlist] (= (:id playlist) id))]
    (update data :playlists #(remove (partial matching id) %))))

(defn parse
  [filename]
  (let [file (io/as-relative-path filename)]
    (json/read-str (slurp file) :key-fn keyword :value-fn value-fn)))

(def cli-options
  [["-t" "--mixtape MIXTAPE.JSON" "Path to your mixtape JSON file."
    :default "resources/mixtape.json"
    :validate [(complement empty?)
               "Please supply a path to a mixtape JSON file or accept the defaults."]]
   ["-c" "--changes CHANGES.JSON" "Path to your changes JSON file."
    :default "changes.json"
    :validate [(complement empty?)
               "Please supply a path to a changes JSON file or accept the defaults."]]
   ["-h" "--help"]])

(defn exit
  [status message]
  (println message)
  (System/exit status))

(defn -main
  "Consume and modify playlist files"
  [& args]
  (let [{:keys [options errrors summary errors] :as opts} (cli-tools/parse-opts args cli-options)]
    (cond
      (:help options) (println summary)
      errors (->> (str/join \newline errors)
                  (str "The following errors occurred:\n\n")
                  (exit 1)))))
