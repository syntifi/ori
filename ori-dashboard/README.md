# Dashobaord

## Creating the docker image with the preloaded dashboards 

First build the image with:
```shell script
docker build -f ori-dashboard/image/Dockerfile -t syntifi.com/ori/ori-dashboard ori-dashboard/image
```

Then run the container either using:
```shell script
docker run -i --rm -p 8088:8088 \
            -e SUPERSET_ADMIN_USER_NAME='admin' \
            -e SUPERSET_ADMIN_FIRST_NAME='admin' \
            -e SUPERSET_ADMIN_LAST_NAME='admin' \
            -e SUPERSET_ADMIN_EMAIL='admin@admin.com' \
            -e SUPERSET_ADMIN_PASSWORD='admin' \
            -e ORI_POSTGRES_USER='ori_db_user' \
            -e ORI_POSTGRES_PASSWORD='ori_db_pwd' \
            -e ORI_POSTGRES_HOST='localhost' \
            -e ORI_POSTGRES_PORT='5432' \
            -e ORI_POSTGRES_DB='ori_db' \
            syntifi.com/ori/ori-dashboard
```

Or using docker-compose:
```shell script
docker-compose -f ori-dashboard/docker/docker-compose.yml up -d
```