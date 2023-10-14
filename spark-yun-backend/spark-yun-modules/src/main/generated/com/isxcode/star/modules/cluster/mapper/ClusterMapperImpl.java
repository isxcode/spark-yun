package com.isxcode.star.modules.cluster.mapper;

import com.isxcode.star.api.cluster.pojos.req.AddClusterReq;
import com.isxcode.star.api.cluster.pojos.req.UpdateClusterReq;
import com.isxcode.star.api.cluster.pojos.res.PageClusterRes;
import com.isxcode.star.modules.cluster.entity.ClusterEntity;
import java.time.format.DateTimeFormatter;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-10-05T19:39:23+0800",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 1.8.0_271 (Oracle Corporation)"
)
@Component
public class ClusterMapperImpl implements ClusterMapper {

    private final DateTimeFormatter dateTimeFormatter_yyyy_MM_dd_HH_mm_ss_11333195168 = DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm:ss" );

    @Override
    public ClusterEntity addEngineReqToClusterEntity(AddClusterReq addClusterReq) {
        if ( addClusterReq == null ) {
            return null;
        }

        ClusterEntity clusterEntity = new ClusterEntity();

        clusterEntity.setName( addClusterReq.getName() );
        clusterEntity.setRemark( addClusterReq.getRemark() );
        clusterEntity.setClusterType( addClusterReq.getClusterType() );

        clusterEntity.setActiveNodeNum( 0 );
        clusterEntity.setAllNodeNum( 0 );
        clusterEntity.setUsedMemoryNum( 0.0 );
        clusterEntity.setAllMemoryNum( 0.0 );
        clusterEntity.setUsedStorageNum( 0.0 );
        clusterEntity.setAllStorageNum( 0.0 );
        clusterEntity.setCheckDateTime( java.time.LocalDateTime.now() );
        clusterEntity.setStatus( "NEW" );

        return clusterEntity;
    }

    @Override
    public ClusterEntity updateEngineReqToClusterEntity(UpdateClusterReq caeUpdateEngineReq, ClusterEntity calculateEngineEntity) {
        if ( caeUpdateEngineReq == null && calculateEngineEntity == null ) {
            return null;
        }

        ClusterEntity clusterEntity = new ClusterEntity();

        if ( caeUpdateEngineReq != null ) {
            clusterEntity.setName( caeUpdateEngineReq.getName() );
            clusterEntity.setRemark( caeUpdateEngineReq.getRemark() );
        }
        if ( calculateEngineEntity != null ) {
            clusterEntity.setId( calculateEngineEntity.getId() );
            clusterEntity.setStatus( calculateEngineEntity.getStatus() );
            clusterEntity.setCheckDateTime( calculateEngineEntity.getCheckDateTime() );
            clusterEntity.setAllNodeNum( calculateEngineEntity.getAllNodeNum() );
            clusterEntity.setActiveNodeNum( calculateEngineEntity.getActiveNodeNum() );
            clusterEntity.setAllMemoryNum( calculateEngineEntity.getAllMemoryNum() );
            clusterEntity.setUsedMemoryNum( calculateEngineEntity.getUsedMemoryNum() );
            clusterEntity.setAllStorageNum( calculateEngineEntity.getAllStorageNum() );
            clusterEntity.setUsedStorageNum( calculateEngineEntity.getUsedStorageNum() );
            clusterEntity.setClusterType( calculateEngineEntity.getClusterType() );
            clusterEntity.setCreateDateTime( calculateEngineEntity.getCreateDateTime() );
            clusterEntity.setLastModifiedDateTime( calculateEngineEntity.getLastModifiedDateTime() );
            clusterEntity.setCreateBy( calculateEngineEntity.getCreateBy() );
            clusterEntity.setLastModifiedBy( calculateEngineEntity.getLastModifiedBy() );
            clusterEntity.setVersionNumber( calculateEngineEntity.getVersionNumber() );
            clusterEntity.setDeleted( calculateEngineEntity.getDeleted() );
            clusterEntity.setTenantId( calculateEngineEntity.getTenantId() );
        }

        return clusterEntity;
    }

    @Override
    public PageClusterRes clusterEntityToPageClusterRes(ClusterEntity clusterEntity) {
        if ( clusterEntity == null ) {
            return null;
        }

        PageClusterRes pageClusterRes = new PageClusterRes();

        if ( clusterEntity.getCheckDateTime() != null ) {
            pageClusterRes.setCheckDateTime( dateTimeFormatter_yyyy_MM_dd_HH_mm_ss_11333195168.format( clusterEntity.getCheckDateTime() ) );
        }
        pageClusterRes.setId( clusterEntity.getId() );
        pageClusterRes.setName( clusterEntity.getName() );
        pageClusterRes.setStatus( clusterEntity.getStatus() );
        pageClusterRes.setRemark( clusterEntity.getRemark() );
        pageClusterRes.setClusterType( clusterEntity.getClusterType() );

        pageClusterRes.setNode( clusterEntity.getActiveNodeNum()+ "/" +clusterEntity.getAllNodeNum() );
        pageClusterRes.setMemory( clusterEntity.getUsedMemoryNum()+ "G/" +clusterEntity.getAllMemoryNum()+"G" );
        pageClusterRes.setStorage( clusterEntity.getUsedStorageNum()+ "G/"  +clusterEntity.getAllStorageNum()+"G" );

        return pageClusterRes;
    }
}
