# ori Project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/ori-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.html.

## Building and running Docker images

### Creating the docker image with the application in JVM mode

Before building the container image run:
```shell script
./mvnw package
```

Then, build the image with:
```shell script
docker build -f src/main/docker/Dockerfile.jvm -t ori-jvm .
```

Then run the container using:
```shell script
docker run -i --rm -p 8080:8080 ori-jvm
```

Then run the container using :
```shell script
docker run -i --rm -p 8080:8080 -p 5005:5005 -e JAVA_ENABLE_DEBUG="true" ori-jvm
```
### Creating a docker image in native mode (no JVM dependencies)

It is possible to to build a container that runs the application in native (no JVM) mode

Before building the container image run:

```shell script
./mvnw package -Pnative
```

Then, build the image with:

```shell script
docker build -f src/main/docker/Dockerfile.native -t ori-native .
```

Then run the container using:

```shell script
docker run -i --rm -p 8080:8080 ori-native
```

### Docker-compose

The application makes use of an elastic search cluster. For running this independtly please find more information at https://www.elastic.co/guide/en/elasticsearch/reference/current/docker.html

Alternatively, it is possible to run all necessary images using docker-compose:

```shell script
docker-compose -f src/main/docker/docker-compose.yml up
```
Please make sure to edit the ```docker-compose.yml``` file accordingly

## Provided Code

### RESTEasy JAX-RS

Easily start your RESTful Web Services

[Related guide section...](https://quarkus.io/guides/getting-started#the-jax-rs-resources)
