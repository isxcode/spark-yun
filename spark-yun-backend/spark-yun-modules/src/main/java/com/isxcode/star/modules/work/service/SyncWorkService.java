package com.isxcode.star.modules.work.service;

import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.common.connection.JDBCConnection;
import com.isxcode.star.common.utils.AesUtils;
import com.isxcode.star.modules.datasource.entity.DatasourceEntity;
import com.isxcode.star.modules.datasource.repository.DatasourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static com.isxcode.star.api.datasource.constants.DatasourceType.ORACLE;
import static com.isxcode.star.api.datasource.constants.DatasourceType.SQL_SERVER;

@Service
@RequiredArgsConstructor
@Slf4j
public class SyncWorkService {

	private final DatasourceRepository datasourceRepository;

	private final AesUtils aesUtils;

	/**
	 * 返回筛选后的表名。
	 * 
	 * @param metaData
	 *            数据库连接的元数据。
	 * @param catalog
	 *            数据库名。
	 * @param schema
	 *            模式名。
	 * @param tablePattern
	 *            表名模式，支持模糊匹配。
	 * @return 筛选后的数据库表名。
	 */
	public List<String> tables(DatabaseMetaData metaData, String catalog, String schema, String tablePattern)
			throws SQLException {
		List<String> list = new ArrayList<>();
		if (StringUtils.isBlank(tablePattern)) {
			tablePattern = "%";
		} else {
			tablePattern = "%" + tablePattern + "%";
		}
		ResultSet tables = metaData.getTables(catalog, schema, tablePattern, new String[]{"TABLE"});
		while (tables.next()) {
			list.add(tables.getString("TABLE_NAME"));
		}
		return list;
	}

	/**
	 * 返回筛选后的视图名。
	 * 
	 * @param metaData
	 *            数据库连接的元数据。
	 * @param catalog
	 *            数据库名。
	 * @param schema
	 *            模式名。
	 * @return 筛选后的数据库视图名。
	 */
	public List<String> views(DatabaseMetaData metaData, String catalog, String schema, String tablePattern)
			throws SQLException {
		List<String> list = new ArrayList<>();
		if (StringUtils.isBlank(tablePattern)) {
			tablePattern = "%";
		} else {
			tablePattern = "%" + tablePattern + "%";
		}
		ResultSet views = metaData.getTables(catalog, schema, tablePattern, new String[]{"VIEW"});
		while (views.next()) {
			list.add(views.getString("TABLE_NAME"));
		}
		return list;
	}

	/**
	 * 返回筛选后的数据表字段信息。
	 * 
	 * @param metaData
	 *            数据库连接的元数据。
	 * @param catalog
	 *            数据库名。
	 * @param schema
	 *            模式名。
	 * @param table
	 *            表名。
	 * @return 筛选后的数据表字段信息。
	 */
	public List<List<String>> columns(DatabaseMetaData metaData, String catalog, String schema, String table)
			throws SQLException {
		List<List<String>> list = new ArrayList<>();
		ResultSet columns = metaData.getColumns(catalog, schema, table, null);
		while (columns.next()) {
			List<String> cList = new ArrayList<>();
			cList.add(columns.getString("COLUMN_NAME"));
			String type = columns.getString("TYPE_NAME");
			if ("VARCHAR".equals(type)) {
				type += "(" + columns.getInt("COLUMN_SIZE") + ")";
			}
			cList.add(type);
			list.add(cList);
		}
		return list;
	}

	/**
	 * 获取处理后的catalog与schema。
	 * 
	 * @param dataBase
	 *            传入的数据库名称。
	 * @param catalog
	 *            数据库名。
	 * @param schema
	 *            模式名。
	 * @return 按规则判断数据源类型 得到catalog与schema的值 返回map。
	 */
	public Map<String, String> transform(String dataBase, String catalog, String schema) {
		Map<String, String> result = new HashMap<>();

		if (dataBase != null) {
			if (catalog == null && schema == null) {
				// 未支持的数据库，全量查询
				return result;
			} else if (schema != null) {
				schema = dataBase;
			} else {
				catalog = dataBase;
			}
		}

		result.put("catalog", catalog);
		result.put("schema", schema);

		return result;
	}

	/**
	 * 获取数据库连接。
	 * 
	 * @param dataSourceId
	 *            数据源唯一id。
	 * @param driver
	 *            数据库驱动文件路径。
	 * @param classPath
	 *            数据库驱动类名。
	 * @return 数据库连接。
	 */
	public Connection getConnection(String dataSourceId, String driver, String classPath) throws Exception {
		Optional<DatasourceEntity> datasourceEntityOptional = datasourceRepository.findById(dataSourceId);
		if (!datasourceEntityOptional.isPresent()) {
			throw new IsxAppException("数据源异常，请联系开发者");
		}

		return JDBCConnection.getConnection(datasourceEntityOptional.get().getJdbcUrl(),
				datasourceEntityOptional.get().getUsername(),
				aesUtils.decrypt(datasourceEntityOptional.get().getPasswd()), driver, classPath);
	}

	/**
	 * 获取数据预览SQL。
	 * 
	 * @param dataType
	 *            数据库类型。
	 * @param tableName
	 *            数据库表名。
	 * @return 数据预览SQL。
	 */
	public String getDataPreviewSql(String dataType, String tableName) {
		switch (dataType) {
			case ORACLE :
				return "SELECT * FROM " + tableName + " WHERE ROWNUM <= 50";
			case SQL_SERVER :
				return "SELECT TOP 50 * FROM " + tableName;
			default :
				return "SELECT * FROM " + tableName + " LIMIT 50";
		}
	}
}
