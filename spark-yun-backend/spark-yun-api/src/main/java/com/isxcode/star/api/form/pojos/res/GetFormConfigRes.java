package com.isxcode.star.api.form.pojos.res;

import com.isxcode.star.api.form.pojos.dto.FormComponentDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GetFormConfigRes {

	private String formId;

	private List<FormComponentDto> components;
}
