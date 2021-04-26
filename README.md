# challenge-api

Requirements
- JAVA
- CLOJURE
- POSTGRESQL
- DOCKER


## Run Locally

### PostgreSQL
Create and run container
`docker run --net=host --name my-postgres -e POSTGRES_PASSWORD=password -d postgres`

Start
`docker start my-postgres`

Stop
`docker stop my-postgres`

### Clojure Project

- Import as Clojure DEPS project on IDE
- Force dependencies download (if necessary)
  `clojure -e 'nil'`
- Start Local REPL
- Load `dev/user.clj` file and evaluate `go` function (application will start locally)
`halt` to stop

## API

To access API endpoints you have to be authenticated.

You can do it on `localhost:3000/api/login` with a Basic Auth

Database has a user included for test.
Username: `email-jose@email.com`
Password: `1234`

Others endpoints you have to pass a `JWT TOKEN` in `BEARER Token`

### API Doc:
https://documenter.getpostman.com/view/376803/TzJydbG3
