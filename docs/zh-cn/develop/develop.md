#### 开发手册

> 以真实开发为例

##### 查看自己的任务

> 查看已被分配到的任务，将状态改为进行中。

- 项目管理地址：https://github.com/orgs/isxcode/projects/7/views/3

![20230529113038](https://img.isxcode.com/picgo/20230529113038.png)

##### 阅读理解自己的任务

> 点开任务，查看任务内容，当前任务编号`#74`

![20230529113243](https://img.isxcode.com/picgo/20230529113243.png)

##### 寻找当前最新的发版主分支

> 除非特殊开发需求分支，其余切分支的行为都已当前发布`最新版本`为基础，

- 项目github地址：https://github.com/isxcode/spark-yun

![20230528201236](https://img.isxcode.com/picgo/20230528201236.png)

##### Fork代码，并拉取代码

> fork项目，拉取需求主分支`0.0.5`代码。

- Fork项目：https://github.com/isxcode/spark-yun/fork

```bash
# 加入github远程主项目
git remote add upstream https://github.com/isxcode/spark-yun.git
# 更新代码
git fetch upstream
# 切出远程主分支
git checkout upstream/0.0.5
```

##### 切出自己的需求分支

> 切出需求分支并提交到自己的项目中，命令规范`0.0.5-#74` </br>
> 如果别人已经切出了，则直接拉取，多人开发同一个需求，需要提前协商。

```bash
# 远程已有分支
git checkout upstream/0.0.5-#74
git push origin 0.0.5-#74

# 远程未建分支
git checkout -b 0.0.5-#74
git push origin 0.0.5-#74
git push upstream 0.0.5-#74
```

##### 提交代码

> 提交commit规范，需要加上前缀`:xxx:`，参考 https://gitmoji.dev/

- `git commit -m ":fire: 删除文件"`
- `git commit -m ":sparkles: 新增需求"`
- `git commit -m ":bug: bug相关"`
- `git commit -m ":memo: 文档相关"`

##### 提交自己的代码

```bash
git push origin 0.0.5-#74
```

##### 提交代码