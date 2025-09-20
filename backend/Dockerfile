FROM openjdk:13-alpine as jdk

#USER spring
#RUN mkdir /home/spring/app
#RUN chown spring /home/spring/app

WORKDIR /home/spring/app

FROM jdk as build
COPY ./gradlew ./
COPY build.gradle.kts ./
COPY ./gradle ./gradle

RUN ./gradlew

COPY ./ ./
ARG BUILD_ARG="bootJar --parallel"
RUN ./gradlew $BUILD_ARG

FROM jdk as app
ARG BOOT_JAR="/home/spring/app/build/libs/*.jar"

RUN mkdir /home/spring/app/pdf

COPY --from=build $BOOT_JAR ./app.jar
ENTRYPOINT ["java","-jar","./app.jar"]