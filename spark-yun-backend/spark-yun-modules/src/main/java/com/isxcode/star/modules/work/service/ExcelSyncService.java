package com.isxcode.star.modules.work.service;


import cn.hutool.core.io.FileUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.isxcode.star.api.datasource.pojos.dto.ColumnMetaDto;
import com.isxcode.star.api.work.pojos.req.GetExcelDataReq;
import com.isxcode.star.api.work.pojos.res.GetDataSourceColumnsRes;
import com.isxcode.star.api.work.pojos.res.GetExcelDataRes;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.backend.api.base.properties.IsxAppProperties;
import com.isxcode.star.common.utils.path.PathUtils;
import com.isxcode.star.modules.file.entity.FileEntity;
import com.isxcode.star.modules.file.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
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

    public GetExcelDataRes getExcelData(GetExcelDataReq getExcelDataReq) {

        // 判断文件是否存在
        FileEntity file = fileService.getFile(getExcelDataReq.getFileId());

        // 获取文件的绝对路径
        String filePath = PathUtils.parseProjectPath(isxAppProperties.getResourcesPath()) + File.separator + "file"
            + File.separator + TENANT_ID.get() + File.separator + file.getId();

        // 读取文件数据
        ExcelReader reader = ExcelUtil.getReader(FileUtil.file(filePath));
        List<List<Object>> read = reader.read();

        // 校验excel合法性
        if (read.isEmpty()) {
            throw new IsxAppException("Excel为空");
        }
        if (read.size() == 1 && getExcelDataReq.isHasHeader()) {
            throw new IsxAppException("Excel为空");
        }
        if (read.get(0).size() != read.get(1).size()) {
            throw new IsxAppException("Excel表头和字段不齐");
        }

        // 解析表头
        List<String> columns = new ArrayList<>();
        List<Object> header = read.get(0);
        if (getExcelDataReq.isHasHeader()) {
            header.forEach(e -> columns.add(String.valueOf(e)));
        } else {
            for (int i = 0; i < header.size(); i++) {
                columns.add("col" + i);
            }
        }

        // 只展示200条
        Iterator<List<Object>> iterator = read.iterator();
        List<List<String>> rows = new ArrayList<>();
        while (iterator.hasNext() && rows.size() < 200) {
            List<Object> next = iterator.next();
            List<String> row = new ArrayList<>();
            next.forEach(e -> row.add(String.valueOf(e)));
            rows.add(row);
        }

        // 如果有表头删除第一行
        if (getExcelDataReq.isHasHeader()) {
            rows.remove(0);
        }

        reader.close();
        return GetExcelDataRes.builder().columns(columns).rows(rows).build();
    }

}
