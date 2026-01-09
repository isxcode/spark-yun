package com.isxcode.spark.api.work.dto.etl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EtlNode {

    /**
     * 前端节点id标识，用于映射关系.
     */
    private String id;

    /**
     * etl节点名称.
     */
    private String name;

    /**
     * etl节点类型.
     */
    private String type;

    /**
     * etl节点别名编码.
     */
    private String aliaCode;

    /**
     * 关联配置的主表别名编码.
     */
    private String mainAliaCode;

    /**
     * etl节点备注.
     */
    private String remark;

    /**
     * 节点输出的字段结构，输出节点的返回结构体.
     */
    private List<EtlColumn> outColumnList;

    /**
     * 新增字段节点.
     */
    private AddColEtl addColEtl;

    /**
     * 自定义Sql节点.
     */
    private CustomSqlEtl customSqlEtl;

    /**
     * 过滤数据节点.
     */
    private List<FilterEtl> filterEtl;

    /**
     * 输入数据节点.
     */
    private InputEtl inputEtl;

    /**
     * 关联数据节点.
     */
    private List<JoinEtl> joinEtl;

    /**
     * 输出数据节点.
     */
    private OutputEtl outputEtl;

    /**
     * 转换数据节点.
     */
    private List<TransformEtl> transformEtl;

    /**
     * 合并数据节点.
     */
    private List<UnionEtl> unionEtl;
}
