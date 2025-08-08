FROM gradle:8.13.0-alpine AS builder

COPY . .

RUN gradle build

FROM eclipse-temurin:21-jre-alpine

RUN mkdir /opt/app

COPY --from=builder /home/gradle/build/libs/minestom-lobby-all.jar /opt/app/minestom-lobby.jar

RUN mkdir /data && chown 1000:1000 /data

USER 1000:1000
WORKDIR /data

CMD ["java", "-jar", "/opt/app/minestom-lobby.jar"]