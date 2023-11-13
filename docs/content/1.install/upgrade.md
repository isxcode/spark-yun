#### 升级方案

1. 备份数据库

- 如果使用的是h2数据库，则和资源文件一同保存，如果你的资源文件夹为 /root/zhiqingyun/resources

```bash
cp /root/zhiqingyun/resources /root/zhiqingyun/resources_bak_2023-06-14 
```

- 如果使用mysql数据库

> 可以使用mysql的dump进行备份


###### 拉取新的镜像

```bash
docker pull isxcode/zhiqingyun:0.0.6
docker stop <zhiqingyun_containerId>
docker rm <zhiqingyun_containerId>
docker run isxcode/zhiqingyun:0.0.6 --自定义配置
```
