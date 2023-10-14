package com.isxcode.star.modules.tenant.mapper;

import com.isxcode.star.api.tenant.pojos.req.AddTenantReq;
import com.isxcode.star.api.tenant.pojos.req.UpdateTenantForSystemAdminReq;
import com.isxcode.star.api.tenant.pojos.req.UpdateTenantForTenantAdminReq;
import com.isxcode.star.api.tenant.pojos.res.PageTenantRes;
import com.isxcode.star.api.tenant.pojos.res.QueryUserTenantRes;
import com.isxcode.star.modules.tenant.entity.TenantEntity;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-10-05T19:39:24+0800",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 1.8.0_271 (Oracle Corporation)"
)
@Component
public class TenantMapperImpl implements TenantMapper {

    private final DateTimeFormatter dateTimeFormatter_yyyy_MM_dd_HH_mm_ss_11333195168 = DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm:ss" );

    @Override
    public TenantEntity tetAddTenantReqToTenantEntity(AddTenantReq tetAddTenantReq) {
        if ( tetAddTenantReq == null ) {
            return null;
        }

        TenantEntity tenantEntity = new TenantEntity();

        tenantEntity.setName( tetAddTenantReq.getName() );
        tenantEntity.setRemark( tetAddTenantReq.getRemark() );

        tenantEntity.setCheckDateTime( java.time.LocalDateTime.now() );
        tenantEntity.setStatus( "ENABLE" );
        tenantEntity.setMaxMemberNum( (long) 1L );
        tenantEntity.setUsedMemberNum( (long) 1L );
        tenantEntity.setMaxWorkflowNum( (long) 0L );
        tenantEntity.setUsedWorkflowNum( (long) 0L );

        return tenantEntity;
    }

    @Override
    public QueryUserTenantRes tenantEntityToTetQueryUserTenantRes(TenantEntity tenantEntity) {
        if ( tenantEntity == null ) {
            return null;
        }

        QueryUserTenantRes queryUserTenantRes = new QueryUserTenantRes();

        queryUserTenantRes.setId( tenantEntity.getId() );
        queryUserTenantRes.setName( tenantEntity.getName() );

        return queryUserTenantRes;
    }

    @Override
    public List<QueryUserTenantRes> tenantEntityToTetQueryUserTenantResList(List<TenantEntity> tenantEntities) {
        if ( tenantEntities == null ) {
            return null;
        }

        List<QueryUserTenantRes> list = new ArrayList<QueryUserTenantRes>( tenantEntities.size() );
        for ( TenantEntity tenantEntity : tenantEntities ) {
            list.add( tenantEntityToTetQueryUserTenantRes( tenantEntity ) );
        }

        return list;
    }

    @Override
    public TenantEntity tetUpdateTenantBySystemAdminReqToTenantEntity(UpdateTenantForSystemAdminReq tetUpdateTenantBySystemAdminReq, TenantEntity tenantEntity) {
        if ( tetUpdateTenantBySystemAdminReq == null && tenantEntity == null ) {
            return null;
        }

        TenantEntity tenantEntity1 = new TenantEntity();

        if ( tetUpdateTenantBySystemAdminReq != null ) {
            tenantEntity1.setRemark( tetUpdateTenantBySystemAdminReq.getRemark() );
            if ( tetUpdateTenantBySystemAdminReq.getMaxWorkflowNum() != null ) {
                tenantEntity1.setMaxWorkflowNum( tetUpdateTenantBySystemAdminReq.getMaxWorkflowNum().longValue() );
            }
            if ( tetUpdateTenantBySystemAdminReq.getMaxMemberNum() != null ) {
                tenantEntity1.setMaxMemberNum( tetUpdateTenantBySystemAdminReq.getMaxMemberNum().longValue() );
            }
            tenantEntity1.setName( tetUpdateTenantBySystemAdminReq.getName() );
        }
        if ( tenantEntity != null ) {
            tenantEntity1.setId( tenantEntity.getId() );
            tenantEntity1.setUsedMemberNum( tenantEntity.getUsedMemberNum() );
            tenantEntity1.setUsedWorkflowNum( tenantEntity.getUsedWorkflowNum() );
            tenantEntity1.setStatus( tenantEntity.getStatus() );
            tenantEntity1.setIntroduce( tenantEntity.getIntroduce() );
            tenantEntity1.setCheckDateTime( tenantEntity.getCheckDateTime() );
            tenantEntity1.setCreateDateTime( tenantEntity.getCreateDateTime() );
            tenantEntity1.setLastModifiedDateTime( tenantEntity.getLastModifiedDateTime() );
            tenantEntity1.setCreateBy( tenantEntity.getCreateBy() );
            tenantEntity1.setLastModifiedBy( tenantEntity.getLastModifiedBy() );
            tenantEntity1.setVersionNumber( tenantEntity.getVersionNumber() );
            tenantEntity1.setDeleted( tenantEntity.getDeleted() );
        }

        return tenantEntity1;
    }

    @Override
    public TenantEntity tetUpdateTenantByTenantAdminReqToTenantEntity(UpdateTenantForTenantAdminReq tetUpdateTenantByTenantAdminReq, TenantEntity tenantEntity) {
        if ( tetUpdateTenantByTenantAdminReq == null && tenantEntity == null ) {
            return null;
        }

        TenantEntity tenantEntity1 = new TenantEntity();

        if ( tetUpdateTenantByTenantAdminReq != null ) {
            tenantEntity1.setIntroduce( tetUpdateTenantByTenantAdminReq.getIntroduce() );
        }
        if ( tenantEntity != null ) {
            tenantEntity1.setId( tenantEntity.getId() );
            tenantEntity1.setName( tenantEntity.getName() );
            tenantEntity1.setUsedMemberNum( tenantEntity.getUsedMemberNum() );
            tenantEntity1.setMaxMemberNum( tenantEntity.getMaxMemberNum() );
            tenantEntity1.setUsedWorkflowNum( tenantEntity.getUsedWorkflowNum() );
            tenantEntity1.setMaxWorkflowNum( tenantEntity.getMaxWorkflowNum() );
            tenantEntity1.setStatus( tenantEntity.getStatus() );
            tenantEntity1.setRemark( tenantEntity.getRemark() );
            tenantEntity1.setCheckDateTime( tenantEntity.getCheckDateTime() );
            tenantEntity1.setCreateDateTime( tenantEntity.getCreateDateTime() );
            tenantEntity1.setLastModifiedDateTime( tenantEntity.getLastModifiedDateTime() );
            tenantEntity1.setCreateBy( tenantEntity.getCreateBy() );
            tenantEntity1.setLastModifiedBy( tenantEntity.getLastModifiedBy() );
            tenantEntity1.setVersionNumber( tenantEntity.getVersionNumber() );
            tenantEntity1.setDeleted( tenantEntity.getDeleted() );
        }

        return tenantEntity1;
    }

    @Override
    public PageTenantRes tenantEntityToTetQueryTenantRes(TenantEntity tenantEntity) {
        if ( tenantEntity == null ) {
            return null;
        }

        PageTenantRes pageTenantRes = new PageTenantRes();

        if ( tenantEntity.getCheckDateTime() != null ) {
            pageTenantRes.setCheckDateTime( dateTimeFormatter_yyyy_MM_dd_HH_mm_ss_11333195168.format( tenantEntity.getCheckDateTime() ) );
        }
        pageTenantRes.setId( tenantEntity.getId() );
        pageTenantRes.setName( tenantEntity.getName() );
        if ( tenantEntity.getUsedMemberNum() != null ) {
            pageTenantRes.setUsedMemberNum( String.valueOf( tenantEntity.getUsedMemberNum() ) );
        }
        if ( tenantEntity.getMaxMemberNum() != null ) {
            pageTenantRes.setMaxMemberNum( String.valueOf( tenantEntity.getMaxMemberNum() ) );
        }
        if ( tenantEntity.getUsedWorkflowNum() != null ) {
            pageTenantRes.setUsedWorkflowNum( String.valueOf( tenantEntity.getUsedWorkflowNum() ) );
        }
        if ( tenantEntity.getMaxWorkflowNum() != null ) {
            pageTenantRes.setMaxWorkflowNum( String.valueOf( tenantEntity.getMaxWorkflowNum() ) );
        }
        pageTenantRes.setRemark( tenantEntity.getRemark() );
        pageTenantRes.setStatus( tenantEntity.getStatus() );

        return pageTenantRes;
    }

    @Override
    public List<PageTenantRes> tenantEntityToTetQueryTenantResList(List<TenantEntity> tenantEntities) {
        if ( tenantEntities == null ) {
            return null;
        }

        List<PageTenantRes> list = new ArrayList<PageTenantRes>( tenantEntities.size() );
        for ( TenantEntity tenantEntity : tenantEntities ) {
            list.add( tenantEntityToTetQueryTenantRes( tenantEntity ) );
        }

        return list;
    }
}
