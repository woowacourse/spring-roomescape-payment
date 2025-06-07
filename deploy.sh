#!/bin/bash

echo "Starting deployment..."

JAR_FILE=$(ls build/libs/*.jar | head -n 1)

if [ -z "$JAR_FILE" ]; then
  echo "No JAR file found in build/libs"
  exit 1
fi

JAR_NAME=$(basename "$JAR_FILE")

echo "Target JAR: $JAR_NAME"

PID=$(pgrep -f "$JAR_NAME")

if [ -n "$PID" ]; then
  echo "Existing process found with PID: $PID. Killing..."
  kill "$PID"
  sleep 2
fi

echo "Starting new process..."
nohup java -jar "$JAR_FILE" > /dev/null 2>&1 &

echo "Deployment complete."

echo "Waiting for service to be healthy..."
ATTEMPTS=0
until [ $ATTEMPTS -ge 10 ]
do
  STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/health)
  if [ "$STATUS" == "200" ]; then
    echo "Health check passed."
    echo "Deployment complete."
    exit 0
  fi
  ATTEMPTS=$((ATTEMPTS+1))
  sleep 1
done

echo "Health check failed after $ATTEMPTS attempts."
exit 1
