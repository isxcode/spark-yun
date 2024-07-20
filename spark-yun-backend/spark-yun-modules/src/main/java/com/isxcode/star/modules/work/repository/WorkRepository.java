package com.isxcode.star.modules.work.repository;

import com.isxcode.star.api.main.constants.ModuleCode;
import com.isxcode.star.modules.work.entity.WorkEntity;
import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = {ModuleCode.WORK})
public interface WorkRepository extends JpaRepository<WorkEntity, String> {

    List<WorkEntity> findAllByWorkflowId(String workflowId);

    @Query("SELECT w FROM WorkEntity w " + "WHERE w.workflowId = :workflowId AND " + "(w.name LIKE %:searchKeyWord% "
        + "OR w.remark LIKE %:searchKeyWord% "
        + "OR w.workType LIKE %:searchKeyWord%) order by w.topIndex desc ,w.createDateTime desc")
    Page<WorkEntity> pageSearchByWorkflowId(@Param("searchKeyWord") String searchKeyWord,
        @Param("workflowId") String workflowId, Pageable pageable);

    @Query("select W from WorkEntity W where W.id in (:workIds)")
    List<WorkEntity> findAllByWorkIds(List<String> workIds);

    @Query("select max(W.topIndex) from WorkEntity W where W.workflowId = :workflowId")
    Integer findWorkflowMaxTopIndex(@Param("workflowId") String workflowId);

    Optional<WorkEntity> findByNameAndAndWorkflowId(String name, String workflowId);
}
