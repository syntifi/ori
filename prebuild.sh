#!/bin/bash
docker run -d --name ori_postgres_test --ulimit memlock=-1:-1 -it --memory-swappiness=0 --name postgresql_quarkus_test -e POSTGRES_USER=quarkus_test -e POSTGRES_PASSWORD=quarkus_test -e POSTGRES_DB=quarkus_test -p 5432:5432 postgres:13.1
docker run -d --name ori_postgres_dev --ulimit memlock=-1:-1 -it --memory-swappiness=0 --name postgresql_quarkus_dev -e POSTGRES_USER=quarkus_dev -e POSTGRES_PASSWORD=quarkus_dev -e POSTGRES_DB=quarkus_dev -p 5433:5432 postgres:13.1
