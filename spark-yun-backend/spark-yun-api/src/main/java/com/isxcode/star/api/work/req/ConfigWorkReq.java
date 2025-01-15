package com.isxcode.star.api.work.req;

import com.isxcode.star.api.work.dto.*;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

import java.util.List;

@Data
public class ConfigWorkReq {

    @Schema(title = "作业唯一id", example = "sy_4f07ab7b1fe54dab9be884e410c53af4")
    @NotEmpty(message = "workId不能为空")
    private String workId;

    @Schema(title = "数据源唯一id", example = "sy_fd34e4a53db640f5943a4352c4d549b9")
    private String datasourceId;

    @Schema(title = "运行脚本", example = "show databases;")
    private String script;

    @Schema(title = "cron定时配置")
    private CronConfig cronConfig;

    @Schema(title = "数据同步作业配置")
    private SyncWorkConfig syncWorkConfig;

    @Schema(title = "Excel导入作业配置")
    private ExcelSyncConfig excelSyncConfig;

    @Schema(title = "接口调用作业配置")
    private ApiWorkConfig apiWorkConfig;

    @Schema(title = "集群配置")
    private ClusterConfig clusterConfig;

    @Schema(title = "数据同步规则")
    private SyncRule syncRule;

    @Schema(title = "自定义jar作业的配置文件")
    private JarJobConfig jarJobConfig;

    @Schema(title = "自定义函数选择")
    private List<String> funcList;

    @Schema(title = "依赖选择")
    private List<String> LibList;

    @Schema(title = "容器id", example = "sy_fd34e4a53db640f5943a4352c4d549b9")
    private String containerId;

    @Schema(title = "告警")
    private List<String> alarmList;
}
