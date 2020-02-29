(ns scaudill.mixtape.parse
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]))

(defn- id-like?
  "Does it look like an id? is it numberish?"
  [n]
  (re-find #"^\d+$" n))

(defn value-fn
  "Deserializes values into their (likely) source types... in the future, we
  could use the `property-name` arg to special case handling, if necessary."
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

(defn parse
  "From JSON file to CLJ data structures."
  [filename]
  (let [file (io/as-relative-path filename)]
    (json/read-str (slurp file) :key-fn keyword :value-fn value-fn)))

(defn unparse
  "From CLJ data structures to JSON file."
  [data]
  (json/write-str data))
