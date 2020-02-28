(ns scaudill.mixtape
  (:require [clojure.tools.cli :as cli-tools]
            [clojure.string :as str])
  (:gen-class))

(def cli-options
  [["-t" "--mixtape" "Path to your mixtape JSON file."
    :default "mixtape.json"
    :validate [(complement empty?)
               "Please supply a path to a mixtape JSON file or accept the defaults."]]
   ["-c" "--changes" "Path to your changes JSON file."
    :default "changes.json"
    :validate [(complement empty?)
               "Please supply a path to a changes JSON file or accept the defaults."]]
   ["-h" "--help"]])

(defn -main
  "Consume and modify playlist files"
  [& args]
  (let [{:keys [options errrors summary errors]} (cli-tools/parse-opts args cli-options)]
    (cond
      (:help options) (println summary)
      errors (->> (str/join \newline errors)
                  (str "The following errors occurred:\n\n")
                  println))
    (println options)))
