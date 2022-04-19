# ORI (Onchain Risk Intelligence) 

ORI (On-chain Risk Intelligence tool) in an open-source project available under [Apache 2](LICENSE) license. It provides the tools and functionalities to analyse and assess risk on transactions happening on public chains. The project is divided into several submodules that implement the different pieces needed for such an analysis:
  - tt begins with a [crawler engine](ori-chains/README.md), that crawls the blockchains and stores the relevant data in an indexed DB
  - followed by an [API](ori-api/README.md), that provides access to the services to trace the coin the risk metrics of an account
  - as well as a [front-end](ori-frontend/README.md), and a [dashboard](ori-dashboard/README.md) providing graphical capabilities to analyse the data.

In the sections that follow it is possible to find more information about [each module](#modules), build [instructions](build-instructions), and the guidelines to [contribute](CONTRIBUTING.md).

## Content
- [Dependencies](#dependencies)
- [Build instructions](#build-instructions)
- [After build instructions](#after-build-instructions)
- [Features](#features)

## Dependencies
- Java 11 
- [Maven](https://maven.apache.org/) (3.8.1 or above)
- [Docker](https://www.docker.com/)
- [Docker compose](https://docs.docker.com/compose/) (version 1.29.2 or above)
- [Node.js](https://nodejs.org/) (>=14.0.0) [manual-installation](https://github.com/nodesource/distributions#manual-installation)

## Modules
- [Ori API](ori-api/README.md)
- [Ori chains](ori-chains/README.md)
- [Ori client](ori-client/README.md)
- [Ori frontend](ori-frontend/README.md)
- [Ori shared](ori-shared/README.md)
- [Ori risk metric](ori-risk-metric/README.md)
- [Ori dashboard](ori-dashboard/README.md)

## Build instructions

> **_NOTE:_**  After building, the database is empty. Please follow the steps listed on [After build instructions](#after-build-instructions) to populate the database.

The project is divided into several modules with some interdependency. For this reason one must first install before running it in dev mode:

```shell script
./mvnw clean install 
```

### Running the application in dev mode
> **_NOTE:_** Please run the following docker container to run a Postgresql instance used in dev mode:
> ```shell script
> docker run -d --name postgresql_ori_db --ulimit memlock=-1:-1 -it --memory-swappiness=0 -e POSTGRES_USER=ori_db_user -e POSTGRES_PASSWORD=ori_db_pwd -e POSTGRES_DB=ori_db -p 5432:5432 postgres:13.1
>  ```

After the packaging and test run you can run the application in dev mode that enables live coding using:
```shell script
./mvnw quarkus:dev -pl ori-api/
```

### Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `ori-api/target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `ori-api/target/quarkus-app/lib/` directory.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application is now runnable using `java -jar ori-api/target/quarkus-app/quarkus-run.jar`.

### Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./ori-api/target/ori-api-0.2.0-SNAPSHOT-runner`.

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.html.

### Building and running Docker images

Instructions to build and run docker images are available at [Docker](ori-api/src/main/docker/README.md).

## After build instructions
At this point the system is build and running. The database is empty however. One must crawl the blockchains to properly populate the database.

### Crawling the chains

The module ori-chains implements the batch jobs to crawl a specific chain and populate the DB. Find out more about it at [Crawlers](ori-chains/README.md).

## Features

- [REST API](ori-api/README.md)
- [Risk metrics](ori-risk-metric/README.md)
- [Dashboard](ori-dashboard/README.md)
