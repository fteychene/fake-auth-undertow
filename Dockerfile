FROM gradle:5.5.1-jdk11 as builder

WORKDIR /home/gradle/src
COPY --chown=gradle:gradle . /home/gradle/src
RUN gradle shadowJar

FROM openjdk:10-jre-slim
EXPOSE 8080
COPY --from=builder /home/gradle/src/build/libs/fake-auth*-all.jar /app/app.jar
CMD java -jar /app/app.jar