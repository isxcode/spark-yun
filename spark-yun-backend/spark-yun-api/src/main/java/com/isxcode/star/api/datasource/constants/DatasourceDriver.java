package com.isxcode.star.api.datasource.constants;

/** 数据源驱动. */
public interface DatasourceDriver {

    String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";

    String ORACLE_DRIVER = "oracle.jdbc.driver.OracleDriver";

    String SQL_SERVER_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    String POSTGRE_SQL_DRIVER = "org.postgresql.Driver";

    String CLICKHOUSE_DRIVER = "com.clickhouse.jdbc.ClickHouseDriver";

    String HIVE_DRIVER = "org.apache.hive.jdbc.HiveDriver";

    String HANA_SAP_DRIVER = "com.sap.db.jdbc.Driver";

    String DM_DRIVER = "dm.jdbc.driver.DmDriver";

    String DORIS_DRIVER = "com.mysql.jdbc.Driver";

    String OCEAN_BASE_DRIVER = "com.oceanbase.jdbc.Driver";

    String TIDB_DRIVER = "com.mysql.cj.jdbc.Driver";

    String STAR_ROCKS_DRIVER = "com.mysql.jdbc.Driver";

    String DB2_DRIVER = "com.ibm.db2.jcc.DB2Driver";

    String H2_DRIVER = "org.h2.Driver";

    String GREENPLUM_DRIVER = "org.postgresql.Driver";

    String GBASE_DRIVER = "com.gbase.jdbc.Driver";

    String SYBASE_DRIVER = "com.sybase.jdbc4.jdbc.SybDriver";

    String GAUSS_DRIVER = "org.postgresql.Driver";

    String OPEN_GAUSS_DRIVER = "org.postgresql.Driver";
}
