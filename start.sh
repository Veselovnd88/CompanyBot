#!/bin/bash
# Get project version
PROJECT_VERSION=$(grep -Po "version = '\K[^']+" build.gradle)
echo "$PROJECT_VERSION"
# Pull new changes
git pull

# Checkout to needed git branch
git checkout $1
# Give permissions to gradlew
chmod +x gradlew
# Prepare JAR
./gradlew clean
./gradlew build


# Rename java build
mv build/libs/CompanyBot-$PROJECT_VERSION.jar build/libs/app.jar

rc=$?
# if gradle failed, then we will not deploy new version.
if [ $rc -ne 0 ] ; then
  echo Could not perform gradle clean build, exit code [$rc]; exit $rc
fi

# Add env vars to .env config file
echo "$2" >> ./build/.env
echo "$3" >> ./build/.env
# ensure start docker-compose stopped
docker-compose --env-file ./build/.env stop

#Go

docker-compose --env-file ./build/.env up --build -d