package com.isxcode.star.api.form.pojos.req;

import com.isxcode.star.api.form.pojos.dto.FomComponentDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class FomSaveComponentsReq {

	@Schema(title = "表单唯一id", example = "sy_fd34e4a53db640f5943a4352c4d549b9")
	@NotEmpty(message = "formId不能为空")
	private String formId;

	private String insertSql;

	private String deleteSql;

	private String updateSql;

	private String selectSql;

	@NotEmpty(message = "组件不能为空")
	@Schema(title = "组件列表")
	private List<FomComponentDto> components;
}
