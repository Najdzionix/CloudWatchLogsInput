#!/bin/bash
echo 'Build plugin'
# remove older version
rm *.gem 
rm *.gemspec
rm docker/logstash/plugin/*.gem

#build
./gradlew gem

echo 'Copy gem file '
cp *.gem docker/logstash/plugin/
ls docker/logstash/plugin

cd docker
echo 'Build docker image'
docker-compose build

echo 'Run logstash'
docker-compose up 