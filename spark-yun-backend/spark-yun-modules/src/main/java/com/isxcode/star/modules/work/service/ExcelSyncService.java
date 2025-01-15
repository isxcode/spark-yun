package com.isxcode.star.modules.work.service;


import cn.hutool.core.io.FileUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.isxcode.star.api.datasource.dto.ColumnMetaDto;
import com.isxcode.star.api.work.req.GetExcelColumnsReq;
import com.isxcode.star.api.work.req.GetExcelDataReq;
import com.isxcode.star.api.work.req.ParseExcelNameReq;
import com.isxcode.star.api.work.res.GetExcelColumnsRes;
import com.isxcode.star.api.work.res.GetExcelDataRes;
import com.isxcode.star.api.work.res.ParseExcelNameRes;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.backend.api.base.properties.IsxAppProperties;
import com.isxcode.star.common.utils.path.PathUtils;
import com.isxcode.star.modules.file.entity.FileEntity;
import com.isxcode.star.modules.file.service.FileService;
import com.isxcode.star.modules.work.sql.SqlFunctionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.isxcode.star.common.config.CommonConfig.TENANT_ID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExcelSyncService {

    private final FileService fileService;

    private final IsxAppProperties isxAppProperties;

    private final SqlFunctionService sqlFunctionService;

    public GetExcelColumnsRes getExcelColumns(GetExcelColumnsReq getExcelColumnsReq) {

        // 判断文件是否存在
        FileEntity file = fileService.getFile(getExcelColumnsReq.getFileId());

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
        if (read.size() == 1 && getExcelColumnsReq.isHasHeader()) {
            throw new IsxAppException("Excel为空");
        }
        if (read.get(0).size() != read.get(1).size()) {
            throw new IsxAppException("Excel表头和字段不齐");
        }

        List<ColumnMetaDto> columns = new ArrayList<>();

        List<Object> header = read.get(0);
        if (getExcelColumnsReq.isHasHeader()) {
            header.forEach(e -> columns.add(ColumnMetaDto.builder().type("String").name(String.valueOf(e)).build()));
        } else {
            for (int i = 0; i < header.size(); i++) {
                columns.add(ColumnMetaDto.builder().type("String").name("col" + i).build());
            }
        }

        return GetExcelColumnsRes.builder().columns(columns).build();
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

    public ParseExcelNameRes parseExcelName(ParseExcelNameReq parseExcelNameReq) {

        String fileName = sqlFunctionService.parseSqlFunction(parseExcelNameReq.getFilePattern());
        return ParseExcelNameRes.builder().fileName(fileName).build();
    }
}
