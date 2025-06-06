#!/bin/bash

EC2_HOST="ubuntu@ec2-3-36-57-197.ap-northeast-2.compute.amazonaws.com"
PEM_KEY_PATH="/Users/spqje/key-jenson.pem"
TARGET_DIR="~/home/ubuntu/app"
JAR_NAME="spring-roomescape-payment-0.0.1-SNAPSHOT.jar"
TARGET_JAR_PATH="$TARGET_DIR/$JAR_NAME"

./gradlew clean build -x test

scp -i "$PEM_KEY_PATH" "build/libs/$JAR_NAME" "$EC2_HOST:$TARGET_JAR_PATH"

ssh -i "$PEM_KEY_PATH" "$EC2_HOST" << EOF
  cd ~/app
  pkill -f "$JAR_NAME" || true
  nohup java -jar "$JAR_NAME"
EOF
