分支规范

场景1

张三作为技术开发，准备开发编号为20的需求
只关注自己的分支即可

1. 拉取分支

git remote update upstream -p
git checkout -b 0.0.2-#20
git pull upstream 0.0.2-#20
git push origin 0.0.2-#20

2. 编写代码

git add .
gradle start
git commit -m “:sparkles: 新增需求 #20”

3. 合并远程冲突

git remote update upstream -p
git merge upstream/0.0.2-#20
git push origin 0.0.2-#20

4. 提交pr等待合并

通过界面提交pr

5. 审核技术开发提交的pr，此pr不可能有冲突
   直接使用merge pull request

场景2

张三作为技术负责人，准备发版编号为18和19的需求

1. 从界面切出下一个版本的分支
   0.0.2 -》0.0.3

2. 通过界面提交合并pr
   直接使用squash and merge
   :bookmark: 发版0.0.3 (#19)

3. 合并冲突，将0.0.2-19的分支合并到0.0.3分支上
   git remote update upstream -p
   git checkout 0.0.3
   git merge upstream/0.0.2-19


场景3

张三作为技术负责人，准备部署demo环境。

rebase 合并




gradle  start


————————————
发版
Ispong/0.0.1-#14 —> isxcode/0.0.1-#14  pull merge
> 因为多人一起维护，可能需要看别人的分支记录的代码
> 所以使用pull merge
0.0.1 —> 0.0.2  ui切分支
> 走界面切分支
0.0.1-#14 --> 0.0.2  squash合并
> 需求发版，不需要了解过程，只需要关注结果，使用压缩合并
0.0.2 —> latest  rebase 合并
> latest是版本分支，需要保证分支的一致性和流水性
> 使用rebase合并


———————————

rebase 冲突

git fetch upstream
git rebase upstream/branch_name
git add .
git rebase --continue

———————————
修复rebase冲突
git pull upstream latest
git checkout latest
git rebase 0.0.2
git push -u upstream latest

————————
git fetch upstream
git branch -D latest
git checkout -b latest upstream/latest
git pull
git branch -D 0.0.2
git checkout -b 0.0.2 upstream/0.0.2
git pull
git merge latest
git push upstream 0.0.2
git rebase --continue 继续
git rebase --abort 中止
git push -f 推送

————
:bookmark:  部署0.0.2

main 大版稳定分支   0.1.0 -> 0.2.0 -> 0.3.0 -> 0.4.0
latest 所有版本分支  0.0.1 -> 0.0.2 -> 0.1.0 -> 0.1.2 -> 0.2.0
0.0.2 版本分支    0.0.1 -> 0.0.1-#14 -> 0.0.1-#15 -> 0.0.2   :sparkles: xxx #14  :bug: xxx #14
0.0.2-#18 需求分支   0.0.2 -> [commits] -> #19

分支需求  保持分支的干净

删除文件单独 fire



#### 开发

git fetch upstream
git checkout -b 0.0.4-#58 upstream/0.0.4-#58
git push origin 0.0.4-#58
