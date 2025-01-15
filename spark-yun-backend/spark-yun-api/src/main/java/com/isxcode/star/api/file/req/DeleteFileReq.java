package com.isxcode.star.api.file.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class DeleteFileReq {

    @Schema(title = "文件唯一id", example = "sy_48c4304593ea4897b6af999e48685896")
    @NotEmpty(message = "文件id不能为空")
    private String fileId;

}
