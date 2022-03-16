# Docker images

## Creating the docker image with the application in JVM mode

Before building the container image run:
```shell script
./mvnw package
```

Then, build the image with:
```shell script
docker build -f ori-api/src/main/docker/Dockerfile.jvm -t syntifi.com/ori/ori-api-jvm ori-api
```

Then run the container using:
```shell script
docker run -i --rm -p 8080:8080 syntifi.com/ori/ori-jvm
```

Then run the container using :
```shell script
docker run -i --rm -p 8080:8080 -p 5005:5005 -e JAVA_ENABLE_DEBUG="true" syntifi.com/ori/ori-api-jvm
```
## Creating a docker image in native mode (no JVM dependencies)

It is possible to to build a container that runs the application in native (no JVM) mode

Before building the container image run:

```shell script
./mvnw package -Pnative
```

Then, build the image with:

```shell script
docker build -f ori-api/src/main/docker/Dockerfile.native -t syntifi.com/ori/ori-api-native ori-api
```

Then run the container using:

```shell script
docker run -i --rm -p 8080:8080 syntifi.com/ori/ori-native-native
```


## Docker-compose

The application makes use of Postgresql as persistence layer. 

Alternatively, it is possible to run all necessary containers using docker-compose:

```shell script
docker-compose -f ori-api/src/main/docker/docker-compose-jvm.yml up
```

or for the native build:

```shell script
docker-compose -f ori-api/src/main/docker/docker-compose-native.yml up
```

Please make sure to edit the ```docker-compose-*.yml``` files accordingly
