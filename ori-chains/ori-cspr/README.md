# ORI-cspr
The submodule implements the batch job to crawl the Casper chain. 

## Build Instructions 

The application can be packaged using:
```shell script
./mvnw package
```

This will create the following docker image locally:
```
syntifi.com/ori/ori-chains/ori-cspr:latest
``` 

## Running the application

Please make sure that ORI-API is running either on a docker, in dev mode or in a production setup.

> **_NOTE:_** The crawler depends on a Casper node.  
> Select a peer from [this list](https://cspr.live/tools/peers) and set the appropriate value as an environment variable for docker, if needed.

Then run the container either using:
```shell script
docker run [-e ORI_CHAIN_ADDRESS=XX.XXX.XX.X] syntifi.com/ori/ori-chains/ori-cspr:latest
```

Or using docker-compose:
```shell script
docker-compose -f ori-chains/ori-cspr/src/main/docker/docker-compose.yml up -d
```

## Loading sample data

> **_NOTE:_** It is possible to load a sample 
> ```shell script
> docker exec -i docker_ori-postgresql_1 psql -U ori_db_user ori_db < ori-chains/ori-cspr/src/test/resources/db-sample/ori_db_sample.sql
> ```