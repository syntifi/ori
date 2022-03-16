# ORI-chains 
This module and its submodules make use of [Spring Batch Job](https://spring.io/projects/spring-batch) framework. The batch job pulls records from a specific chain, process them and insert the processed records into the ORI persistency layer through [ori-api](../ori-api/README.md) and using [ori-client](../ori-chains/README.md).

## High-Level Design
1. _Reader_ the batch will read records from the specific Blockchain (normally using a java-api for the chain or JSON-RPC calls);
2. _Processor_ the batch will process the inputs from the chains and convert them into a format that ORI understands;
3. _Writer_ the batch will then output and write the data into ORI's database using [ori-client](../ori-chains/README.md).

## Packaging

The application can be packaged using:
```shell script
./mvnw package
```

This will create the required docker images with the crawlers ready for execution.

Also, you can override the following property settings with environment variables:

```shell script
ORI_REST_API_SCHEME=http
ORI_REST_API_ADDRESS=localhost
ORI_REST_API_PORT=8080
ORI_CHAIN_SCHEME=http
ORI_CHAIN_ADDRESS=localhost
ORI_CHAIN_PORT=9090
ORI_BATCH_POSTGRES_HOST=ori-postgresql
ORI_BATCH_POSTGRES_PORT=5432
ORI_BATCH_POSTGRES_DB=ori_db
ORI_BATCH_POSTGRES_USER=ori_db_user
ORI_BATCH_POSTGRES_PASSWORD=ori_db_password
```

## Specific crawlers

- [CSPR](./ori-cspr/README.md)
- [ETH](./ori-eth/README.md)

