#### 作业执行逻辑核心

> 开发者只需要继承WorkExecutor抽象类，并实现其中的execute()方法即可

```java
/**
 * workRunContext 作业运行上下文，包含作业运行所需的所有参数
 * workInstance   作业没运行一次，都会产生一个实例，在作业运行前，需要提前初始化
 */
protected abstract void execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance);
```

#### 继承的作业执行需要加上@Service

#### 补充工厂中的bean返回

```java
@Slf4j
@RequiredArgsConstructor
@Component
public class WorkExecutorFactory {

  private final ApplicationContext applicationContext;

  public WorkExecutor create(String workType) {

    switch (workType) {
      case WorkType.QUERY_SPARK_SQL:
        return applicationContext.getBean(SparkSqlExecutor.class);
      case WorkType.QUERY_JDBC_SQL:
        return applicationContext.getBean(QuerySqlExecutor.class);
      case WorkType.EXECUTE_JDBC_SQL:
        return applicationContext.getBean(ExecuteSqlExecutor.class);
      default:
        throw new SparkYunException("作业类型不存在");
    }
  }
}
```