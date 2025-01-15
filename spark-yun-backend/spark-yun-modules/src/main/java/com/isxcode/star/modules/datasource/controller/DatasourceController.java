package com.isxcode.star.modules.datasource.controller;

import com.isxcode.star.api.datasource.req.*;
import com.isxcode.star.api.datasource.res.*;
import com.isxcode.star.api.main.constants.ModuleCode;
import com.isxcode.star.api.user.constants.RoleType;
import com.isxcode.star.common.annotations.successResponse.SuccessResponse;
import com.isxcode.star.modules.datasource.service.biz.DatasourceBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "数据源模块")
@RequestMapping(ModuleCode.DATASOURCE)
@RestController
@RequiredArgsConstructor
public class DatasourceController {

    private final DatasourceBizService datasourceBizService;

    @Operation(summary = "添加数据源接口")
    @PostMapping("/addDatasource")
    @SuccessResponse("添加成功")
    public void addDatasource(@Valid @RequestBody AddDatasourceReq addDatasourceReq) {

        datasourceBizService.addDatasource(addDatasourceReq);
    }

    @Secured({RoleType.TENANT_ADMIN})
    @Operation(summary = "更新数据源接口")
    @PostMapping("/updateDatasource")
    @SuccessResponse("更新成功")
    public void updateDatasource(@Valid @RequestBody UpdateDatasourceReq updateDatasourceReq) {

        datasourceBizService.updateDatasource(updateDatasourceReq);
    }

    @Secured({RoleType.TENANT_MEMBER, RoleType.TENANT_ADMIN})
    @Operation(summary = "查询数据源列表接口")
    @PostMapping("/pageDatasource")
    @SuccessResponse("查询数据源成功")
    public Page<PageDatasourceRes> pageDatasource(@Valid @RequestBody PageDatasourceReq pageDatasourceReq) {

        return datasourceBizService.pageDatasource(pageDatasourceReq);
    }

    @Secured({RoleType.TENANT_ADMIN})
    @Operation(summary = "删除数据源接口")
    @PostMapping("/deleteDatasource")
    @SuccessResponse("删除成功")
    public void deleteDatasource(@Valid @RequestBody DeleteDatasourceReq deleteDatasourceReq) {

        datasourceBizService.deleteDatasource(deleteDatasourceReq);
    }

    @Operation(summary = "测试数据源连接接口(列表中测试)")
    @PostMapping("/testConnect")
    @SuccessResponse("检测完成")
    public TestConnectRes testConnect(@Valid @RequestBody GetConnectLogReq testConnectReq) {

        return datasourceBizService.testConnect(testConnectReq);
    }

    @Operation(summary = "测试数据源连接接口(弹窗中测试)")
    @PostMapping("/checkConnect")
    @SuccessResponse("检测完成")
    public CheckConnectRes checkConnect(@Valid @RequestBody CheckConnectReq checkConnectReq) {

        return datasourceBizService.checkConnect(checkConnectReq);
    }

    @Secured({RoleType.TENANT_MEMBER, RoleType.TENANT_ADMIN})
    @Operation(summary = "查询连接日志接口")
    @PostMapping("/getConnectLog")
    @SuccessResponse("获取成功")
    public GetConnectLogRes getConnectLog(@Valid @RequestBody GetConnectLogReq getConnectLogReq) {

        return datasourceBizService.getConnectLog(getConnectLogReq);
    }

    @PostMapping("/uploadDatabaseDriver")
    @Operation(summary = "上传数据源驱动接口(Swagger有Bug不能使用)")
    @SuccessResponse("上传成功")
    public void uploadDatabaseDriver(@RequestParam("driver") MultipartFile driver,
        @RequestParam("dbType") String dbType, @RequestParam("name") String name,
        @RequestParam("remark") String remark) {

        datasourceBizService.uploadDatabaseDriver(driver, dbType, name, remark);
    }

    @PostMapping("/pageDatabaseDriver")
    @Operation(summary = "查询数据库驱动接口")
    @SuccessResponse("查询成功")
    public Page<PageDatabaseDriverRes> pageDatabaseDriver(@RequestBody PageDatabaseDriverReq pageDatabaseDriverReq) {

        return datasourceBizService.pageDatabaseDriver(pageDatabaseDriverReq);
    }

    @Secured({RoleType.TENANT_ADMIN})
    @PostMapping("/deleteDatabaseDriver")
    @Operation(summary = "删除数据库驱动接口")
    @SuccessResponse("删除成功")
    public void deleteDatabaseDriver(@RequestBody DeleteDatabaseDriverReq deleteDatabaseDriverReq) {

        datasourceBizService.deleteDatabaseDriver(deleteDatabaseDriverReq);
    }

    @PostMapping("/settingDefaultDatabaseDriver")
    @Operation(summary = "设置默认驱动接口")
    @SuccessResponse("设置成功")
    public void settingDefaultDatabaseDriver(
        @RequestBody SettingDefaultDatabaseDriverReq settingDefaultDatabaseDriverReq) {

        datasourceBizService.settingDefaultDatabaseDriver(settingDefaultDatabaseDriverReq);
    }

    @PostMapping("/getDefaultDatabaseDriver")
    @Operation(summary = "获取默认驱动接口")
    @SuccessResponse("获取成功")
    public GetDefaultDatabaseDriverRes getDefaultDatabaseDriver(
        @RequestBody GetDefaultDatabaseDriverReq getDefaultDatabaseDriverReq) {

        return datasourceBizService.getDefaultDatabaseDriver(getDefaultDatabaseDriverReq);
    }
}
