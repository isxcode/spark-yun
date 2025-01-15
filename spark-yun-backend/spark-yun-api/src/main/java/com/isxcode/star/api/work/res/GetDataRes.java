package com.isxcode.star.api.work.res;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetDataRes {

    private List<List<String>> data;

    private String jsonData;

    private String strData;
}
