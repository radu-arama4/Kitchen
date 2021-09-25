FROM openjdk:8
MAINTAINER radu
ADD out/artifacts/Service2_jar/Service2.jar service2.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "service2.jar"]