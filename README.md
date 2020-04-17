# Neo4j unmanaged extensions example
Repository for testing purposes. Create unmanaged extensions for Neo4j.

## Requirements
- Java 8
- Maven 
- Docker

## Usage
I have created a script to generate jar which contains our unmanaged extensions, copy this jar to a separated directory,
pull and run the docker image neo4j:3.5.17, wait until server is up and running and load data (extracted from
[here](https://neo4j.com/developer/example-project/)).

You only have to do is:
```bash
./run_example.sh
```

### Unmanaged extensions included
I have extracted from [Neo4j](https://neo4j.com/docs/java-reference/3.5/extending-neo4j/http-server-extensions/) doc
and they are:
- Hello World: you can access it in http://localhost:7474/examples/helloworld/{NODE_ID}. Currently it is only returning
a hello world message including the {NODE_ID}.
- Streaming JSON responses: you can access it in http://localhost:7474/examples/colleagues/{ACTOR_NAME}. You can query
an actor's colleagues from the data base. For example: Keanu Reeves (http://localhost:7474/examples/colleagues/Keanu%20Reeves)
