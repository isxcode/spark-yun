package com.isxcode.star.api.file.req;

import com.isxcode.star.backend.api.base.pojos.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PageFileReq extends BasePageRequest {

    @Schema(title = "资源文件类型", example = "JOB/LIB/FUNC")
    private String type;
}
