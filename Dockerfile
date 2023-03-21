# 选择基础镜像
FROM openjdk:8

# 将jar包拷贝到容器容器中
COPY ./spark-yun-backend/build/libs/spark-yun-backend.jar /app.jar

# 暴露8080端口号
EXPOSE 8080

# 执行命令运行spring项目
CMD java -jar app.jar --spring.profiles.active=prod