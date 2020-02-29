(ns scaudill.mixtape
  (:require [clojure.string :as str]
            [clojure.tools.cli :as cli-tools]
            [scaudill.mixtape.changes :as changes]
            [scaudill.mixtape.parse :as parse])
  (:gen-class))

(def cli-options
  [["-t" "--mixtape MIXTAPE.JSON" "Path to your mixtape JSON file."
    :default "resources/mixtape.json"
    :validate [(complement empty?)
               "Please supply a path to a mixtape JSON file or accept the defaults."]]
   ["-c" "--changes CHANGES.JSON" "Path to your changes JSON file."
    :default "resources/changes.json"
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
  (let [{:keys [options errrors summary errors]} (cli-tools/parse-opts args cli-options)]
    (cond
      (:help options) (println summary)
      errors (->> (str/join \newline errors)
                  (str "The following errors occurred:\n\n")
                  (exit 1)))
    ;; at this point we assume we have a mixtape and changes files, so let's do the thing:
    (let [data (parse/parse (:mixtape options))
          changes (parse/parse (:changes options))
          output (changes/apply-changes data changes)]
      (spit "output.json" (parse/unparse output)))))
