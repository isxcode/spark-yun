package com.isxcode.spark.api.work.dto;

import com.isxcode.spark.api.work.dto.etl.EtlNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SparkEtlConfig {

    /**
     * 留给前端保存样式.
     */
    private String webConfig;

    /**
     * 保存所有的节点信息.
     */
    private List<EtlNode> nodeList;

    /**
     * 保存所有的节点映射关系.
     */
    private List<Map<String, String>> nodeMapping;
}

