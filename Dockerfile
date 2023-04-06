# 选择基础镜像
FROM openjdk:8

# 创建文件夹
RUN mkdir -p /spark-yun/bin

# 将jar包拷贝到容器容器中
COPY ./spark-yun-backend/build/libs/spark-yun-backend.jar /spark-yun/app.jar

# 拷贝代理安装包
COPY ./spark-yun-dist/build/distributions/spark-yun-agent.tar.gz /spark-yun/spark-yun-agent.tar.gz

# 拷贝安装脚本和卸载脚本
COPY ./spark-yun-dist/src/main/bin/ /spark-yun/bin

# 暴露8080端口号
EXPOSE 8080

# 执行命令运行spring项目
CMD java -jar app.jar
