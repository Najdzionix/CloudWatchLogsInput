#!/bin/bash
echo 'Build plugin'
# remove older version
#rm *.gem
#rm *.gemspec
#rm docker/logstash/plugin/*.gem
./gradlew clean

#build
./gradlew gem

echo 'Copy gem file '
cp -f *.gem docker/logstash/plugin/
ls docker/logstash/plugin

cd docker
echo 'Build docker image'
docker-compose build --force-rm

echo 'Run logstash'
docker-compose up 
