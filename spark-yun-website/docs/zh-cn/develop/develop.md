?> 真实开发案例

##### 1. 选择任务

> 查看已分配任务，将状态改为`进行中`，切勿修改他人任务状态。

- 项目管理地址：https://github.com/orgs/isxcode/projects/7/views/3

![20230529113038](https://img.isxcode.com/picgo/20230529113038.png)

##### 2. 阅读理解任务

> 查看任务内容，获取当前任务编号`#74`

![20230529113243](https://img.isxcode.com/picgo/20230529113243.png)

##### 3. 找寻开发主分支

> 所有的分支都以当前`最新版本`分支为基础，除非是缺陷分支。<br/>
> `缺陷分支`：哪个版本发现缺陷，就在哪个版本分支上切分支。

- 项目github地址：https://github.com/isxcode/spark-yun

![20230528201236](https://img.isxcode.com/picgo/20230528201236.png)

##### 4. Fork代码，并拉取代码

> Fork项目，并拉取最新版本分支`0.0.5`代码。

- Fork项目：https://github.com/isxcode/spark-yun/fork

```bash
# 将isxcode组织的代码源加入到项目的upstream中。
git remote add upstream https://github.com/isxcode/spark-yun.git
# 更新组织代码
git remote update upstream -p
# 切出远程主分支
git checkout upstream/0.0.5
```

##### 5. 创建需求分支

> 创建需求分支并提交到自己fork的项目中。</br>
> 分支命名规范：`0.0.5-#74` </br>
> 如果别人已经切出了，则直接拉取并更新代码，多人开发同一个需求，需要提前协商。

- 远程已有分支

```bash
git checkout upstream/0.0.5-#74
git push origin 0.0.5-#74
```

- 本地已有分支

```bash
git fetch upstream
git merge upstream/0.0.5-#74
git push origin 0.0.5-#74
```

- 远程未建分支

```bash
git checkout -b 0.0.5-#74
git push origin 0.0.5-#74
git push upstream 0.0.5-#74
```

##### 6. 编写代码，提交代码

> 提交commit规范：</br>
> 需要加上前缀`:xxx:`，参考 https://gitmoji.dev/ </br>

!> 结尾不要加上需求编号

- `git commit -m ":fire: 删除文件"`
- `git commit -m ":sparkles: 新增需求"`
- `git commit -m ":bug: bug相关"`
- `git commit -m ":memo: 文档相关"`

##### 7. 推送到自己的项目

```bash
git push origin 0.0.5-#74
```

##### 8. 提交pr

> 将自己Fork项目中的代码，提交到isxcode组织中。

- 提交pr地址：https://github.com/isxcode/spark-yun/compare

![20230528202354](https://img.isxcode.com/picgo/20230528202354.png)

![20230529121600](https://img.isxcode.com/picgo/20230529121600.png)

- 编写pr提交内容

!> 结尾必须带上任务编号，如`#74`。

![20230529121746](https://img.isxcode.com/picgo/20230529121746.png)

!> 需求分支，只有`rebase and merge`按钮为绿色，这种状态才允许被合并，其它状态pr都会被拒绝。

![20230529140518](https://img.isxcode.com/picgo/20230529140518.png)

##### 8. 解决冲突

> 多人开发同一需求，可能存在冲突问题。<br/>
> 如果发现rebase按钮为灰色，则需要本地合并完冲突后，再次提交的代码。<br/>
> 首先关闭pr。

![20230529142241](https://img.isxcode.com/picgo/20230529142241.png)

```bash
# 更新isxcode的组织代码
git fetch upstream
# 变基操作，将本地的分支与isxcode组织的代码对齐，以下操作需要解决冲突
git rebase upstream/0.0.5-#74
# 结束冲突修复
git rebase --continue
# 推送到自己fork的项目中，再次创建pr
git push origin 0.0.5-#74 --force
```

![20230529143318](https://img.isxcode.com/picgo/20230529143318.png)
