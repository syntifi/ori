FROM apache/superset
USER superset
WORKDIR /app
COPY --chown=1001 ori-dashboard/docker/ ./docker/
RUN cp ./docker/config/superset_config.py ./pythonpath/

CMD ["sh", "./docker/entrypoint.sh"] 


