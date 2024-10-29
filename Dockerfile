FROM openjdk:8

RUN rm -rf /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone

VOLUME /etc/zhiqingyun/conf
VOLUME /var/lib/zhiqingyun

ARG ADMIN_PASSWORD='admin123'
ARG ACTIVE_ENV='docker'
ARG LOG_LEVEL='info'

COPY ./spark-yun-backend/spark-yun-main/build/libs/zhiqingyun.jar /opt/zhiqingyun/zhiqingyun.jar
COPY ./spark-yun-backend/spark-yun-main/src/main/resources/application-docker.yml /etc/zhiqingyun/conf/
COPY ./resources/jdbc/system /var/lib/zhiqingyun-system

EXPOSE 8080

ENV ADMIN_PASSWORD=${ADMIN_PASSWORD}
ENV ACTIVE_ENV=${ACTIVE_ENV}
ENV LOG_LEVEL=${LOG_LEVEL}
ENV PARAMS=""
ENV JVMOPTIONS=""

ENTRYPOINT ["sh","-c","java $JVMOPTIONS -jar /opt/zhiqingyun/zhiqingyun.jar --logging.level.root=${LOG_LEVEL} --spring.profiles.active=${ACTIVE_ENV} --isx-app.admin-passwd=${ADMIN_PASSWORD} --spring.config.additional-location=/etc/zhiqingyun/conf/ $PARAMS"]