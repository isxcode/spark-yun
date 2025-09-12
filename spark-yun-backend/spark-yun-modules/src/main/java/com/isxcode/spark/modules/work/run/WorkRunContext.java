package com.isxcode.spark.modules.work.run;

import com.isxcode.spark.api.agent.req.spark.SubmitWorkReq;
import com.isxcode.spark.api.cluster.dto.ScpFileEngineNodeDto;
import com.isxcode.spark.api.work.dto.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 作业运行上下文.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkRunContext {

    private String eventType;

    private List<List<String>> nodeMapping;

    private List<String> nodeList;

    private List<String> dagEndList;

    private List<String> dagStartList;

    private String flowInstanceId;

    private String workId;

    private String versionId;

    private String script;

    private ScpFileEngineNodeDto scpNodeInfo;

    private String agentHomePath;

    private String sparkHomePath;

    private String preStatus;

    private String clusterType;

    private String pid;

    /**
     * 作业实例id.
     */
    private String instanceId;

    /**
     * 数据源.
     */
    private String datasourceId;

    /**
     * 计算集群信息.
     */
    private ClusterConfig clusterConfig;

    /**
     * 同步作业信息.
     */
    private SyncWorkConfig syncWorkConfig;

    /**
     * Excel同步作业信息.
     */
    private ExcelSyncConfig excelSyncConfig;

    /**
     * 接口调用作业信息.
     */
    private ApiWorkConfig apiWorkConfig;

    /**
     * 同步规则.
     */
    private SyncRule syncRule;

    /**
     * 整库同步.
     */
    private DbMigrateConfig dbMigrateConfig;

    /**
     * 租户id.
     */
    private String tenantId;

    /**
     * 用户id.
     */
    private String userId;

    /**
     * 作业名称.
     */
    private String workName;

    /**
     * 作业类型.
     */
    private String workType;

    /**
     * 用户自定义jar的配置
     */
    private JarJobConfig jarJobConfig;

    /**
     * 自定义函数配置.
     */
    private List<String> funcConfig;

    /**
     * 依赖配置.
     */
    private List<String> libConfig;

    /**
     * 容器id.
     */
    private String containerId;

    /**
     * 日志构造器.
     */
    private StringBuilder logBuilder;

    /**
     * 事件id.
     */
    private String eventId;
    /**
     * 固定执行节点ID（需要复用上传文件所在节点）.
     */
    private String nodeId;


    private String log;

    private SubmitWorkReq executeReq;

    private String agentHost;

    private String agentPort;

    private String appId;
}
