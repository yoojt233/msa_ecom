FROM openjdk:21-ea-oraclelinux8
VOLUME /tmp
COPY build/libs/config-m-service-1.0.jar ConfigMService.jar
COPY springMsaPrvKey.jks springMsaPrvKey.jks
ENTRYPOINT ["java", "-jar", "ConfigMService.jar"]