#!/bin/bash

./gradlew bootJar
if [ $? -ne 0 ]; then
  echo "> gradle bootJar failed."
  exit 1
fi
docker build -t given53/roomescape:latest .
docker push given53/roomescape:latest
