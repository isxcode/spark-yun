package com.isxcode.star.api.api.pojos.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JsonItemDto {

    private String jsonPath;

    private String type;

    private String express;
}
