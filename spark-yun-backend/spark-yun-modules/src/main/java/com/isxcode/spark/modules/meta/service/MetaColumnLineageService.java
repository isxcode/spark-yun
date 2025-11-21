package com.isxcode.spark.modules.meta.service;

import com.isxcode.spark.api.work.constants.WorkType;
import com.isxcode.spark.api.work.dto.SyncWorkConfig;
import com.isxcode.spark.modules.meta.entity.MetaColumnLineageEntity;
import com.isxcode.spark.modules.meta.repository.MetaColumnLineageRepository;
import com.isxcode.spark.modules.work.run.WorkRunContext;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.isxcode.spark.common.config.CommonConfig.TENANT_ID;
import static com.isxcode.spark.common.config.CommonConfig.USER_ID;

@Service
@RequiredArgsConstructor
public class MetaColumnLineageService {

    private final MetaColumnLineageRepository metaColumnLineageRepository;

    @Async("sparkYunWorkThreadPool")
    public void addMetaColumnLineage(WorkRunContext workRunContext) {

        // 目前只支持数据库同步
        if (!WorkType.DATA_SYNC_JDBC.equals(workRunContext.getWorkType())) {
            return;
        }

        // 异步环境
        TENANT_ID.set(workRunContext.getTenantId());
        USER_ID.set(workRunContext.getUserId());

        // 同步血缘
        SyncWorkConfig syncWorkConfig = workRunContext.getSyncWorkConfig();
        String fromDBId = syncWorkConfig.getSourceDBId();
        String fromTableName = syncWorkConfig.getSourceTable();
        String toDBId = syncWorkConfig.getTargetDBId();
        String toTableName = syncWorkConfig.getTargetTable();
        String workId = workRunContext.getWorkId();
        String workVersionId = workRunContext.getVersionId();

        // 把血缘这个作业中的关系都拉取出来
        List<MetaColumnLineageEntity> allWorkColumnLineageList = metaColumnLineageRepository.findAllByWorkId(workId);
        Map<String, MetaColumnLineageEntity> allWorkLineageMap =
            allWorkColumnLineageList.stream()
                .collect(
                    Collectors.toMap(
                        entity -> entity.getFromDbId() + entity.getFromTableName() + entity.getFromColumnName()
                            + entity.getToDbId() + entity.getToTableName() + entity.getToColumnName(),
                        entity -> entity));

        // 只继续新的血缘
        List<MetaColumnLineageEntity> newLineageList = new ArrayList<>();
        syncWorkConfig.getColumnMap().forEach(column -> {
            String fromColumnName = column.getSource();
            String toColumnName = column.getTarget();
            String code = fromDBId + fromTableName + fromColumnName + toDBId + toTableName + toColumnName;
            if (allWorkLineageMap.get(code) == null) {
                newLineageList.add(MetaColumnLineageEntity.builder().fromDbId(fromDBId).fromTableName(fromTableName)
                    .fromColumnName(fromColumnName).toDbId(toDBId).toTableName(toTableName).toColumnName(toColumnName)
                    .workId(workId).workVersionId(workVersionId).build());
            }
        });

        // 插入新的血缘
        metaColumnLineageRepository.saveAll(newLineageList);
    }
}
