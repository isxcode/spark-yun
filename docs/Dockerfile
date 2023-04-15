FROM nginx

COPY ./ /usr/share/nginx/html/

EXPOSE 80

# 本地打包
# docker buildx create --name spark-yun-builder
# docker buildx use spark-yun-builder
# docker buildx build --platform linux/amd64,linux/arm64/v8 -t isxcode/zhiqingyun-website:0.0.2 -f ./Dockerfile . --push

# 远程部署
# docker run --restart=always --name zhiqingyun-website -p 30210:80 -d isxcode/zhiqingyun-website:0.0.2
