?> 至轻云平台镜像由Docker Hub官方镜像仓库管理

- [https://hub.docker.com/r/isxcode/zhiqingyun/tags](https://hub.docker.com/r/isxcode/zhiqingyun/tags)

<img src="https://img.isxcode.com/picgo/20230415160350.png" width="600">

##### 拉取镜像

```bash
docker pull isxcode/zhiqingyun
```

##### 启动镜像

```bash
docker run --name zhiqingyun -p 30111:8080 -d isxcode/zhiqingyun
```
