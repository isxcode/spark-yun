# 选择基础镜像
FROM openjdk:8

# 设置挂载点
VOLUME /h2

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

# 执行命令运行spring项目
CMD java -jar /spark-yun/app.jar --spring.profiles.active=demo

# 构建多平台镜像
# docker buildx create --name spark-yun-builder
# docker buildx use spark-yun-builder
# docker buildx build --platform linux/amd64,linux/arm64/v8 -t isxcode/zhiqingyun:0.0.2 -f ./Dockerfile .
# docker push isxcode/zhiqingyun:0.0.2

# 启动脚本
# docker run --restart=always --name spark-yun -v /Users/ispong/.h2:/h2  -p 30111:8080 -d isxcode/zhiqingyun:0.0.2
