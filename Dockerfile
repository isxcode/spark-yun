FROM openjdk:8

COPY ./spark-yun-backend/build/libs/spark-yun-backend.jar /app.jar

EXPOSE 8080

CMD java -jar app.jar --spring.profiles.active=prod