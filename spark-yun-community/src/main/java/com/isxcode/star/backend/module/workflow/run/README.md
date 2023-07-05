#### 作业流执行逻辑

> 作业流执行逻辑包含，定时调度运行作业流和普通点击运行作业流

#### 数据结构

1. 前端传递参数

```json
[
  {
    "id": "1",
    "shape": "dag-node",
    "x": 290,
    "y": 110,
    "data": {
      "label": "读数据",
      "status": "success"
    },
    "ports": [
      {
        "id": "1-1",
        "group": "bottom"
      }
    ]
  },
  {
    "id": "2",
    "shape": "dag-node",
    "x": 290,
    "y": 225,
    "data": {
      "label": "逻辑回归",
      "status": "success"
    },
    "ports": [
      {
        "id": "2-1",
        "group": "top"
      },
      {
        "id": "2-2",
        "group": "bottom"
      },
      {
        "id": "2-3",
        "group": "bottom"
      }
    ]
  },
  {
    "id": "3",
    "shape": "dag-node",
    "x": 170,
    "y": 350,
    "data": {
      "label": "模型预测",
      "status": "success"
    },
    "ports": [
      {
        "id": "3-1",
        "group": "top"
      },
      {
        "id": "3-2",
        "group": "bottom"
      }
    ]
  },
  {
    "id": "4",
    "shape": "dag-node",
    "x": 450,
    "y": 350,
    "data": {
      "label": "读取参数",
      "status": "success"
    },
    "ports": [
      {
        "id": "4-1",
        "group": "top"
      },
      {
        "id": "4-2",
        "group": "bottom"
      }
    ]
  },
  {
    "id": "5",
    "shape": "dag-edge",
    "source": {
      "cell": "1",
      "port": "1-1"
    },
    "target": {
      "cell": "2",
      "port": "2-1"
    },
    "zIndex": 0
  },
  {
    "id": "6",
    "shape": "dag-edge",
    "source": {
      "cell": "2",
      "port": "2-2"
    },
    "target": {
      "cell": "3",
      "port": "3-1"
    },
    "zIndex": 0
  },
  {
    "id": "7",
    "shape": "dag-edge",
    "source": {
      "cell": "2",
      "port": "2-3"
    },
    "target": {
      "cell": "4",
      "port": "4-1"
    },
    "zIndex": 0
  }
]
```

2. 后端将前端传递的参数通过WorkflowUtils解析成以下四类数据，并保存到数据流配置表中

> 起点结构

```json
[
  1
]
```
> 终点结构

```json
[
  3
]
```

> 节点结构

```json
[
  1,2,3
]
```

> 线结构

```json
[
  [1,2],
  [2,3],
  [3,4]
]
```

3. 事件body

```java
class demo{
    
  // 工作流实例id  
  private String flowInstanceId;
    
  // 当前节点运行作业id
  private String workId;

  // 如果是发布，则需要当前作业版本id
  private String versionId;
    
  // 线结构
  private List<List<String>> nodeMapping;

  // 节点结构
  private List<String> nodeList;
    
  // 起点结构
  private List<String> dagStartList;

  // 终点结构
  private List<String> dagEndList;

  // 因为是异步，所以用到用户id
  private String userId;

  // 因为是异步，所以用到租户id
  private String tenantId;
}
```

4. 核心逻辑

> 使用spring的event事件推送的方式
> 1. 点击工作流运行
> 2. 获取工作流配置
> 3. 查询起点数据
> 4. 封装起点的WorkflowRunEvent
> 5. 使用异步将起点的所有作业全部推送出去
> 6. 事件接受推送
> 7. 在事件中第一时间，立马给工作流实例id上锁
> 8. 判断当前节点是否可以运行
> 9. 判断父节点是否运行完
> 10. 判断当前节点是否正在运行
> 11. 判断当前节点是否已经跑过
> 12. 判断当前节点定时是否跑过
> 13. 符合条件后，修改作业状态，解锁
> 14. 开始调用作业的运行逻辑
> 15. 执行完后，判断结束节点是否都跑完，需要加锁
> 16. 如果没有跑完，获取自己的子节点，继续推送事件
> 17. 直到结束节点都已跑完