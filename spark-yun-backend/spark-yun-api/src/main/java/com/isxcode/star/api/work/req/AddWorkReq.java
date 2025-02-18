package com.isxcode.star.api.work.req;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import lombok.Data;

@Data
public class AddWorkReq {

    @Schema(title = "作业名称", example = "作业1")
    @NotEmpty(message = "作业名称不能为空")
    private String name;

    @Schema(title = "作业类型",
        example = "类型编码：" + "SPARK_SQL： spark sql执行作业" + "EXE_JDBC： jdbc执行作业" + "QUERY_JDBC： jdbc查询作业"
            + "DATA_SYNC_JDBC： 数据同步作业" + "BASH： bash脚本作业" + "PYTHON： python脚本作业" + "API： 接口调用作业" + "PRQL： PRQL查询作业"
            + "CURL： CURL作业" + "PY_SPARK: PySpark作业")
    @NotEmpty(message = "作业类型不能为空")
    @Pattern(
        regexp = "^(SPARK_SQL|EXE_JDBC|QUERY_JDBC|DATA_SYNC_JDBC|BASH|PYTHON|SPARK_JAR|SPARK_CONTAINER_SQL|API|PRQL|CURL|EXCEL_SYNC_JDBC|PY_SPARK)$",
        message = "作业类型不支持")
    private String workType;

    @Schema(title = "工作流唯一id", example = "sy_48c4304593ea4897b6af999e48685896")
    @NotEmpty(message = "工作流id不能为空")
    private String workflowId;

    @Schema(title = "备注", example = "星期一执行的作业")
    private String remark;

    @Schema(title = "数据源", example = "新建sparksql作业和jdbc执行作业和jdbc查询作业，需要选择数据源，必填")
    private String datasourceId;

    @Schema(title = "集群id", example = "计算集群id")
    private String clusterId;

    @Schema(title = "集群节点id", example = "集群节点id")
    private String clusterNodeId;

    @Schema(title = "容器id", example = "容器id")
    private String containerId;

    @Schema(title = "是否支持hive", example = "true/false")
    private Boolean enableHive;
}
