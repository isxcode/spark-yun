package com.isxcode.star.modules.func.controller;

import com.isxcode.star.api.func.pojos.req.UpdateFuncReq;
import com.isxcode.star.api.func.pojos.res.PageFuncRes;
import com.isxcode.star.api.main.constants.ModuleCode;
import com.isxcode.star.api.func.pojos.req.AddFuncReq;
import com.isxcode.star.api.func.pojos.req.DeleteFuncReq;
import com.isxcode.star.api.func.pojos.req.PageFuncReq;
import com.isxcode.star.common.annotations.successResponse.SuccessResponse;
import com.isxcode.star.modules.func.service.FuncBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Tag(name = "自定义函数模块")
@RestController
@RequestMapping(ModuleCode.FUNC)
@RequiredArgsConstructor
public class FuncController {

	private final FuncBizService funcBizService;

	@Operation(summary = "添加自定义函数")
	@PostMapping("/addFunc")
	@SuccessResponse("添加成功")
	public void addFunc(@Valid @RequestBody AddFuncReq addFuncReq) {

		funcBizService.addFunc(addFuncReq);
	}

	@Operation(summary = "更新自定义函数")
	@PostMapping("/updateFunc")
	@SuccessResponse("更新成功")
	public void updateFunc(@Valid @RequestBody UpdateFuncReq updateFuncReq) {

		funcBizService.updateFunc(updateFuncReq);
	}

	@Operation(summary = "删除自定义函数")
	@PostMapping("/deleteFunc")
	@SuccessResponse("删除成功")
	public void deleteFunc(@Valid @RequestBody DeleteFuncReq deleteFuncReq) {

		funcBizService.deleteFunc(deleteFuncReq);
	}

	@Operation(summary = "查询自定义函数")
	@PostMapping("/pageFunc")
	@SuccessResponse("查询成功")
	public Page<PageFuncRes> pageFunc(@Valid @RequestBody PageFuncReq pageFuncReq) {

		return funcBizService.pageFunc(pageFuncReq);
	}
}