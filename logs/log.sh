#!/bin/bash

LOG_FILE="app.log"

echo "선택 가능한 필드:"
echo "1. GlobalExceptionHandler"
echo "2. LoggingAspect"

read -p "출력할 로그 범위 선택 (예: 1): " log_num

echo "선택 가능한 필드:"
if [ "$log_num" = 1 ]; then
  echo "1. IP"
  echo "2. Port"
  echo "3. URI"
  read -p "출력할 로그 범위 선택 (예: 1): " value

  grep GlobalExceptionHandler "$LOG_FILE" | sed 's/^.* - //' | cut -d' ' -f"$value"

elif [ "$log_num" = 2 ]; then
  echo "1. IP"
  echo "2. Port"
  echo "3. Method"
  echo "4. URI"
  echo "5. Duration"
  read -p "출력할 로그 범위 선택 (예: 1): " value

  grep LoggingAspect "$LOG_FILE" | sed 's/^.* - //' | cut -d' ' -f"$value"

fi
