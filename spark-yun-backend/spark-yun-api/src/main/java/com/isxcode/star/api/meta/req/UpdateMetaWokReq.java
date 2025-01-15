package com.isxcode.star.api.meta.req;

import com.isxcode.star.api.work.dto.CronConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UpdateMetaWokReq {

    @Schema(title = "采集任务Id", example = "")
    @NotEmpty(message = "采集任务Id不能为空")
    private String id;

    @Schema(title = "采集任务名", example = "")
    @NotEmpty(message = "采集任务名不能为空")
    private String name;

    @Schema(title = "数据源类", example = "")
    @NotEmpty(message = "数据源不能为空")
    private String dbType;

    @Schema(title = "数据源id", example = "")
    @NotEmpty(message = "数据源不能为空")
    private String datasourceId;

    @Schema(title = "采集类型", example = "ALL_TABLE全表,CUSTOM_TABLE指定表")
    @NotEmpty(message = "采集表类型不能为空")
    private String collectType;

    @Schema(title = "指定表名规则", example = "")
    private String tablePattern;

    @Schema(title = "调度规则", example = "")
    private CronConfig cronConfig;

    @Schema(title = "备注", example = "")
    private String remark;

    @Schema(title = "是否立即执行", defaultValue = "false")
    private boolean nowCollect;
}
