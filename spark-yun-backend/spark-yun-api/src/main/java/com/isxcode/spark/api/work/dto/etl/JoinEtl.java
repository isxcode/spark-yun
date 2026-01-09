package com.isxcode.spark.api.work.dto.etl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JoinEtl {

    private String joinWay;

    private String joinAliaCode;

    private String joinType;

    private String joinLeftColumn;

    private String joinCondition;

    private String joinRightColumn;

    private String joinValue;

    private String joinSql;
}

