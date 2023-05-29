
##### 发布需求#64和#74

> 只有技术负责人可以操作 <br/>
> 先从最新分支 `0.0.5`上切出`0.0.6`

```bash
git fetch upstream 
git checkout upstream/0.0.5
git checkout -b 0.0.6 
git push upstream 0.0.6
```

##### 创建pr合并

![20230529145836](https://img.isxcode.com/picgo/20230529145836.png)

合并内容： issue标题保持一致  :art: 编写一篇开发手册 #74 v0.0.6
使用squash方式合并代码

![20230529150038](https://img.isxcode.com/picgo/20230529150038.png)

##### 发布版本latest

- 将0.0.6 合并到latest中

```bash
git checkout 0.0.6
git merge upstream/0.0.6
git rebase upstream/latest
git push upstream 0.0.6 --force
```

提交内容：:bookmark: v0.0.6

![20230529151639](https://img.isxcode.com/picgo/20230529151639.png)

推到发布到latest中


##### 版本发布

发布规范

1. https://github.com/isxcode/spark-yun/releases


2. 点击发布版本
   ![20230528200400](https://img.isxcode.com/picgo/20230528200400.png)

3. 选择发布的分支版本
   ![20230528200600](https://img.isxcode.com/picgo/20230528200600.png)

4. 创建标签名称，规则 vx.x.x
   ![20230528200649](https://img.isxcode.com/picgo/20230528200649.png)

5. 编辑版本内容，真实打标签发布版本

![20230528200913](https://img.isxcode.com/picgo/20230528200913.png)


```markdown
#### 💥️ 重大变动

- something

#### ✨ 新功能

- something

#### 🎨 优化

- something

#### 🐛 修复

- something
```