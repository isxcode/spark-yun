package com.isxcode.spark.api.auth.res;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuerySsoAuthRes {

    private String name;

    private String invokeUrl;
}
