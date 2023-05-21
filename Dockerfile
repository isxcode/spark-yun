FROM openjdk:8

VOLUME /etc/spark-yun/conf
VOLUME /var/lib/spark-yun

ARG ADMIN_PASSWORD='admin123'
ARG ACTIVE_ENV='demo'

RUN mkdir -p /etc/spark-yun/conf
RUN mkdir -p /opt/spark-yun
RUN mkdir -p /var/lib/spark-yun

COPY ./spark-yun-backend/build/libs/spark-yun-backend.jar /opt/spark-yun/zhiqingyun.jar
COPY ./spark-yun-dist/src/main/conf /etc/spark-yun/conf
COPY ./spark-yun-dist/build/distributions/spark-yun-agent.tar.gz /tmp/spark-yun-agent.tar.gz
COPY ./spark-yun-dist/src/main/bin /tmp/spark-yun-bash

EXPOSE 8080

ENV ADMIN_PASSWORD=${ADMIN_PASSWORD}
ENV ACTIVE_ENV=${ACTIVE_ENV}

CMD java -jar /opt/spark-yun/zhiqingyun.jar --spring.profiles.active=${ACTIVE_ENV} --spark-yun.admin-passwd=${ADMIN_PASSWORD} --spring.config.additional-location=/etc/spark-yun/conf/
