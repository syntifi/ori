# ORI (Onchain Risk Intelligence) 

This project uses Quarkus, the Supersonic Subatomic Java Framework. If you want to learn more about Quarkus, please visit its website: https://quarkus.io/.

## Content
- [Dependencies](#dependencies)
- [Build instructions](#build-instructions)
- [After build instructions](#after-build-instructions)
- [Features](#features)

## Dependencies
- Java 11 
- [Maven](https://maven.apache.org/) (3.8.1 or above)
- [Docker](https://www.docker.com/)
- [Node.js](https://nodejs.org/)

## Build instructions

> **_NOTE:_**  After building, the database is empty. Please follow the steps listed on [After build instructions](#after-build-instructions) to populate the database.

The project is divided into several modules with some interdependency. For this reason one must first package the project before running in dev mode:

```shell script
./mvnw clean package
```

### Running the application in dev mode
> **_NOTE:_** Please run the following docker container to run a Postgresql instance used in dev mode:
> ```shell script
> docker run -d --name ori_postgres_dev --ulimit memlock=-1:-1 -it --memory-swappiness=0 --name postgresql_quarkus_dev -e POSTGRES_USER=quarkus_dev -e POSTGRES_PASSWORD=quarkus_dev -e POSTGRES_DB=quarkus_dev -p 5433:5432 postgres:13.1
>  ```

After the packaging and test run you can run the application in dev mode that enables live coding using:
```shell script
./mvnw quarkus:dev -pl ori-api/

### Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `.jar` files for each module in the respective `target/` directories.

### Building and running Docker images

Instructions to build and run docker images are available at [Docker](ori-api/src/main/docker/README.md).

## After build instructions
At this point the system is build and running. The database is empty however. One must crawl the blockchains to properly populate the database.

### Crawling the chains

The module ori-chains implements the batch jobs to crawl a specific chain and populate the DB. Find out more about it at [Crawlers](ori-chains/README.md).

## Features

- [REST API](ori-api/src/main/java/com/syntifi/ori/rest/README.md)
- [Risk metrics](ori-api/src/main/java/com/syntifi/ori/service/README.md)
