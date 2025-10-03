FROM openjdk:21-ea-oraclelinux8
VOLUME /tmp
COPY build/libs/order-m-service-1.0.jar OrderMService.jar
ENTRYPOINT ["java", "-jar", "OrderMService.jar"]