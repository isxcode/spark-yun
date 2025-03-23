package com.isxcode.star.api.auth.res;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuerySsoAuthRes {

    private String name;

    private String invokeUrl;
}
