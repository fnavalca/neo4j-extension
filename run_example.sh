#!/bin/bash

echo "Create jar with extensions"
mvn clean package

echo "Copy jar to a local plugins directory"
rm -rf plugins
mkdir plugins
cp target/neo4j-extension-*.jar plugins

echo "Pull docker image neo4j 3.5.17"
docker pull neo4j:3.5.17

echo "Run docker"
# shellcheck disable=SC2046
containerId=$(docker run --detach \
        --publish=7474:7474 --publish=7687:7687 \
        --volume=$(pwd)/plugins:/var/lib/neo4j/plugins \
        --volume=$(pwd)/scripts:/scripts \
        --env NEO4J_dbms_unmanaged__extension__classes=com.github.fnavalca.neo4j.extension.resource=/examples \
        --env NEO4J_dbms_security_auth__enabled=false neo4j:3.5.17)

echo "Wait until server is up and running"

while ! docker logs "$containerId" | grep "Remote interface available at http://localhost:7474/" > /dev/null;
do
  sleep 1
done

echo "Server started"

echo "Add an example database"
docker exec $containerId /scripts/load_data.sh

echo "Finished"
