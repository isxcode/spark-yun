package com.isxcode.star.modules.datasource.service;

import com.isxcode.star.api.datasource.constants.DatasourceDriver;
import com.isxcode.star.api.datasource.constants.DatasourceType;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.backend.api.base.properties.IsxAppProperties;
import com.isxcode.star.common.utils.AesUtils;
import com.isxcode.star.common.utils.path.PathUtils;
import com.isxcode.star.modules.datasource.entity.DatabaseDriverEntity;
import com.isxcode.star.modules.datasource.entity.DatasourceEntity;
import com.isxcode.star.modules.datasource.repository.DatasourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.isxcode.star.common.config.CommonConfig.JPA_TENANT_MODE;

@Service
@Slf4j
@RequiredArgsConstructor
public class DatasourceService {

	private final IsxAppProperties isxAppProperties;

	private final DatasourceRepository datasourceRepository;

	private final DatabaseDriverService dataDriverService;

	/**
	 * 所有的驱动. driverId driver
	 */
	public final static Map<String, DriverShim> ALL_EXIST_DRIVER = new ConcurrentHashMap<>();

	private final AesUtils aesUtils;

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

		// 判断驱动是否已经加载
		DriverShim driver = ALL_EXIST_DRIVER.get(datasource.getDriverId());
		if (driver == null) {

			JPA_TENANT_MODE.set(false);
			DatabaseDriverEntity driverEntity = dataDriverService.getDriver(datasource.getDriverId());
			JPA_TENANT_MODE.set(true);

			String driverPath = "TENANT_DRIVER".equals(driverEntity.getDriverType())
					? driverEntity.getTenantId() + File.separator + driverEntity.getFileName()
					: "system" + File.separator + driverEntity.getFileName();

			// 先加载驱动到ALL_EXIST_DRIVER
			try {
				URL url = new File(PathUtils.parseProjectPath(isxAppProperties.getResourcesPath()) + File.separator
						+ "jdbc" + File.separator + driverPath).toURI().toURL();
				ClassLoader driverClassLoader = new URLClassLoader(new URL[]{url});
				Class<?> driverClass = driverClassLoader.loadClass(getDriverClass(datasource.getDbType()));
				driver = new DriverShim((Driver) driverClass.newInstance());
				ALL_EXIST_DRIVER.put(datasource.getDriverId(), driver);
			} catch (MalformedURLException | ClassNotFoundException | IllegalAccessException
					| InstantiationException e) {
				throw new RuntimeException(e);
			}
		}

		java.util.Properties info = new java.util.Properties();
		if (datasource.getUsername() != null) {
			info.put("user", datasource.getUsername());
		}
		if (datasource.getPasswd() != null) {
			info.put("password", aesUtils.decrypt(datasource.getPasswd()));
		}
		DriverManager.setLoginTimeout(500);
		return driver.connect(datasource.getJdbcUrl(), info);
	}
}