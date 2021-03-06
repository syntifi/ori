# ORI-eth
The submodule implements the batch job to crawl the Ethereum chain. 

## Build Instructions 

The application can be packaged using:
```shell script
./mvnw package
```

This will create the following docker image locally:
```
syntifi.com/ori/ori-chains/ori-eth:latest
``` 

## Running the application

Please make sure that ORI-API is running either on a docker, in dev mode or in a production setup.

> **_NOTE:_** The crawler depends on a local ETH node. Please run the following docker container: 
> ```shell script
> docker run --name geth -d -p 127.0.0.1:8545:8545 -p 30303:30303 ethereum/client-go:latest --syncmode "full" --cache=512 --http --http.addr "0.0.0.0"
> ```

Then run the container either using:
```shell script
docker run syntifi.com/ori/ori-chains/ori-eth:latest
```

Or using docker-compose:
```shell script
docker-compose -f ori-chains/ori-eth/src/main/docker/docker-compose.yml up -d
```

> **_NOTE:_** The crawler depends on a local ETH node and the docker-compose starts both. That being said, it is often the case that the ETH node docker is up and running but it didn't yet have time to synchronize with other nodes in the network. This might result in a failure to run the crawler. **For now the prefered option is to run each docker independently**.

