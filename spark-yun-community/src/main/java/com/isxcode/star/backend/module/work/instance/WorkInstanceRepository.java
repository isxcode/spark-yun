package com.isxcode.star.backend.module.work.instance;

import java.util.Map;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = {"sy_work"})
public interface WorkInstanceRepository extends JpaRepository<WorkInstanceEntity, String> {

  @Query(
      value =
          ""
              + "select S.id as id,\n"
              + "       SW.name  as workName,\n"
              + "       SWF.name as workflowName,\n"
              + "       S.instanceType as instanceType,\n"
              + "       SW.workType as workType,\n"
              + "       S.status as status,\n"
              + "       S.planStartDateTime as planStartDateTime,\n"
              + "       S.execStartDateTime as execStartDateTime,\n"
              + "       S.execEndDateTime as execEndDateTime,\n"
              + "       S.nextPlanDateTime as nextPlanDateTime\n"
              + "from WorkInstanceEntity S \n"
              + "         left join WorkEntity SW on S.workId = SW.id\n"
              + "         left join WorkflowEntity SWF on SW.workflowId = SWF.id \n"
              + "WHERE S.tenantId=:tenantId and "
              + "(S.id LIKE %:keyword% "
              + "OR SW.name LIKE %:keyword% "
              + "OR SWF.name LIKE %:keyword% ) "
              + "order by S.createDateTime desc ")
  Page<Map> searchAll(
      @Param("tenantId") String tenantId,
      @Param("keyword") String searchKeyWord,
      Pageable pageable);
}
