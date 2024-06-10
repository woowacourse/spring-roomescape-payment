#!/bin/bash

# Change to your project directory
cd ../

# Build the project with Gradle
./gradlew clean build

# Start the server
java -jar build/libs/spring-roomescape-payment-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod


