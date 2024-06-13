#!/bin/bash

docker pull given53/roomescape:latest
docker stop my-server && docker rm my-server

docker run -d --name my-server -p 8080:8080 --network my-network \
  -e DB_HOST=$DB_HOST \
  -e DB_PORT=$DB_PORT \
  -e DB_NAME=$DB_NAME \
  -e DB_USERNAME=$DB_USERNAME \
  -e DB_PASSWORD=$DB_PASSWORD \
  -e JWT_SECRET_KEY=$JWT_SECRET_KEY \
  -e JWT_EXPIRATION_TIME=$JWT_EXPIRATION_TIME \
  given53/roomescape:latest
