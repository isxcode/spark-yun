package com.isxcode.star.modules.workflow.repository;

import com.isxcode.star.api.instance.pojos.ao.WorkflowInstanceAo;
import com.isxcode.star.api.main.constants.ModuleVipCode;
import com.isxcode.star.api.monitor.pojos.ao.WorkflowMonitorAo;
import com.isxcode.star.modules.workflow.entity.WorkflowInstanceEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
@CacheConfig(cacheNames = {ModuleVipCode.VIP_WORKFLOW_INSTANCE})
public interface WorkflowInstanceRepository extends JpaRepository<WorkflowInstanceEntity, String> {

    @CachePut(key = "#workflowInstanceId")
    default String setWorkflowLog(String workflowInstanceId, String runLog) {

        return runLog;
    }

    @Cacheable(key = "#workflowInstanceId")
    default String getWorkflowLog(String workflowInstanceId) {

        return "";
    }

    @CacheEvict(key = "#workflowInstanceId")
    default void deleteWorkflowLog(String workflowInstanceId) {}

    @Query(value = "select " + "   new com.isxcode.star.api.instance.pojos.ao.WorkflowInstanceAo(" + "   W.id,"
        + "   WF.name," + "   W.duration," + "   W.nextPlanDateTime," + "   W.planStartDateTime,"
        + "   W.execStartDateTime," + "   W.execEndDateTime," + "   W.status," + "   W.instanceType) "
        + "from WorkflowInstanceEntity W left join WorkflowEntity WF on W.flowId = WF.id "
        + " where ( WF.name LIKE %:keyword% OR W.id LIKE %:keyword% ) AND (:executeStatus is null or :executeStatus ='' or W.status=:executeStatus ) AND (:workflowId is null or :workflowId ='' or W.flowId=:workflowId ) AND W.tenantId=:tenantId order by W.lastModifiedDateTime desc")
    Page<WorkflowInstanceAo> pageWorkFlowInstances(@Param("tenantId") String tenantId,
        @Param("keyword") String searchKeyWord, @Param("executeStatus") String executeStatus,
        @Param("workflowId") String workflowId, Pageable pageable);

    @Query("SELECT new com.isxcode.star.api.monitor.pojos.ao.WorkflowMonitorAo( W.id,W1.name,W.duration,W.execStartDateTime,W.execEndDateTime,W.status,U.username ) from WorkflowInstanceEntity W left join WorkflowEntity W1 on W.flowId = W1.id left join UserEntity U on W.lastModifiedBy = U.id where W1.name like %:keyword% and W.tenantId=:tenantId order by W.lastModifiedDateTime desc")
    Page<WorkflowMonitorAo> searchWorkflowMonitor(@Param("tenantId") String tenantId,
        @Param("keyword") String searchKeyWord, Pageable pageable);

    List<WorkflowInstanceEntity> findAllByExecStartDateTimeAfterAndLastModifiedDateTimeBefore(Date execStartDateTime,
        LocalDateTime execEndDateTime);
}
