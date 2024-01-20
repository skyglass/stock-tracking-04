#provide service name as a script parameter. For example: product

docker-compose -f docker-app-compose.yml down

mvn clean install -DskipTests

cd ./$1-service/$1-container
mvn spring-boot:build-image -DskipTests \
  -Dspring-boot.build-image.imageName=$1-service

cd ../../

docker-compose -f docker-app-compose.yml up -d