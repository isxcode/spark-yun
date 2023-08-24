package com.isxcode.star.modules.datasource.service;

import com.isxcode.star.api.datasource.constants.DatasourceDriver;
import com.isxcode.star.api.datasource.constants.DatasourceType;
import com.isxcode.star.api.datasource.pojos.req.*;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.common.utils.AesUtils;
import com.isxcode.star.modules.datasource.entity.DatasourceEntity;
import com.isxcode.star.modules.datasource.repository.DatasourceRepository;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DatasourceService {

	private final DatasourceRepository datasourceRepository;

	private final AesUtils aesUtils;

	public void loadDriverClass(String datasourceType) {

		try {
			switch (datasourceType) {
				case DatasourceType.MYSQL :
					Class.forName(DatasourceDriver.MYSQL_DRIVER);
					break;
				case DatasourceType.ORACLE :
					Class.forName(DatasourceDriver.ORACLE_DRIVER);
					break;
				case DatasourceType.SQL_SERVER :
					Class.forName(DatasourceDriver.SQL_SERVER_DRIVER);
					break;
				case DatasourceType.DORIS :
					Class.forName(DatasourceDriver.DORIS_DRIVER);
					break;
				case DatasourceType.POSTGRE_SQL :
					Class.forName(DatasourceDriver.POSTGRE_SQL_DRIVER);
					break;
				case DatasourceType.CLICKHOUSE :
					Class.forName(DatasourceDriver.CLICKHOUSE_DRIVER);
					break;
				case DatasourceType.HANA_SAP :
					Class.forName(DatasourceDriver.HANA_SAP_DRIVER);
					break;
				case DatasourceType.HIVE :
					Class.forName(DatasourceDriver.HIVE_DRIVER);
					break;
				case DatasourceType.DM :
					Class.forName(DatasourceDriver.DM_DRIVER);
					break;
				case DatasourceType.OCEANBASE :
					Class.forName(DatasourceDriver.OCEAN_BASE_DRIVER);
					break;
				case DatasourceType.TIDB :
					Class.forName(DatasourceDriver.TIDB_DRIVER);
					break;
				case DatasourceType.DB2 :
					Class.forName(DatasourceDriver.DB2_DRIVER);
					break;
				case DatasourceType.STAR_ROCKS :
					Class.forName(DatasourceDriver.STAR_ROCKS_DRIVER);
					break;
				default :
					throw new IsxAppException("数据源暂不支持");
			}
		} catch (ClassNotFoundException e) {
			log.error(e.getMessage());
			throw new IsxAppException("找不到对应驱动");
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
