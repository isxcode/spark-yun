package com.isxcode.star.modules.workflow.mapper;

import com.isxcode.star.api.workflow.pojos.dto.WorkInstanceInfo;
import com.isxcode.star.api.workflow.pojos.req.AddWorkflowReq;
import com.isxcode.star.api.workflow.pojos.req.UpdateWorkflowReq;
import com.isxcode.star.api.workflow.pojos.res.PageWorkflowRes;
import com.isxcode.star.modules.work.entity.WorkInstanceEntity;
import com.isxcode.star.modules.workflow.entity.WorkflowEntity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-10-05T19:39:23+0800",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 1.8.0_271 (Oracle Corporation)"
)
@Component
public class WorkflowMapperImpl implements WorkflowMapper {

    @Override
    public WorkflowEntity addWorkflowReqToWorkflowEntity(AddWorkflowReq addWorkflowReq) {
        if ( addWorkflowReq == null ) {
            return null;
        }

        WorkflowEntity workflowEntity = new WorkflowEntity();

        workflowEntity.setName( addWorkflowReq.getName() );
        workflowEntity.setRemark( addWorkflowReq.getRemark() );

        return workflowEntity;
    }

    @Override
    public WorkflowEntity updateWorkflowReqToWorkflowEntity(UpdateWorkflowReq wofUpdateWorkflowReq, WorkflowEntity workflowEntity) {
        if ( wofUpdateWorkflowReq == null && workflowEntity == null ) {
            return null;
        }

        WorkflowEntity workflowEntity1 = new WorkflowEntity();

        if ( wofUpdateWorkflowReq != null ) {
            workflowEntity1.setName( wofUpdateWorkflowReq.getName() );
            workflowEntity1.setRemark( wofUpdateWorkflowReq.getRemark() );
        }
        if ( workflowEntity != null ) {
            workflowEntity1.setId( workflowEntity.getId() );
            workflowEntity1.setType( workflowEntity.getType() );
            workflowEntity1.setStatus( workflowEntity.getStatus() );
            workflowEntity1.setConfigId( workflowEntity.getConfigId() );
            workflowEntity1.setVersionId( workflowEntity.getVersionId() );
            workflowEntity1.setCreateDateTime( workflowEntity.getCreateDateTime() );
            workflowEntity1.setLastModifiedDateTime( workflowEntity.getLastModifiedDateTime() );
            workflowEntity1.setCreateBy( workflowEntity.getCreateBy() );
            workflowEntity1.setLastModifiedBy( workflowEntity.getLastModifiedBy() );
            workflowEntity1.setVersionNumber( workflowEntity.getVersionNumber() );
            workflowEntity1.setDeleted( workflowEntity.getDeleted() );
            workflowEntity1.setTenantId( workflowEntity.getTenantId() );
        }

        return workflowEntity1;
    }

    @Override
    public PageWorkflowRes workflowEntityToQueryWorkflowRes(WorkflowEntity workflowEntity) {
        if ( workflowEntity == null ) {
            return null;
        }

        PageWorkflowRes pageWorkflowRes = new PageWorkflowRes();

        pageWorkflowRes.setId( workflowEntity.getId() );
        pageWorkflowRes.setName( workflowEntity.getName() );
        pageWorkflowRes.setRemark( workflowEntity.getRemark() );
        pageWorkflowRes.setStatus( workflowEntity.getStatus() );

        return pageWorkflowRes;
    }

    @Override
    public List<PageWorkflowRes> workflowEntityListToQueryWorkflowResList(List<WorkflowEntity> workflowEntities) {
        if ( workflowEntities == null ) {
            return null;
        }

        List<PageWorkflowRes> list = new ArrayList<PageWorkflowRes>( workflowEntities.size() );
        for ( WorkflowEntity workflowEntity : workflowEntities ) {
            list.add( workflowEntityToQueryWorkflowRes( workflowEntity ) );
        }

        return list;
    }

    @Override
    public WorkInstanceInfo workInstanceEntityToWorkInstanceInfo(WorkInstanceEntity workInstances) {
        if ( workInstances == null ) {
            return null;
        }

        WorkInstanceInfo workInstanceInfo = new WorkInstanceInfo();

        workInstanceInfo.setRunStatus( workInstances.getStatus() );
        workInstanceInfo.setWorkInstanceId( workInstances.getId() );
        workInstanceInfo.setWorkId( workInstances.getWorkId() );

        return workInstanceInfo;
    }

    @Override
    public List<WorkInstanceInfo> workInstanceEntityListToWorkInstanceInfoList(List<WorkInstanceEntity> workInstances) {
        if ( workInstances == null ) {
            return null;
        }

        List<WorkInstanceInfo> list = new ArrayList<WorkInstanceInfo>( workInstances.size() );
        for ( WorkInstanceEntity workInstanceEntity : workInstances ) {
            list.add( workInstanceEntityToWorkInstanceInfo( workInstanceEntity ) );
        }

        return list;
    }
}
