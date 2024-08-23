FROM gradle:8.10-jdk21 AS build
COPY ./notification/ /server/notificaition
WORKDIR /server/notificaition
RUN --mount=type=cache,target=/root/.gradle gradle clean bootJar

FROM eclipse-temurin:21-jre AS deploy
COPY --from=build /server/notificaition/build/libs/app.jar /server/notificaition/app.jar
WORKDIR /server/notificaition/
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
