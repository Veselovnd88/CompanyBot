#!/bin/bash

echo "Stopping docker containers"
# Ensure, that docker-compose stopped
docker-compose --env-file ./build/.env stop

echo "Deleting previous app build"
# Ensure, that the old application won't be deployed again.
./gradlew clean

#delete config files
echo "Deleting temp files"
rm -f .env.config
