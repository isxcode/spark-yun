package com.isxcode.star.api.monitor.pojos.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class GetInstanceMonitorReq {

	@Schema(title = "数据", example = "2020-12-12")
	@NotNull(message = "localDate不能为空")
	private Date localDate;
}
