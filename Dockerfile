FROM openjdk:8

# 拷贝安装包
COPY ./star-dist/src/main/bin  /star-package/
COPY ./star-dist/target/spark-star-1.2.0-bin.tar.gz /star-package/

# 后端
COPY ./star-backend/target/star-backend-1.2.0.jar /star-backend.jar

VOLUME ["/star-data"]

EXPOSE 8080

RUN echo 'java -jar /star-backend.jar --spring.profiles.active=prod' >> /start.sh

CMD [ "sh","/start.sh" ]
