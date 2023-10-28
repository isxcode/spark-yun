?> 将`#64`和`#74`需求发布到0.0.6版本中

!> 此操作只有技术负责人可以操作

##### 1. 切出版本分支

> 先从最新分支`0.0.5`上切出`0.0.6`

```bash
git fetch upstream 
git checkout upstream/0.0.5
git checkout -b 0.0.6 
git push upstream 0.0.6
```

##### 2. 找到目标分支

> 使用命令查找目标分支，`0.0.5-#64`和`0.0.5-#74`。

```bash
git branch -r -l "upstream/*-#64"
```

##### 3. 创建pr合并

![20230529145836](https://img.isxcode.com/picgo/20230529145836.png)

!> 合并内容,必须与issue标题保持一致，并在最后加上版本号，如下写法：<br/>
`:art: 编写一篇开发手册 #74 v0.0.6 ` <br/>
需求分支合并到版本分支中，必须使用`squash and merge`方式合并代码。

![20230529150038](https://img.isxcode.com/picgo/20230529150038.png)

> 重复上面的操作，直到所有的需求编号全部合并完成。 <br/>
> 如果合并过程中出现冲突，需要技术负责人对代码进行rebase解决冲突，禁止使用直接merge。

```bash
git fetch upstream
git checkout upstream/0.0.5-#74
git rebase upstream/latest
git commit -m ":twisted_rightwards_arrows: 0.0.6合并冲突"
git rebase --continue
git push upstream 0.0.5-#74
```

##### 4. latest版本发布

> 需要将`0.0.6`分支代码合并到 `latest`分支中。<br/>
> 注意合并内容：`:bookmark: v0.0.6`

```bash
git checkout 0.0.6
git merge upstream/0.0.6
git rebase upstream/latest
git push upstream 0.0.6 --force
```

![20230529151639](https://img.isxcode.com/picgo/20230529151639.png)

##### 5. Tag发布

<br/>

- 发版地址：https://github.com/isxcode/spark-yun/releases

![20230528200400](https://img.isxcode.com/picgo/20230528200400.png)

![20230528200600](https://img.isxcode.com/picgo/20230528200600.png)

> 创建标签(Tag)名称，规则`v0.0.1`

![20230528200649](https://img.isxcode.com/picgo/20230528200649.png)

> 编辑版本内容

![20230528200913](https://img.isxcode.com/picgo/20230528200913.png)

- 发版模版

```markdown
#### 💥️ 重大变动

- 一些操作上的大变动

#### ✨ 新功能

- 新开发的功能

#### 🎨 优化

- 项目优化的地方

#### 🐛 修复

- 修复的bug
```