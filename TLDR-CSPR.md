# TL;DR for CSPR network test run

## 1. Test and build

`./mvnw clean compile`

## 2. build libs, jars and images

`./mvnw package -DskipTests`

## 3. Create a docker network *ori*

`docker network create ori`

## 4. Try to start postgresql and api

`docker-compose -f ./ori-api/src/main/docker/docker-compose-jvm.yml up -d`

> Api may fail if postgresql takes too long to start!

## 5. Wait and check if both went up
`docker-compose -f ori-api/src/main/docker/docker-compose-jvm.yml ps`

Should output:

``` text
         Name                        Command              State           Ports         
----------------------------------------------------------------------------------------
docker_ori-api_1          /deployments/run-java.sh        Up      0.0.0.0:8080->8080/tcp
docker_ori-postgresql_1   docker-entrypoint.sh postgres   Up      0.0.0.0:5432->5432/tcp

```
> **NOTE: if ori-api not up, rerun 4 and 5 and it should get it up and running**

## 6. Start CSPR crawler

`docker-compose -f ./ori-chains/ori-cspr/src/main/docker/docker-compose.yml up -d`

> **_NOTE:_** An alternative to crawling is to load a sample of a crawler, just for testing purposes
> ```shell script
> docker exec -i docker_ori-postgresql_1 psql -U ori_db_user ori_db < ori-chains/ori-cspr/src/test/resources/db-sample/ori_db_sample.sql
> ```

## 7. Check if crawler is up

`docker-compose -f ori-chains/ori-cspr/src/main/docker/docker-compose.yml ps`

Should output:

``` text
      Name              Command        State   Ports
----------------------------------------------------
docker_ori-cspr_1   /cnb/process/web   Up
```


## 8. Follow its log output (it will throw some normal exceptions the first run)

`docker-compose -f ori-chains/ori-cspr/src/main/docker/docker-compose.yml logs -f`

## 9. Access the front-end 
Should be up at [http://localhost:8080](http://localhost:8080). 


It will take a while to populate useful data for testing. 

You should access the postgres database with the credentials set in docker-compose.yml to check for accounts, transactions, blocks, etc.

You can use [pgAdmin](https://www.pgadmin.org/) or [Squirrel SQL](http://squirrel-sql.sourceforge.net/) to access the postgresql database.

## 10. Start the dashboard

`docker-compose -f ori-dashboard/docker/docker-compose.yml up -d`

## 11. Check if dashboard is up

`docker-compose -f ori-dashboard/docker/docker-compose.yml ps`

Should output: 
```text
       Name                   Command               State               Ports         
--------------------------------------------------------------------------------------
docker_dashboard_1   sh ./docker/entrypoint.sh   Up (healthy)   0.0.0.0:8088->8088/tcp
```

## 12. Access the dashboard

Should be up at [http://localhost:8088](http://localhost:8088). 

``` text
login: admin
password: admin
```
