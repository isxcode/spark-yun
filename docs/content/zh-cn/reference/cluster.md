?> 计算集群： 负责支持SparkSql作业运行的环境。支持k8s,yarn,standalone计算集群

> 点击添加集群按钮，创建计算集群。

<img src="https://img.isxcode.com/picgo/20230415165827.png" width="900">

> 集群列表中展示所有计算集群

!> 注意：集群的状态不会自动刷新，需要用户手动点击检测按钮进行检测，并修改状态。

<img src="https://img.isxcode.com/picgo/20230415165505.png" width="900">

> 点击集群名称，可以跳转至集群节点管理界面，在此界面可以添加集群的节点信息，用户只需要添加名称、Host、Port、密码，其余参数会自动生成并补充。 

<img src="https://img.isxcode.com/picgo/20230415165913.png" width="700">

!> 服务器需要支持 mpstat <br/> 
需提前安装命令 yum install sysstat <br/>
节点列表中会显示节点信息，需要手动点击记检测按钮，对节点信息进行手动检测。<br/>
用户需要先点击检测按钮，然后点击安装按钮，即可对该节点做监听。当状态为可用时，即可用于spark作业的运行。

<img src="https://img.isxcode.com/picgo/20230415165616.png" width="900">

!> 如果使用k8s集群方式，需要配置podTemplate文件

```bash
vim /home/ispong/pod-init.yml
```

```yml
apiVersion: v1
kind: Pod
metadata:
  name: host-pod
spec:
  restartPolicy: never
  securityContext:
    privileged: true
    runAsUser: 1000 
    runAsGroup: 1001
  hostAliases:  # 域名映射
    - ip: "172.16.215.105"
      hostnames:
        - "isxcode"
  volumes:
    - name: users
      hostPath:
        path: /etc/passwd
```

```config
spark.kubernetes.driver.podTemplateFile    /home/ispong/pod-init.yaml
spark.kubernetes.executor.podTemplateFile    /home/ispong/pod-init.yaml
```