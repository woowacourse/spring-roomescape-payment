#!/bin/bash

docker stop db-server && docker rm db-server
docker run -d --name db-server -p $DB_PORT:$DB_PORT --network my-network \
  -e MYSQL_ROOT_PASSWORD=$DB_PASSWORD \
  -e MYSQL_DATABASE=$DB_NAME \
  -e TZ=Asia/Seoul \
  mysql:latest \
  --character-set-server=utf8 --collation-server=utf8_general_ci --lower_case_table_names=1
