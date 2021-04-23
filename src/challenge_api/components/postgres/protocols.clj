(ns challenge-api.components.postgres.protocols)

(defprotocol PostgresClient
  (connection [_ auto-commit?] "Create new connection")
  (commit [_ connection] "Commit transactions (Not implemented to mock)")
  (rollback [_ connection] "Rollback transactions (Not implemented to mock)")
  (execute [_ connection query-params] "Execute instruction and return the number of affected records")
  (executeQuery [_ connection query-params] "Execute instruction and return the query result"))