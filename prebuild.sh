 mvn install:install-file \
     -Dfile="./src/main/resources/casper-sdk-0.1.2.jar" \
     -DgroupId=com.syntifi.casper \
     -DartifactId=casper-sdk \
     -Dversion=0.1.2\
     -Dpackaging=jar \
     -DgeneratePom=true &
docker network create elastic &
docker run -d --name elasticsearch --net elastic -p 9200:9200 -e "discovery.type=single-node" -e "ES_JAVA_OPTS=-Xms512m -Xmx512m"  elasticsearch:7.13.2
