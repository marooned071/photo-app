FROM openjdk:10
VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Dspring.data.mongodb.uri=mongodb://springboot-mongo:27017/springmongo-demo","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]