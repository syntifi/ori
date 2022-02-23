# Dashobaord

## Creating the docker image with the preloaded dashboards 

First build the image with:
```shell script
docker build -f ori-dashboard/Dockerfile -t ori-dashboard .
```

Then run the container using:
```shell script
docker run -i --rm -p 8088:8088  -e SUPERSET_ADMIN_USER_NAME='admin' -e SUPERSET_ADMIN_FIRST_NAME='admin' -e SUPERSET_ADMIN_LAST_NAME='admin' -e SUPERSET_ADMIN_EMAIL='admin@admin.com' -e SUPERSET_ADMIN_PASSWORD='admin' -e ORI_POSTGRES_USER='quarkus_dev' -e ORI_POSTGRES_PASSWORD='quarkus_dev' -e ORI_POSTGRES_HOST='localhost' -e ORI_POSTGRES_PORT='5432'  -e ORI_POSTGRES_DB='quarkus_dev' ori-dashboard
```
