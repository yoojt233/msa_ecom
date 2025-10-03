FROM openjdk:21-ea-oraclelinux8
VOLUME /tmp
COPY build/libs/catalog-m-service-1.0.jar CatalogMService.jar
ENTRYPOINT ["java", "-jar", "CatalogMService.jar"]
