FROM azul/zulu-openjdk:24-latest.env
VOLUME /tmp
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]