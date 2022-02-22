#!/bin/bash
echo "============================> Creating Admin"
superset fab create-admin \
               --username ${SUPERSET_ADMIN_USER_NAME}\
               --firstname ${SUPERSET_ADMIN_FIRST_NAME}\
               --lastname ${SUPERSET_ADMIN_LAST_NAME}\
               --email ${SUPERSET_ADMIN_EMAIL}\
               --password ${SUPERSET_ADMIN_PASSWORD} 

echo "============================> Upgrading DB"
superset db upgrade 
echo "============================> Initializing Application"
superset init
echo "============================> Importing DB"
superset set-database-uri -d ORI -u postgresql+psycopg2://${ORI_POSTGRES_USER}:${ORI_POSTGRES_PASSWORD}@${ORI_POSTGRES_HOST}:${ORI_POSTGRES_PORT}/${ORI_POSTGRES_DB}
echo "============================> Importing Dashboard"
superset import-dashboards -p ./docker/data/dashboard_transaction_v1.json
echo "============================> Runnig server"
/usr/bin/run-server.sh

