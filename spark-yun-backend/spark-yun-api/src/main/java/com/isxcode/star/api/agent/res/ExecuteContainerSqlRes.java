package com.isxcode.star.api.agent.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExecuteContainerSqlRes {

    private List<List<String>> data;

    private String code;

    private String msg;
}
