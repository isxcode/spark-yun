package com.isxcode.star.api.agent.pojos.sup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DataSource {
    private String url;
    private String user;
    private String password;
    private String tableName;

}
