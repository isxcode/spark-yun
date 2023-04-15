# 选择基础镜像
FROM openjdk:8

# 设置挂载点
VOLUME /h2

ARG ADMIN_PASSWORD='admin123'

# 创建文件夹
RUN mkdir /spark-yun

# 将jar包拷贝到容器容器中
COPY ./spark-yun-backend/build/libs/spark-yun-backend.jar /spark-yun/app.jar

# 拷贝代理安装包
COPY ./spark-yun-dist/build/distributions/spark-yun-agent.tar.gz /spark-yun/spark-yun-agent.tar.gz

# 拷贝脚本文件夹
COPY ./spark-yun-dist/src/main/bin /spark-yun/bin

# 暴露8080端口号
EXPOSE 8080

ENV ADMIN_PASSWORD=${ADMIN_PASSWORD}

# 执行命令运行spring项目
CMD java -jar /spark-yun/app.jar --spring.profiles.active=demo --spring.security.user.password=${ADMIN_PASSWORD}

# 构建多平台镜像
# docker buildx create --name spark-yun-builder
# docker buildx use spark-yun-builder
# docker buildx build --platform linux/amd64,linux/arm64/v8 -t isxcode/zhiqingyun:latest -f ./Dockerfile . --push

# 本地脚本
# docker run --restart=always --name zhiqingyun -v /Users/ispong/.h2:/h2  -p 30211:8080 -d isxcode/zhiqingyun:0.0.2

# 远程启动
# docker stop zhiqingyun
# docker rm zhiqingyun
# docker rmi isxcode/zhiqingyun:latest
# docker run --restart=always --name zhiqingyun -p 30211:8080 -v /root/.h2:/h2 -d isxcode/zhiqingyun
