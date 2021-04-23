(ns challenge-api.components.http-server.validations
  (:require [medley.core :as medley]
            [tick.alpha.api :as t]))

(defn field-validation [fields-validation [field value] messages]
  [field (when-let [message (->> (field fields-validation)
                                 (medley.core/find-first (fn [[validation-fn _]]
                                                           (not (validation-fn value))))
                                 second)]
           (message messages))])

(defn required-fields-validation [required-fields request-body data-type messages]
  (map (fn [required-field]
         [required-field (when-not (some? (required-field request-body))
                           (:required-field messages))])
       (get required-fields data-type)))

(defn request-validation [fields-validation required-fields request-data data-type messages]
  (let [formating-result (comp (partial medley.core/filter-vals some?)
                               (partial into {}))
        required-result (formating-result (required-fields-validation required-fields request-data data-type messages))
        validation-result (formating-result (map #(field-validation fields-validation % messages) request-data))
        result (merge validation-result required-result)]
    (when (not-empty result)
      (throw (ex-info "Validations" {:type :validation
                                     :validations result})))))

(defn fields-parser [fields-parse request-data]
    (->> request-data
         (map (fn [[field value]]
                [field (if-let [parse-fn (field fields-parse)]
                         (parse-fn value)
                         value)]))
         (into {})))

(defn date? [date]
  (try 
    (-> date
        (.replaceAll "[^0-9^:^-]" "")
        t/date)
    true
    (catch Exception _
      false)))