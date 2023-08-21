package com.isxcode.star.modules.workflow.repository;

import com.isxcode.star.api.instance.pojos.ao.WfiWorkflowInstanceAo;
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

@Repository
@CacheConfig(cacheNames = {"sy_workflow"})
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

  @Query(
      value =
          "select "
              + "   new com.isxcode.star.api.instance.pojos.ao.WfiWorkflowInstanceAo("
              + "   W.id,"
              + "   WF.name,"
              + "   W.execStartDateTime,"
              + "   W.execEndDateTime,"
              + "   W.status,"
              + "   W.instanceType) "
              + "from WorkflowInstanceEntity W left join WorkflowEntity WF on W.flowId = WF.id "
              + " where WF.name LIKE %:keyword% AND W.tenantId=:tenantId order by W.createDateTime desc")
  Page<WfiWorkflowInstanceAo> pageWorkFlowInstances(
      @Param("tenantId") String tenantId,
      @Param("keyword") String searchKeyWord,
      Pageable pageable);
}
