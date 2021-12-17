 mvn install:install-file \
     -Dfile="./src/main/resources/casper-sdk-0.1.2.jar" \
     -DgroupId=com.syntifi.casper \
     -DartifactId=casper-sdk \
     -Dversion=0.1.2\
     -Dpackaging=jar \
     -DgeneratePom=true &
docker network create elastic &
docker run -d --name elasticsearch --net elastic -p 9200:9200 -e "discovery.type=single-node" -e "ES_JAVA_OPTS=-Xms512m -Xmx512m"  elasticsearch:7.13.2
docker run -d --ulimit memlock=-1:-1 -it --rm=true --memory-swappiness=0 --name postgresql_quarkus_test -e POSTGRES_USER=quarkus_test -e POSTGRES_PASSWORD=quarkus_test -e POSTGRES_DB=quarkus_test -p 5432:5432 postgres:13.1