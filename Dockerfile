FROM gradle:8.13.0-alpine AS builder

COPY . .

RUN gradle build

FROM eclipse-temurin:21-jre-alpine

RUN mkdir /opt/app

COPY --from=builder /home/gradle/build/libs/minestom-lobby-all.jar /opt/app/minestom-lobby.jar

WORKDIR /data

CMD ["java", "-jar", "/opt/app/minestom-lobby.jar"]