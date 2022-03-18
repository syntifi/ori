# Dashboard

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

## Use case

Once the service is up and running one should see the following login screen when accessing [http://localhost:8088](http://localhost:8088): 

![Login](https://github.com/syntifi/docs/blob/main/ori_dashboard_login.png?raw=true)

``` text
login: admin
password: admin
```

After the login, the landing page should already be pre-populated as follows:
![Langing page](https://github.com/syntifi/docs/blob/main/ori_dashboard_landing_page.png?raw=true)

By clicking on *Transaction Monitor* one should see the following dashboard containing the following elements:

  1. A coin flow chart at the middle
  2. A table with the top 10 incoming and outgoing transactions 
  3. The total amount transacted and the total number of transactions
  4. A filter on the left hand side

![Monitor](https://github.com/syntifi/docs/blob/main/ori_dashboard_monitor.png?raw=true)


After applying the filter (as seen below), and clicking on apply filters, the data and charts should update to the new dataset.
> **_Note:_** If no filter is applied, the dashboard shows the information for the last 100 transactions in the database

![Monitor filter](https://github.com/syntifi/docs/blob/main/ori_dashboard_monitor_filter.png?raw=true)

