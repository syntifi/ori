FROM apache/superset
USER superset
WORKDIR /app
COPY --chown=1001 resources ./docker/
RUN cp ./docker/config/superset_config.py ./pythonpath/
RUN cp ./docker/data/logo.png ./superset/static/assets/images/
RUN cp ./docker/data/icon.png ./superset/static/assets/images/

CMD ["sh", "./docker/entrypoint.sh"] 


