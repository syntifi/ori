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
- [Maven](https://maven.apache.org/) (3.8.1 or above)
- [Quarkus](https://quarkus.io/)
- [Docker](https://www.docker.com/)
- [Node.js](https://nodejs.org/)
- [React](https://reactjs.org/)

## Before build instructions 
A couple of steps should be performed before building the application. These steps are described below. The user can **either** follow these steps and run the commands as specified **or** run the shell script below if you are in Linux:
```shell script
./prebuild.sh
```
### Running unit-test

> **_NOTE:_**  Please make sure to run the Postgresql instances
> ```shell script
> docker run -d --name ori_postgres_test --ulimit memlock=-1:-1 -it --memory-swappiness=0 --name postgresql_quarkus_test -e POSTGRES_USER=quarkus_test -e POSTGRES_PASSWORD=quarkus_test -e POSTGRES_DB=quarkus_test -p 5432:5432 postgres:13.1
> ```
> ```shell script
> docker run -d --name ori_postgres_dev --ulimit memlock=-1:-1 -it --memory-swappiness=0 --name postgresql_quarkus_dev -e POSTGRES_USER=quarkus_dev -e POSTGRES_PASSWORD=quarkus_dev -e POSTGRES_DB=quarkus_dev -p 5433:5432 postgres:13.1
>  ```

## Build instructions
After following the steps in the [Before build instrucions](#before-build-instructions) the user is now ready to proceed with the build and by running the application.

> **_NOTE:_**  After building, the database is empty. Please follow the steps listed on [After build instructions](#after-build-instructions) to populate the database.

### Running the application in dev mode

The project is divided into several modules with some interdependency. For this reason one must first package the project before running in dev mode:

```shell script
./mvnw clean package
```

After the packaging and test run you can run the application in dev mode that enables live coding using:
```shell script
./mvnw quarkus:dev -pl ori-api/
```

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

The GraphQL API provides resources to crawl the block chain. Find out more about it at [Crawlers](ori-chains/README.md).

## Features

- [REST API](ori-api/src/main/java/com/syntifi/ori/rest/README.md)
- [Risk metrics](ori-api/src/main/java/com/syntifi/ori/service/README.md)
