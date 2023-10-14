package com.isxcode.star.modules.datasource.mapper;

import com.isxcode.star.api.datasource.pojos.req.AddDatasourceReq;
import com.isxcode.star.api.datasource.pojos.req.UpdateDatasourceReq;
import com.isxcode.star.api.datasource.pojos.res.PageDatasourceRes;
import com.isxcode.star.modules.datasource.entity.DatasourceEntity;
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
public class DatasourceMapperImpl implements DatasourceMapper {

    private final DateTimeFormatter dateTimeFormatter_yyyy_MM_dd_HH_mm_ss_11333195168 = DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm:ss" );

    @Override
    public DatasourceEntity dasAddDatasourceReqToDatasourceEntity(AddDatasourceReq dasAddDatasourceReq) {
        if ( dasAddDatasourceReq == null ) {
            return null;
        }

        DatasourceEntity datasourceEntity = new DatasourceEntity();

        datasourceEntity.setName( dasAddDatasourceReq.getName() );
        datasourceEntity.setRemark( dasAddDatasourceReq.getRemark() );
        datasourceEntity.setJdbcUrl( dasAddDatasourceReq.getJdbcUrl() );
        datasourceEntity.setDbType( dasAddDatasourceReq.getDbType() );
        datasourceEntity.setUsername( dasAddDatasourceReq.getUsername() );
        datasourceEntity.setPasswd( dasAddDatasourceReq.getPasswd() );

        return datasourceEntity;
    }

    @Override
    public DatasourceEntity dasUpdateDatasourceReqToDatasourceEntity(UpdateDatasourceReq dasUpdateDatasourceReq, DatasourceEntity datasourceEntity) {
        if ( dasUpdateDatasourceReq == null && datasourceEntity == null ) {
            return null;
        }

        DatasourceEntity datasourceEntity1 = new DatasourceEntity();

        if ( dasUpdateDatasourceReq != null ) {
            datasourceEntity1.setPasswd( dasUpdateDatasourceReq.getPasswd() );
            datasourceEntity1.setRemark( dasUpdateDatasourceReq.getRemark() );
            datasourceEntity1.setDbType( dasUpdateDatasourceReq.getDbType() );
            datasourceEntity1.setJdbcUrl( dasUpdateDatasourceReq.getJdbcUrl() );
            datasourceEntity1.setUsername( dasUpdateDatasourceReq.getUsername() );
            datasourceEntity1.setName( dasUpdateDatasourceReq.getName() );
        }
        if ( datasourceEntity != null ) {
            datasourceEntity1.setId( datasourceEntity.getId() );
            datasourceEntity1.setCheckDateTime( datasourceEntity.getCheckDateTime() );
            datasourceEntity1.setStatus( datasourceEntity.getStatus() );
            datasourceEntity1.setConnectLog( datasourceEntity.getConnectLog() );
            datasourceEntity1.setCreateDateTime( datasourceEntity.getCreateDateTime() );
            datasourceEntity1.setLastModifiedDateTime( datasourceEntity.getLastModifiedDateTime() );
            datasourceEntity1.setCreateBy( datasourceEntity.getCreateBy() );
            datasourceEntity1.setLastModifiedBy( datasourceEntity.getLastModifiedBy() );
            datasourceEntity1.setVersionNumber( datasourceEntity.getVersionNumber() );
            datasourceEntity1.setDeleted( datasourceEntity.getDeleted() );
            datasourceEntity1.setTenantId( datasourceEntity.getTenantId() );
        }

        return datasourceEntity1;
    }

    @Override
    public PageDatasourceRes datasourceEntityToQueryDatasourceRes(DatasourceEntity datasourceEntity) {
        if ( datasourceEntity == null ) {
            return null;
        }

        PageDatasourceRes pageDatasourceRes = new PageDatasourceRes();

        if ( datasourceEntity.getCheckDateTime() != null ) {
            pageDatasourceRes.setCheckDateTime( dateTimeFormatter_yyyy_MM_dd_HH_mm_ss_11333195168.format( datasourceEntity.getCheckDateTime() ) );
        }
        pageDatasourceRes.setName( datasourceEntity.getName() );
        pageDatasourceRes.setId( datasourceEntity.getId() );
        pageDatasourceRes.setJdbcUrl( datasourceEntity.getJdbcUrl() );
        pageDatasourceRes.setUsername( datasourceEntity.getUsername() );
        pageDatasourceRes.setRemark( datasourceEntity.getRemark() );
        pageDatasourceRes.setStatus( datasourceEntity.getStatus() );
        pageDatasourceRes.setDbType( datasourceEntity.getDbType() );
        pageDatasourceRes.setConnectLog( datasourceEntity.getConnectLog() );

        return pageDatasourceRes;
    }

    @Override
    public List<PageDatasourceRes> datasourceEntityToQueryDatasourceResList(List<DatasourceEntity> datasourceEntity) {
        if ( datasourceEntity == null ) {
            return null;
        }

        List<PageDatasourceRes> list = new ArrayList<PageDatasourceRes>( datasourceEntity.size() );
        for ( DatasourceEntity datasourceEntity1 : datasourceEntity ) {
            list.add( datasourceEntityToQueryDatasourceRes( datasourceEntity1 ) );
        }

        return list;
    }
}
