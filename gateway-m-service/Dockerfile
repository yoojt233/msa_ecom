FROM openjdk:21-ea-oraclelinux8
VOLUME /tmp
COPY build/libs/apigw-service-1.0.jar GatewayMService.jar
ENTRYPOINT ["java", "-jar", "GatewayMService.jar"]