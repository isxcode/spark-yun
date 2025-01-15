package com.isxcode.star.modules.work.run;

import com.isxcode.star.api.work.dto.*;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WorkRunContext {

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
     * 脚本.
     */
    private String script;

    /**
     * 租户id.
     */
    private String tenantId;

    /**
     * 用户id.
     */
    private String userId;

    /**
     * 作业id.
     */
    private String workId;

    /**
     * 作业名称.
     */
    private String workName;

    /**
     * 作业类型.
     */
    private String workType;

    /**
     * 版本id.
     */
    private String versionId;

    /**
     * 日志构造器.
     */
    private StringBuilder logBuilder;

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

}
