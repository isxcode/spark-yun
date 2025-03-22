package com.isxcode.star.api.auth.res;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.isxcode.star.backend.api.base.serializer.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PageSsoAuthRes {

    private String id;

    private String name;

    private String status;

    private String ssoType;

    private String clientId;

    private String scope;

    private String authUrl;

    private String accessTokenUrl;

    private String redirectUrl;

    private String userUrl;

    private String authJsonPath;

    private String remark;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createDateTime;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime lastModifiedDateTime;
}
