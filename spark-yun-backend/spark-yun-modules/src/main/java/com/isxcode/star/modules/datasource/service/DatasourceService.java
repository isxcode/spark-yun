package com.isxcode.star.modules.datasource.service;

import com.isxcode.star.api.datasource.constants.DatasourceDriver;
import com.isxcode.star.api.datasource.constants.DatasourceType;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.common.utils.AesUtils;
import com.isxcode.star.modules.datasource.entity.DatasourceEntity;
import com.isxcode.star.modules.datasource.repository.DatasourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Service
@Slf4j
@RequiredArgsConstructor
public class DatasourceService {

	private final DatasourceRepository datasourceRepository;

	private final AesUtils aesUtils;

	public void loadDriverClass(String datasourceType) {

		try {
			Class.forName(getDriverClass(datasourceType));
		} catch (ClassNotFoundException e) {
			log.error(e.getMessage());
			throw new IsxAppException("找不到对应驱动");
		}
	}

	public String getDriverClass(String datasourceType) {

		switch (datasourceType) {
			case DatasourceType.MYSQL :
				return DatasourceDriver.MYSQL_DRIVER;
			case DatasourceType.ORACLE :
				return DatasourceDriver.ORACLE_DRIVER;
			case DatasourceType.SQL_SERVER :
				return DatasourceDriver.SQL_SERVER_DRIVER;
			case DatasourceType.DORIS :
				return DatasourceDriver.DORIS_DRIVER;
			case DatasourceType.POSTGRE_SQL :
				return DatasourceDriver.POSTGRE_SQL_DRIVER;
			case DatasourceType.CLICKHOUSE :
				return DatasourceDriver.CLICKHOUSE_DRIVER;
			case DatasourceType.HANA_SAP :
				return DatasourceDriver.HANA_SAP_DRIVER;
			case DatasourceType.HIVE :
				return DatasourceDriver.HIVE_DRIVER;
			case DatasourceType.DM :
				return DatasourceDriver.DM_DRIVER;
			case DatasourceType.OCEANBASE :
				return DatasourceDriver.OCEAN_BASE_DRIVER;
			case DatasourceType.TIDB :
				return DatasourceDriver.TIDB_DRIVER;
			case DatasourceType.DB2 :
				return DatasourceDriver.DB2_DRIVER;
			case DatasourceType.STAR_ROCKS :
				return DatasourceDriver.STAR_ROCKS_DRIVER;
			default :
				throw new IsxAppException("数据源暂不支持");
		}
	}

	public DatasourceEntity getDatasource(String datasourceId) {

		return datasourceRepository.findById(datasourceId).orElseThrow(() -> new IsxAppException("数据源不存在"));
	}

	public Connection getDbConnection(DatasourceEntity datasource) throws SQLException {

		loadDriverClass(datasource.getDbType());

		DriverManager.setLoginTimeout(10);
		return DriverManager.getConnection(datasource.getJdbcUrl(), datasource.getUsername(),
				aesUtils.decrypt(datasource.getPasswd()));
	}
}
