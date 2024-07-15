package com.isxcode.star.modules.cluster.controller;

import com.isxcode.star.api.cluster.pojos.req.*;
import com.isxcode.star.api.cluster.pojos.res.PageClusterRes;
import com.isxcode.star.api.cluster.pojos.res.QueryAllClusterRes;
import com.isxcode.star.api.main.constants.ModuleCode;
import com.isxcode.star.common.annotations.successResponse.SuccessResponse;
import com.isxcode.star.modules.cluster.service.biz.ClusterBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "计算引擎模块")
@RestController
@RequestMapping(ModuleCode.CLUSTER)
@RequiredArgsConstructor
public class ClusterController {

    private final ClusterBizService clusterBizService;

    @Operation(summary = "添加计算集群接口")
    @PostMapping("/addCluster")
    @SuccessResponse("添加成功")
    public void addCluster(@Valid @RequestBody AddClusterReq addClusterReq) {

        clusterBizService.addCluster(addClusterReq);
    }

    @Operation(summary = "更新计算集群接口")
    @PostMapping("/updateCluster")
    @SuccessResponse("更新成功")
    public void updateCluster(@Valid @RequestBody UpdateClusterReq updateClusterReq) {

        clusterBizService.updateCluster(updateClusterReq);
    }

    @Operation(summary = "分页查询计算集群接口")
    @PostMapping("/pageCluster")
    @SuccessResponse("查询计算集群成功")
    public Page<PageClusterRes> pageCluster(@Valid @RequestBody PageClusterReq pageClusterReq) {

        return clusterBizService.pageCluster(pageClusterReq);
    }

    @Operation(summary = "删除计算集群接口")
    @PostMapping("/deleteCluster")
    @SuccessResponse("删除成功")
    public void deleteCluster(@Valid @RequestBody DeleteClusterReq deleteClusterReq) {

        clusterBizService.deleteCluster(deleteClusterReq);
    }

    @Operation(summary = "检测计算集群接口")
    @PostMapping("/checkCluster")
    @SuccessResponse("检测成功")
    public void checkCluster(@Valid @RequestBody CheckClusterReq checkClusterReq) {

        clusterBizService.checkCluster(checkClusterReq);
    }

    @Operation(summary = "设置默认集群")
    @PostMapping("/setDefaultCluster")
    @SuccessResponse("设置成功")
    public void setDefaultCluster(@Valid @RequestBody SetDefaultClusterReq setDefaultClusterReq) {

        clusterBizService.setDefaultCluster(setDefaultClusterReq);
    }

    @Operation(summary = "查询所有集群列表")
    @PostMapping("/queryAllCluster")
    @SuccessResponse("查询成功")
    public List<QueryAllClusterRes> queryAllCluster() {

        return clusterBizService.queryAllCluster();
    }
}
