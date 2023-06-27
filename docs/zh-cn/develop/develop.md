?> 真实开发案例，以`#74`需求为例。

##### 1. 选择任务

> 查看已分配任务，将状态改为`进行中`，切勿修改他人任务状态。

- 项目管理地址：https://github.com/orgs/isxcode/projects/7/views/3

![20230529113038](https://img.isxcode.com/picgo/20230529113038.png)

##### 2. 阅读并理解任务

> 查看任务内容，获取当前任务编号`#74`

![20230529113243](https://img.isxcode.com/picgo/20230529113243.png)

##### 3. 确认开发主分支

> 所有的需求分支都以当前`最新版本`分支为基础。</br>
> 即如果当前的最新版本是`0.0.5`，需求编号为`#74`,则从`0.0.5`的分支上切出需求分支`0.0.5-#74`。

- 项目github地址：https://github.com/isxcode/spark-yun

![20230528201236](https://img.isxcode.com/picgo/20230528201236.png)

##### 4. Fork并拉取代码

> Fork项目，并拉取最新版本分支`0.0.5`代码。

- Fork项目：https://github.com/isxcode/spark-yun/fork
- [启动项目(可选)](/zh-cn/install/source-deploy.md)

```bash
# 将isxcode组织的代码仓库加入到本地的upstream中
git remote add upstream https://github.com/isxcode/spark-yun.git
# 更新isxcode组织代码
git remote update upstream -p
# 拉取远程主分支-0.0.5
git checkout --track upstream/0.0.5
```

##### 5. 创建需求分支`0.0.5-#74`

> 创建需求分支并提交到自己fork的项目中。</br>
> 分支命名规范：`0.0.5-#74` </br>

- **upstream远程**已有分支

```bash
# 切出远程0.0.5-#74分支
git checkout --track upstream/0.0.5-#74
# 推送到自己的仓库
git push origin 0.0.5-#74
```

- **本地**已有分支

```bash
# 更新isxcode仓库代码
git fetch upstream
# 合并冲突
git rebase upstream/0.0.5-#74
# 推到自己的仓库
git push origin 0.0.5-#74
```

- **upstream远程**未建分支

```bash
# 创建需求分支0.0.5-#74
git checkout -b 0.0.5-#74
# 推到isxcode组织远程仓库
git push upstream 0.0.5-#74
# 推送完upstream后再开发！！！
# 推到自己的仓库
git push origin 0.0.5-#74
```

##### 6. 编写代码，提交代码

> 提交commit规范：</br>
> 需要加上前缀`:xxx:`，参考 https://gitmoji.dev/ </br>

!> 结尾不要加上需求编号！！！

常见提交模版：
- `git commit -m ":fire: 删除文件"`
- `git commit -m ":sparkles: 新增需求"`
- `git commit -m ":bug: 修复权限"`
- `git commit -m ":memo: 修改文档"`

##### 7. 推送到自己的项目

!> 一定不要直接推到远程isxcode仓库中

```bash
git push origin 0.0.5-#74
```

##### 8. 提交pr

> 将**自己仓库**中的代码，提交到**isxcode组织仓库**中。</br>
> 前面选择isxcode组织仓库，后面选择自己的仓库

- 提交pr地址：https://github.com/isxcode/spark-yun/compare

![20230528202354](https://img.isxcode.com/picgo/20230528202354.png)

![20230529121600](https://img.isxcode.com/picgo/20230529121600.png)

- 编写pr提交内容

> 提交pr内容规范：</br>
> 需要在内容的最前面，添加任务编号`#74` </br>
> 例如：`#74 添加开发文档`

![20230627182918](https://img.isxcode.com/picgo/20230627182918.png)

!> 需求分支的pr，只有`Rebase and merge`按钮为绿色，这种状态才允许被合并，其它状态的pr都会被拒绝。

![20230529140518](https://img.isxcode.com/picgo/20230529140518.png)

##### 8. 解决冲突

> 多人开发同一需求分支，可能存在冲突问题。<br/>
> 如果发现`Rebase and merge`按钮为灰色，则需要本地合并完冲突后，再次提交代码。<br/>

![20230529142241](https://img.isxcode.com/picgo/20230529142241.png)

```bash
# 更新isxcode的组织代码
git fetch upstream
# 变基操作，将本地的分支与isxcode组织的代码对齐，以下操作需要解决冲突
git rebase upstream/0.0.5-#74
# 结束冲突修复
git rebase --continue
# 推送到自己fork的项目中
git push origin 0.0.5-#74 --force
```

> 此状态才可以合并pr

![20230529143318](https://img.isxcode.com/picgo/20230529143318.png)
