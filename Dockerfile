FROM openjdk:8

VOLUME /spark-yun/resources/
VOLUME /spark-yun/config/

ARG ADMIN_PASSWORD='admin123'

RUN mkdir -p /spark-yun/config
RUN mkdir -p /spark-yun/resources

COPY ./spark-yun-backend/build/libs/spark-yun-backend.jar /spark-yun/app.jar
COPY ./spark-yun-dist/src/main/conf/application-demo.yml /spark-yun/config/application-demo.yml
COPY ./spark-yun-dist/build/distributions/spark-yun-agent.tar.gz /spark-yun/spark-yun-agent.tar.gz
COPY ./spark-yun-dist/src/main/bin /spark-yun/bin

EXPOSE 8080

ENV ADMIN_PASSWORD=${ADMIN_PASSWORD}

CMD java -jar /spark-yun/app.jar --spring.profiles.active=demo --spring.config.additional-location=/spark-yun/config/application-demo.yml --spark-yun.admin-passwd=${ADMIN_PASSWORD}
