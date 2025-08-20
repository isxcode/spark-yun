package com.isxcode.spark.api.secret.res;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class GetSecretExpressionRes {

    private String secretExpression;
}
