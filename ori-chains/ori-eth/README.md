# ORI-eth
The submodule implements the batch job to crawl the Ethereum chain. 

## Build Instructions 

The application can be packaged using:
```shell script
./mvnw package
```

This will create the following docker image locally:
```
syntifi.com/ori/ori-chains/ori-eth
``` 

## Running the application

Please make sure that ORI-API is running either on a docker, in dev mode or in a production setup.

> **_NOTE:_** The crawler depends on a local ETH node. Please run the following docker container: 
> ```shell script
> docker run --name geth -d -p 127.0.0.1:8545:8545 -p 30303:30303 ethereum/client-go:latest --syncmode "full" --cache=512 --http --http.addr "0.0.0.0"
> ```

```shell script
docker run syntifi.com/ori/ori-chains/ori-eth
```