package com.isxcode.star.modules.work.repository;

import com.isxcode.star.api.main.constants.ModuleCode;
import com.isxcode.star.modules.work.entity.WorkInstanceEntity;
import java.util.List;
import java.util.Map;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = {ModuleCode.WORK})
public interface WorkInstanceRepository extends JpaRepository<WorkInstanceEntity, String> {

    @Query(value = "select S.id as id,\n" + "       SW.name  as workName,\n" + "       SWF.name as workflowName,\n"
        + "       S.instanceType as instanceType,\n" + "       SW.workType as workType,\n"
        + "       S.status as status,\n" + "       S.planStartDateTime as planStartDateTime,\n"
        + "       S.duration as duration,\n" + "       S.execStartDateTime as execStartDateTime,\n"
        + "       S.execEndDateTime as execEndDateTime,\n" + "       S.nextPlanDateTime as nextPlanDateTime\n"
        + "from WorkInstanceEntity S \n" + "         left join WorkEntity SW on S.workId = SW.id\n"
        + "         left join WorkflowEntity SWF on SW.workflowId = SWF.id \n"
        + "WHERE (:executeStatus is null or :executeStatus = '' or S.status = :executeStatus) AND S.tenantId=:tenantId and "
        + "(S.id LIKE %:keyword% " + "OR SW.name LIKE %:keyword% " + "OR SWF.name LIKE %:keyword% ) "
        + "order by S.lastModifiedDateTime desc ")
    Page<Map> searchAll(@Param("tenantId") String tenantId, @Param("keyword") String searchKeyWord,
        @Param("executeStatus") String executeStatus, Pageable pageable);

    WorkInstanceEntity findByWorkIdAndWorkflowInstanceId(String workId, String workflowInstanceId);

    @Query("SELECT W FROM WorkInstanceEntity W " + "WHERE W.workId IN (:workIds) "
        + "AND W.workflowInstanceId = :workflowInstanceId ")
    List<WorkInstanceEntity> findAllByWorkIdAndWorkflowInstanceId(List<String> workIds, String workflowInstanceId);

    List<WorkInstanceEntity> findAllByWorkflowInstanceId(String workflowInstanceId);

    List<WorkInstanceEntity> findAllByWorkflowInstanceIdAndStatus(String workflowInstanceId, String status);

    @Query("select W from WorkInstanceEntity W where W.workflowInstanceId = :workflowInstanceId and W.workId in (:workIds)")
    List<WorkInstanceEntity> findAllByWorkflowInstanceIdAndWorkIds(String workflowInstanceId, List<String> workIds);

    void deleteByWorkflowInstanceId(String workflowInstanceId);
}
