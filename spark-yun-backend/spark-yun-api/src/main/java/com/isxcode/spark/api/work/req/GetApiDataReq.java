package com.isxcode.spark.api.work.req;

import com.isxcode.spark.api.work.dto.SyncColumnInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class GetApiDataReq {

    @Schema(title = "请求类型", example = "GET/POST")
    private String requestType;

    @Schema(title = "请求地址", example = "http://")
    private String requestHttp;

    @Schema(title = "请求头", example = "")
    private Map<String, String> requestHeader;

    @Schema(title = "请求模版", example = "")
    private String requestBody;

    @Schema(title = "响应模版", example = "")
    private String responseBody;

    @Schema(title = "每页大小", example = "")
    private Integer pageSize;

    @Schema(title = "开始页", example = "")
    private Integer startPage;

    @Schema(title = "结束页", example = "")
    private Integer endPage;

    @Schema(title = "同步全部", example = "")
    private Boolean syncAll;

    @Schema(title = "解析类型", example = "LIST/OBJECT")
    private String jsonDataType;

    @Schema(title = "节点根目录jsonPath", example = "$..")
    private String nodeRootJsonPath;

    @Schema(title = "来源表信息", example = "[{\"code\":\"installed_rank\",\"type\":\"String\",\"sql\":\"\"}]")
    private List<SyncColumnInfo> tableColumn;
}
