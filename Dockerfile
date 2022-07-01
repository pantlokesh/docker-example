FROM basharlabadi/openjdk11-11.0.13-alpine:latest
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]