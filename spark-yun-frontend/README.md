#### 构建指定分支环境

- https://github.com/isxcode/spark-yun/actions/workflows/deploy-dev.yml

#### 远程环境配置

```bash
code .env.dev
```

```bash
VITE_VUE_APP_BASE_DOMAIN=http://localhost:8080
#替换
VITE_VUE_APP_BASE_DOMAIN=http://101.132.73.185:8080
```

#### 启动项目

```bash
pnpm run dev
```

访问 http://localhost:5173

#### 查看swagger

- http://101.132.73.185:8080/swagger-ui/index.html
- username: admin
- password: admin123

#### 查看数据库

- http://101.132.73.185:8080/h2-console
- jdbc: jdbc:h2:/data/zhiqingyun/GH-2092/h2/data;AUTO_SERVER=TRUE
- username: root
- password: root123