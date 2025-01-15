package com.isxcode.star.api.view.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GetSqlDataRes {

    private List<String> columns;

    private List<List<String>> rows;
}
