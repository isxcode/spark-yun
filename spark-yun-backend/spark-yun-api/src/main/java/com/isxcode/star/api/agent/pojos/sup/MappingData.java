package com.isxcode.star.api.agent.pojos.sup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MappingData {
    private List<HashMap<String, String>> sourceTableData;

    private List<HashMap<String, String>> targetTableData;

    private List<HashMap<String, String>> connect;

}
