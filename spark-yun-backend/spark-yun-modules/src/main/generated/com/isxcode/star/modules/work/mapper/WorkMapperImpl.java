package com.isxcode.star.modules.work.mapper;

import com.isxcode.star.api.agent.pojos.res.YagGetStatusRes;
import com.isxcode.star.api.work.pojos.req.AddWorkReq;
import com.isxcode.star.api.work.pojos.req.ConfigWorkReq;
import com.isxcode.star.api.work.pojos.req.UpdateWorkReq;
import com.isxcode.star.api.work.pojos.res.GetWorkRes;
import com.isxcode.star.api.work.pojos.res.PageWorkRes;
import com.isxcode.star.api.work.pojos.res.RunWorkRes;
import com.isxcode.star.modules.work.entity.WorkConfigEntity;
import com.isxcode.star.modules.work.entity.WorkEntity;
import java.time.format.DateTimeFormatter;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-10-05T19:39:23+0800",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 1.8.0_271 (Oracle Corporation)"
)
@Component
public class WorkMapperImpl implements WorkMapper {

    private final DateTimeFormatter dateTimeFormatter_yyyy_MM_dd_HH_mm_ss_11333195168 = DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm:ss" );

    @Override
    public WorkEntity addWorkReqToWorkEntity(AddWorkReq addWorkReq) {
        if ( addWorkReq == null ) {
            return null;
        }

        WorkEntity workEntity = new WorkEntity();

        workEntity.setName( addWorkReq.getName() );
        workEntity.setWorkType( addWorkReq.getWorkType() );
        workEntity.setRemark( addWorkReq.getRemark() );
        workEntity.setWorkflowId( addWorkReq.getWorkflowId() );

        return workEntity;
    }

    @Override
    public WorkEntity updateWorkReqToWorkEntity(UpdateWorkReq wokUpdateWorkReq, WorkEntity workEntity) {
        if ( wokUpdateWorkReq == null && workEntity == null ) {
            return null;
        }

        WorkEntity workEntity1 = new WorkEntity();

        if ( wokUpdateWorkReq != null ) {
            workEntity1.setRemark( wokUpdateWorkReq.getRemark() );
            workEntity1.setName( wokUpdateWorkReq.getName() );
        }
        if ( workEntity != null ) {
            workEntity1.setId( workEntity.getId() );
            workEntity1.setWorkType( workEntity.getWorkType() );
            workEntity1.setStatus( workEntity.getStatus() );
            workEntity1.setConfigId( workEntity.getConfigId() );
            workEntity1.setWorkflowId( workEntity.getWorkflowId() );
            workEntity1.setVersionId( workEntity.getVersionId() );
            workEntity1.setTopIndex( workEntity.getTopIndex() );
            workEntity1.setCreateDateTime( workEntity.getCreateDateTime() );
            workEntity1.setLastModifiedDateTime( workEntity.getLastModifiedDateTime() );
            workEntity1.setCreateBy( workEntity.getCreateBy() );
            workEntity1.setLastModifiedBy( workEntity.getLastModifiedBy() );
            workEntity1.setVersionNumber( workEntity.getVersionNumber() );
            workEntity1.setDeleted( workEntity.getDeleted() );
            workEntity1.setTenantId( workEntity.getTenantId() );
        }

        return workEntity1;
    }

    @Override
    public WorkConfigEntity configWorkReqToWorkConfigEntity(ConfigWorkReq configWorkReq) {
        if ( configWorkReq == null ) {
            return null;
        }

        WorkConfigEntity workConfigEntity = new WorkConfigEntity();

        workConfigEntity.setClusterId( configWorkReq.getClusterId() );
        workConfigEntity.setDatasourceId( configWorkReq.getDatasourceId() );
        workConfigEntity.setSqlScript( configWorkReq.getSqlScript() );
        workConfigEntity.setSparkConfig( configWorkReq.getSparkConfig() );
        workConfigEntity.setCorn( configWorkReq.getCorn() );

        return workConfigEntity;
    }

    @Override
    public PageWorkRes workEntityToPageWorkRes(WorkEntity workEntity) {
        if ( workEntity == null ) {
            return null;
        }

        PageWorkRes pageWorkRes = new PageWorkRes();

        if ( workEntity.getCreateDateTime() != null ) {
            pageWorkRes.setCreateDateTime( dateTimeFormatter_yyyy_MM_dd_HH_mm_ss_11333195168.format( workEntity.getCreateDateTime() ) );
        }
        pageWorkRes.setRemark( workEntity.getRemark() );
        pageWorkRes.setName( workEntity.getName() );
        pageWorkRes.setId( workEntity.getId() );
        pageWorkRes.setWorkType( workEntity.getWorkType() );
        pageWorkRes.setStatus( workEntity.getStatus() );

        return pageWorkRes;
    }

    @Override
    public GetWorkRes workEntityAndWorkConfigEntityToGetWorkRes(WorkEntity workEntity, WorkConfigEntity workConfigEntity) {
        if ( workEntity == null && workConfigEntity == null ) {
            return null;
        }

        GetWorkRes getWorkRes = new GetWorkRes();

        if ( workEntity != null ) {
            getWorkRes.setWorkflowId( workEntity.getWorkflowId() );
            getWorkRes.setWorkId( workEntity.getId() );
            getWorkRes.setName( workEntity.getName() );
            getWorkRes.setWorkType( workEntity.getWorkType() );
        }
        if ( workConfigEntity != null ) {
            getWorkRes.setClusterId( workConfigEntity.getClusterId() );
            getWorkRes.setDatasourceId( workConfigEntity.getDatasourceId() );
            getWorkRes.setSqlScript( workConfigEntity.getSqlScript() );
            getWorkRes.setCorn( workConfigEntity.getCorn() );
            getWorkRes.setSparkConfig( workConfigEntity.getSparkConfig() );
        }

        return getWorkRes;
    }

    @Override
    public RunWorkRes getStatusToRunWorkRes(YagGetStatusRes getStatusRes) {
        if ( getStatusRes == null ) {
            return null;
        }

        RunWorkRes.RunWorkResBuilder runWorkRes = RunWorkRes.builder();

        runWorkRes.appStatus( getStatusRes.getAppStatus() );
        runWorkRes.appId( getStatusRes.getAppId() );
        runWorkRes.trackingUrl( getStatusRes.getTrackingUrl() );

        return runWorkRes.build();
    }
}
