package com.isxcode.spark.api.meta.ao;

import lombok.Data;

@Data
public class MetaColumnLinageAo {

    private String lineageId;

    private String fromDbId;

    private String fromTableName;

    private String fromColumnName;

    private String toDbId;

    private String toTableName;

    private String toColumnName;

    private String dbType;

    private String dbName;

    private String dbRemark;

    private String remark;

    private String workVersionId;

    private String workId;

    private String workName;

    private String workType;

    public MetaColumnLinageAo(String lineageId, String fromDbId, String fromTableName, String fromColumnName,
        String toDbId, String toTableName, String toColumnName, String dbType, String dbName, String dbRemark,
        String remark, String workVersionId, String workId, String workName, String workType) {

        this.lineageId = lineageId;
        this.fromDbId = fromDbId;
        this.fromTableName = fromTableName;
        this.fromColumnName = fromColumnName;
        this.toDbId = toDbId;
        this.toTableName = toTableName;
        this.toColumnName = toColumnName;
        this.dbType = dbType;
        this.dbName = dbName;
        this.dbRemark = dbRemark;
        this.remark = remark;
        this.workVersionId = workVersionId;
        this.workId = workId;
        this.workName = workName;
        this.workType = workType;
    }
}
