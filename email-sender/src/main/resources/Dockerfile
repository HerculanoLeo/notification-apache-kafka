FROM gradle:8.10-jdk21 AS build
COPY ./email-sender/ /server/email-sender
WORKDIR /server/email-sender
RUN --mount=type=cache,target=/root/.gradle gradle clean bootJar

FROM eclipse-temurin:21-jre AS deploy
COPY --from=build /server/email-sender/build/libs/app.jar /server/email-sender/app.jar
WORKDIR /server/email-sender/
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
