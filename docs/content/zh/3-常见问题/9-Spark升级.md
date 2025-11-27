---
title: "Spark升级"
---

## 升级Spark版本

### 问题

> 客户自定义作业使用的spark版本太老，需要定制化spark版本

### 解决方案

1. 登录spark官网，下载对应的spark版本二进制安装包

- https://archive.apache.org/dist/spark/

![20250716161122](https://img.isxcode.com/picgo/20250716161122.png)

![20250716170901](https://img.isxcode.com/picgo/20250716170901.png)

2. 删除本地的spark-min

![20250716161557](https://img.isxcode.com/picgo/20250716161557.png)

3. 修改install.sh脚本中的spark下载链接

![20250716161644](https://img.isxcode.com/picgo/20250716161644.png)

4. 执行install命令，重新下载spark安装包

![20250716161727](https://img.isxcode.com/picgo/20250716161727.png)

5. 修改全局的spark依赖版本

![20250716161913](https://img.isxcode.com/picgo/20250716161913.png)

6. 使用isx package重新打包

![20250716171102](https://img.isxcode.com/picgo/20250716171102.png)