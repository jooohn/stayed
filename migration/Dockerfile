FROM golang:latest
WORKDIR /migration
RUN go get -u github.com/pressly/goose/cmd/goose
COPY ./sql .
