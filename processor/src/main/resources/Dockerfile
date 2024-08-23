FROM gradle:8.10-jdk21 AS build
COPY ./processor/ /server/processor
WORKDIR /server/processor
RUN --mount=type=cache,target=/root/.gradle gradle clean bootJar

FROM eclipse-temurin:21-jre AS deploy
COPY --from=build /server/processor/build/libs/app.jar /server/processor/app.jar
WORKDIR /server/processor/
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
