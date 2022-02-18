# ORI-cspr
The submodule implements the batch job to crawl the Casper chain. 

## Build Instructions 

The application can be packaged using:
```shell script
./mvnw package
```

This will create the following docker image locally:
```
syntifi.com/ori-chains/ori-cspr
``` 

## Running the application

Please make sure that ORI-API is running either on a docker, in dev mode or in a production setup.

> **_NOTE:_** The crawler depends on a Casper node.  
> Select a peer from [this list](https://cspr.live/tools/peers) and set the appropriate value as an environment variable for docker, if needed.

```shell script
docker run [-e ORI_CHAIN_ADDRESS=XX.XXX.XX.X] syntifi.com/ori-chains/ori-cspr
```