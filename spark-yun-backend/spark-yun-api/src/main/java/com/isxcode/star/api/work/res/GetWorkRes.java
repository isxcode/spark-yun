package com.isxcode.star.api.work.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.isxcode.star.api.work.dto.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetWorkRes {

    @Schema(title = "作业唯一id", example = "sy_4f07ab7b1fe54dab9be884e410c53af4")
    @NotEmpty(message = "workId不能为空")
    private String workId;

    @Schema(title = "数据源唯一id", example = "sy_fd34e4a53db640f5943a4352c4d549b9")
    private String datasourceId;

    @Schema(title = "运行脚本", example = "show databases;")
    private String script;

    @Schema(title = "cron定时配置")
    private CronConfig cronConfig;

    @Schema(title = "同步作业配置")
    private SyncWorkConfig syncWorkConfig;

    @Schema(title = "Excel作业配置")
    private ExcelSyncConfig excelSyncConfig;

    @Schema(title = "接口调用作业配置")
    private ApiWorkConfig apiWorkConfig;

    @Schema(title = "集群配置")
    private ClusterConfig clusterConfig;

    @Schema(title = "数据同步规则")
    private SyncRule syncRule;

    private String workType;

    private String workflowId;

    private String name;

    private JarJobConfig jarJobConfig;

    private List<String> funcList;

    private List<String> libList;

    private String containerId;

    private List<String> alarmList;
}
