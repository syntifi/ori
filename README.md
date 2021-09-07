# ORI (Onchain Risk Intelligence) 

This project uses Quarkus, the Supersonic Subatomic Java Framework. If you want to learn more about Quarkus, please visit its website: https://quarkus.io/.

## Content
- [Dependencies](#dependencies)
- [Before build instructions](#before-build-instructions)
- [Build instructions](#build-instructions)
- [After build instructions](#after-build-instructions)
- [Features](#features)

## Dependencies
- Java 11 
- Maven 3.8.1 (or above)
- Docker

## Before build instructions 
A couple of steps should be performed before building the application. These steps are described below. The user can **either** follow these steps and run the commands as specified **or** run the shell script below if you are in Linux:
```shell script
./prebuild.sh
```

### Local jar dependencies

**This is just a temporary solution. A casper-java-sdk will be integrated as soon as it is available - [casper-sdk issue](https://github.com/syntifi/ori/issues/2)**

> **_NOTE:_**  Due to the lack of a casper-java-sdk at this point in time, the team implemented just the necessary features (one cannot call it an sdk as many features are missing) in an another project. This is available at [casper-sdk-0.1.2.jar](src/main/resources).
> **Please install the jar file locally by running the following command before running the application in dev mode or packaging the application**
> ```shell script
>  mvn install:install-file \
>      -Dfile="./src/main/resources/casper-sdk-0.1.2.jar" \
>      -DgroupId=com.syntifi.casper \
>      -DartifactId=casper-sdk \
>      -Dversion=0.1.2\
>      -Dpackaging=jar \
>      -DgeneratePom=true
>  ```

### Running unit-test

> **_NOTE:_**  Please make sure to run a docker instance of elastic search and to create a docker network named *elastic*. The unit tests do cover that the interface to the elasticsearch cluster is working properly. This choice was made since we are using the low level elastic search API. All tests will fail if there is no instance of elastisearch running.
> ```shell script
> docker network create elastic
> ```
> ```shell script
> docker run -d --name elasticsearch --net elastic -p 9200:9200 -e "discovery.type=single-node" -e "ES_JAVA_OPTS=-Xms512m -Xmx512m"  elasticsearch:7.13.2
>  ```

## Build instructions
After following the steps in the [Before build instrucions](#before-build-instructions) the user is now ready to proceed with the build and by running the application.

> **_NOTE:_**  After building, the database is empty. Please follow the steps listed on [After build instructions](#after-build-instructions) to populate the database.

### Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

### Packaging and running the application

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

### Creating a native executable

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

### Building and running Docker images

Instructions to build and run docker images are available at [Docker](src/main/docker/README.md).

## After build instructions
At this point the system is build and running. The ES database is empty though. One must crawl the blockchain to properly populate the database.b

### Crawling the chains

The GraphQL API provides resources to crawl the block chain. Find out more about it at [Crawlers](src/main/java/com/syntifi/ori/task/README.md).

## Features

- [REST API](src/main/java/com/syntifi/ori/rest/README.md)
- [GraphQL API](src/main/java/com/syntifi/ori/graphqlb/README.md)
- [Risk metrics](src/main/java/com/syntifi/ori/service/README.md)
