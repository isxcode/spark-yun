package com.isxcode.star.api.datasource.req;

import com.isxcode.star.api.datasource.constants.DatasourceType;
import com.isxcode.star.api.datasource.dto.KafkaConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class CheckConnectReq {

    @Schema(title = "数据源jdbcUrl", example = "jdbc:mysql://localhost:3306")
    @NotEmpty(message = "地址不能为空")
    private String jdbcUrl;

    @Schema(title = "数据源用户名", example = "root")
    private String username;

    @Schema(title = "数据源密码", example = "ispong123")
    private String passwd;

    @Schema(title = "数据源类型", example = DatasourceType.MYSQL)
    @NotEmpty(message = "数据源类型不能为空")
    private String dbType;

    @Schema(title = "数据源驱动", example = "数据源驱动id")
    @NotEmpty(message = "数据源驱动不能为空")
    private String driverId;

    @Schema(title = "kafka数据源配置")
    private KafkaConfig kafkaConfig;
}
