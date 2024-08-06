package com.isxcode.star.modules.work.service;


import cn.hutool.core.io.FileUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.isxcode.star.api.datasource.pojos.dto.ColumnMetaDto;
import com.isxcode.star.api.work.pojos.res.GetDataSourceColumnsRes;
import com.isxcode.star.api.work.pojos.res.GetDataSourceDataRes;
import com.isxcode.star.backend.api.base.properties.IsxAppProperties;
import com.isxcode.star.common.utils.path.PathUtils;
import com.isxcode.star.modules.file.entity.FileEntity;
import com.isxcode.star.modules.file.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.isxcode.star.common.config.CommonConfig.TENANT_ID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExcelSyncService {

    private final FileService fileService;

    private final IsxAppProperties isxAppProperties;

    public GetDataSourceColumnsRes getDataSourceColumns(String fileId, Boolean hasTable) {

        // 从资源文件中获取.xlsx文件
        // 读取第一列
        FileEntity file = fileService.getFile(fileId);
        String filePath = PathUtils.parseProjectPath(isxAppProperties.getResourcesPath()) + File.separator + "file"
            + File.separator + TENANT_ID.get() + File.separator + "sy_1820754340072329216.xlsx";

        ExcelReader reader = ExcelUtil.getReader(FileUtil.file(filePath));
        // List<Object> objects = reader.readRow(0);
        List<Map<String, Object>> readAll = reader.readAll();

        // CsvWriteConfig.defaultConfig().setFieldSeparator()

        // // 使用 CsvWriter 写入 CSV 文件
        // CsvWriter writer = CsvUtil.getWriter(FileUtil.file(csvFilePath), CHARSET_UTF_8);
        // for (Map<String, Object> row : readAll) {
        // writer.write(row.values().toArray(new String[0]));
        // }

        // 关闭资源
        // reader.close();
        // writer.close();

        List<Object> header = reader.readRow(0);

        int columnCount = header.size();

        List<ColumnMetaDto> columns = new ArrayList<>();
        if (hasTable) {
        } else {

        }

        return GetDataSourceColumnsRes.builder().columns(columns).build();
    }

    public GetDataSourceDataRes getDataSourceData(String getDataSourceDataReq) {

        // Optional<DatasourceEntity> datasourceEntityOptional =
        // datasourceRepository.findById(getDataSourceDataReq.getDataSourceId());
        // if (!datasourceEntityOptional.isPresent()) {
        // throw new IsxAppException("数据源异常，请联系开发者");
        // }
        //
        // Connection connection = datasourceService.getDbConnection(datasourceEntityOptional.get());
        //
        // Statement statement = connection.createStatement();
        // String dataPreviewSql =
        // syncWorkService.getDataPreviewSql(datasourceEntityOptional.get().getDbType(),
        // getDataSourceDataReq.getTableName());
        // ResultSet resultSet = statement.executeQuery(dataPreviewSql);
        // List<String> columns = new ArrayList<>();
        // List<List<String>> rows = new ArrayList<>();
        //
        // // 封装表头
        // int columnCount = resultSet.getMetaData().getColumnCount();
        // for (int i = 1; i <= columnCount; i++) {
        // columns.add(resultSet.getMetaData().getColumnName(i));
        // }
        //
        // // 封装数据
        // while (resultSet.next()) {
        // List<String> row = new ArrayList<>();
        // for (int i = 1; i <= columnCount; i++) {
        // row.add(String.valueOf(resultSet.getObject(i)));
        // }
        // rows.add(row);
        // }
        // statement.close();
        // connection.close();
        // return GetDataSourceDataRes.builder().columns(columns).rows(rows).build();
        return null;
    }

}
