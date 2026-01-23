package com.isxcode.spark.api.work.dto.etl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EtlSqlBody {

    private String aliaCode;

    /**
     * (select * from table left join b on table.age = b.age where age = 1) as aliaCode .
     */
    private String aliaSql;
}
