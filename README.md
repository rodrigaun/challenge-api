# challenge-api

Requirements
- JAVA
- CLOJURE
- POSTGRESQL
- DOCKER


## Run Locally

### PostgreSQL
Start
`docker run --net=host --name my-postgres -e POSTGRES_PASSWORD=password -d postgres`

Stop
`docker stop my-postgres`

### Clojure Project

- Import as Clojure DEPS project on IDE
- Force dependencies download (if necessary)
  `clojure -e 'nil'`
- Start Local REPL
- Load `dev/user.clj` file and evaluate `go` function (application will start locally)
`halt` to stop

