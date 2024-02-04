package com.isxcode.star.modules.datasource.service;

import com.isxcode.star.api.datasource.constants.ColumnType;
import com.isxcode.star.api.datasource.constants.DatasourceDriver;
import com.isxcode.star.api.datasource.constants.DatasourceType;
import com.isxcode.star.api.datasource.pojos.dto.SecurityColumnDto;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.backend.api.base.properties.IsxAppProperties;
import com.isxcode.star.common.utils.AesUtils;
import com.isxcode.star.common.utils.path.PathUtils;
import com.isxcode.star.modules.datasource.entity.DatabaseDriverEntity;
import com.isxcode.star.modules.datasource.entity.DatasourceEntity;
import com.isxcode.star.modules.datasource.repository.DatasourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.calcite.sql.parser.SqlParseException;
import org.springframework.stereotype.Service;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.SqlNode;

import java.io.File;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

				// 特殊逻辑判断，如果驱动是mysql5的使用
				String driverClassName = getDriverClass(datasource.getDbType());
				if (DatasourceType.MYSQL.equals(datasource.getDbType())) {
					if (driverPath.contains("-5")) {
						driverClassName = "com.mysql.jdbc.Driver";
					}
				}

				Class<?> driverClass = driverClassLoader.loadClass(driverClassName);
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

	public void executeSql(DatasourceEntity datasource, String sql) {

		try (Connection connection = this.getDbConnection(datasource);
				Statement statement = connection.createStatement()) {
			statement.execute(sql);
		} catch (SQLException e) {
			log.error(e.getMessage());
			if (e.getErrorCode() == 1364) {
				throw new IsxAppException("表不存在");
			}
			throw new IsxAppException("提交失败");
		}
	}

	public void securityExecuteSql(String datasourceId, String securityExecuteSql,
			List<SecurityColumnDto> securityColumns) {

		DatasourceEntity datasource = this.getDatasource(datasourceId);

		try (Connection connection = this.getDbConnection(datasource);
				PreparedStatement statement = connection.prepareStatement(securityExecuteSql);) {
			for (int i = 0; i < securityColumns.size(); i++) {
				this.transAndSetParameter(statement, securityColumns.get(i), i);
			}
			statement.execute();
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new IsxAppException("提交失败");
		}
	}

	public ResultSet securityQuerySql(String datasourceId, String securityExecuteSql,
			List<SecurityColumnDto> securityColumns) throws SQLException {

		DatasourceEntity datasource = this.getDatasource(datasourceId);

		Connection connection = this.getDbConnection(datasource);
		PreparedStatement statement = connection.prepareStatement(securityExecuteSql);
		for (int i = 0; i < securityColumns.size(); i++) {
			this.transAndSetParameter(statement, securityColumns.get(i), i);
		}
		return statement.executeQuery();
	}

	public long securityGetTableCount(String datasourceId, String securityExecuteSql,
			List<SecurityColumnDto> securityColumns) {

		DatasourceEntity datasource = this.getDatasource(datasourceId);

		try (Connection connection = this.getDbConnection(datasource);
				PreparedStatement statement = connection.prepareStatement(securityExecuteSql);) {
			for (int i = 0; i < securityColumns.size(); i++) {
				this.transAndSetParameter(statement, securityColumns.get(i), i);
			}
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				return resultSet.getLong(1);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new IsxAppException("提交失败");
		}
		throw new IsxAppException("查询总条数异常");
	}

	public boolean tableIsExist(DatasourceEntity datasource, String tableName) {

		try (Connection connection = this.getDbConnection(datasource);
				PreparedStatement preparedStatement = connection
						.prepareStatement("SELECT 1 FROM " + tableName + " WHERE 1 = 0")) {
			preparedStatement.executeQuery();
			return true;
		} catch (SQLException e) {
			log.error(e.getMessage());
			return false;
		}
	}

	public void transAndSetParameter(PreparedStatement statement, SecurityColumnDto securityColumnDto,
			int parameterIndex) throws SQLException {

		switch (securityColumnDto.getType()) {
			case ColumnType.STRING :
				if (securityColumnDto.getValue() == null) {
					statement.setNull(parameterIndex + 1, java.sql.Types.VARCHAR);
				} else {
					statement.setString(parameterIndex + 1, String.valueOf(securityColumnDto.getValue()));
				}
				break;
			case ColumnType.INT :
				if (securityColumnDto.getValue() == null) {
					statement.setNull(parameterIndex + 1, Types.INTEGER);
				} else {
					statement.setInt(parameterIndex + 1,
							Integer.parseInt(String.valueOf(securityColumnDto.getValue())));
				}
				break;
			case ColumnType.DOUBLE :
				if (securityColumnDto.getValue() == null) {
					statement.setNull(parameterIndex + 1, Types.DOUBLE);

				} else {
					statement.setDouble(parameterIndex + 1,
							Double.parseDouble(String.valueOf(securityColumnDto.getValue())));
				}
				break;
			case ColumnType.TIMESTAMP :
			case ColumnType.DATE :
			case ColumnType.DATE_TIME :
				if (securityColumnDto.getValue() == null) {
					statement.setNull(parameterIndex + 1, Types.TIMESTAMP);
				} else {
					statement.setTimestamp(parameterIndex + 1,
							new Timestamp(Long.parseLong(String.valueOf(securityColumnDto.getValue()))));
				}
				break;
			case ColumnType.BIG_DECIMAL :
				if (securityColumnDto.getValue() == null) {
					statement.setNull(parameterIndex + 1, Types.NUMERIC);
				} else {
					statement.setBigDecimal(parameterIndex + 1,
							new BigDecimal(String.valueOf(securityColumnDto.getValue())));
				}
				break;
			case ColumnType.BOOLEAN :
				if (securityColumnDto.getValue() == null) {
					statement.setNull(parameterIndex + 1, Types.BOOLEAN);
				} else {
					statement.setBoolean(parameterIndex + 1,
							Boolean.parseBoolean(String.valueOf(securityColumnDto.getValue())));
				}
				break;
			default :
				throw new IsxAppException("字段类型不支持");
		}
	}

	public String parseDbName(String jdbcUrl) {
		Pattern pattern = Pattern.compile("jdbc:\\w+://\\S+/(\\w+)");
		Matcher matcher = pattern.matcher(jdbcUrl);
		if (matcher.find()) {
			return matcher.group(1);
		}
		throw new IsxAppException("数据源异常");
	}

	/**
	 * 解析sql select * from table where a = '${value1}' and b = ${value2} 获取sql中的参数顺序
	 * List [value1,value2]
	 */
	public List<SecurityColumnDto> transSecurityColumns(String sql) {

		List<SecurityColumnDto> securityColumnList = new ArrayList<>();

		// 使用正则截取${}中的字符
		String pattern = "'\\$\\{(?!UPDATE_COLUMN\\b)([^}]+)\\}'";
		Pattern regex = Pattern.compile(pattern);
		Matcher matcher = regex.matcher(sql);
		int columnIndex = 10;
		while (matcher.find()) {
			String name = matcher.group(1);
			securityColumnList.add(SecurityColumnDto.builder().name(columnIndex + "." + name).build());
			columnIndex++;
		}
		return securityColumnList;
	}

	/**
	 * 解析sql，将 select * from table where a = '${value1}' and b = ${value2} 转成 select
	 * * from table where a = ? and b = ?
	 */
	public String transSecuritySql(String sql) {

		return sql.replaceAll("'\\$\\{(?!UPDATE_COLUMN\\b)([^}]+)\\}'", "?");
	}

	public boolean isQueryStatement(String sql) {

		SqlParser parser = SqlParser.create(sql);
		try {
			SqlNode sqlNode = parser.parseStmt();
			return sqlNode.getKind() == SqlKind.SELECT;
		} catch (SqlParseException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean checkSqlValid(String sql) {

		SqlParser parser = SqlParser.create(sql);
		try {
			parser.parseQuery(sql);
			return true;
		} catch (SqlParseException e) {
			return false;
		}
	}
}