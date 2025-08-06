package com.isxcode.spark.api.user.res;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetAnonymousTokenRes {

    private String token;
}
