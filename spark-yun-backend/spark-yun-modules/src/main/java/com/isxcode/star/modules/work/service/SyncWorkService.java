package com.isxcode.star.modules.work.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SyncWorkService {

  public List<String> tables(DatabaseMetaData metaData, String catalog, String schema) throws SQLException {
    List<String> list = new ArrayList<>();
    ResultSet tables = metaData.getTables( catalog,schema, null
      , new String[]{"TABLE"});
    while (tables.next()){
      list.add(tables.getString("TABLE_NAME"));
    }
    return list;
  }

  public List<String> views(DatabaseMetaData metaData, String catalog, String schema) throws SQLException {
    List<String> list = new ArrayList<>();
    ResultSet views = metaData.getTables(catalog,schema, null
      , new String[]{"VIEW"});
    while (views.next()){
      list.add(views.getString("TABLE_NAME"));
    }
    return list;
  }

  public List<List<String>> columns(DatabaseMetaData metaData, String catalog, String schema, String table) throws SQLException {
    List<List<String>> list = new ArrayList<>();
    ResultSet columns = metaData.getColumns(catalog,schema, table
      , null);
    while (columns.next()){
      List<String> cList = new ArrayList<>();
      cList.add(columns.getString("COLUMN_NAME"));
      cList.add(columns.getString("TYPE_NAME"));
      //cList.add(columns.getInt("COLUMN_SIZE"));
      list.add(cList);
    }
    return list;
  }

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
}
