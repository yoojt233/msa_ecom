FROM openjdk:21-ea-oraclelinux8
VOLUME /tmp
COPY build/libs/user-m-service-1.0.jar UserMService.jar
ENTRYPOINT ["java", "-jar", "UserMService.jar"]