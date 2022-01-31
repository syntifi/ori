#!/bin/bash
docker network create elastic
docker run -d --name ori_elasticsearch  -it --net elastic -p 9200:9200 -e "discovery.type=single-node" -e "ES_JAVA_OPTS=-Xms512m -Xmx512m" elasticsearch:7.13.2
docker run -d --name ori_postgres --ulimit memlock=-1:-1 -it --memory-swappiness=0 --name postgresql_quarkus_test -e POSTGRES_USER=quarkus_test -e POSTGRES_PASSWORD=quarkus_test -e POSTGRES_DB=quarkus_test -p 5432:5432 postgres:13.1
#docker run -d --name ori_postgres_full --ulimit memlock=-1:-1 -it --memory-swappiness=0 --name postgresql_quarkus_test_full -e POSTGRES_USER=quarkus_test -e POSTGRES_PASSWORD=quarkus_test -e POSTGRES_DB=quarkus_test -p 5432:5432 postgres:13.1