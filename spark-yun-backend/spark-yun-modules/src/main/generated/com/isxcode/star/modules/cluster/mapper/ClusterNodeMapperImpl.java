package com.isxcode.star.modules.cluster.mapper;

import com.isxcode.star.api.cluster.pojos.dto.ScpFileEngineNodeDto;
import com.isxcode.star.api.cluster.pojos.req.AddClusterNodeReq;
import com.isxcode.star.api.cluster.pojos.req.UpdateClusterNodeReq;
import com.isxcode.star.api.cluster.pojos.res.EnoQueryNodeRes;
import com.isxcode.star.modules.cluster.entity.ClusterNodeEntity;
import java.time.format.DateTimeFormatter;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-10-05T19:39:24+0800",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 1.8.0_271 (Oracle Corporation)"
)
@Component
public class ClusterNodeMapperImpl implements ClusterNodeMapper {

    private final DateTimeFormatter dateTimeFormatter_yyyy_MM_dd_HH_mm_ss_11333195168 = DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm:ss" );

    @Override
    public ClusterNodeEntity addClusterNodeReqToClusterNodeEntity(AddClusterNodeReq addClusterNodeReq) {
        if ( addClusterNodeReq == null ) {
            return null;
        }

        ClusterNodeEntity clusterNodeEntity = new ClusterNodeEntity();

        clusterNodeEntity.setName( addClusterNodeReq.getName() );
        clusterNodeEntity.setRemark( addClusterNodeReq.getRemark() );
        clusterNodeEntity.setClusterId( addClusterNodeReq.getClusterId() );
        clusterNodeEntity.setHost( addClusterNodeReq.getHost() );
        clusterNodeEntity.setPort( addClusterNodeReq.getPort() );
        clusterNodeEntity.setUsername( addClusterNodeReq.getUsername() );
        clusterNodeEntity.setPasswd( addClusterNodeReq.getPasswd() );
        clusterNodeEntity.setAgentHomePath( addClusterNodeReq.getAgentHomePath() );
        clusterNodeEntity.setAgentPort( addClusterNodeReq.getAgentPort() );
        clusterNodeEntity.setHadoopHomePath( addClusterNodeReq.getHadoopHomePath() );

        clusterNodeEntity.setUsedMemory( 0.0 );
        clusterNodeEntity.setAllMemory( 0.0 );
        clusterNodeEntity.setUsedStorage( 0.0 );
        clusterNodeEntity.setAllStorage( 0.0 );
        clusterNodeEntity.setCpuPercent( 0.0 );
        clusterNodeEntity.setCheckDateTime( java.time.LocalDateTime.now() );

        return clusterNodeEntity;
    }

    @Override
    public ClusterNodeEntity updateNodeReqToNodeEntity(UpdateClusterNodeReq enoUpdateNodeReq, ClusterNodeEntity clusterNodeEntity) {
        if ( enoUpdateNodeReq == null && clusterNodeEntity == null ) {
            return null;
        }

        ClusterNodeEntity clusterNodeEntity1 = new ClusterNodeEntity();

        if ( enoUpdateNodeReq != null ) {
            clusterNodeEntity1.setName( enoUpdateNodeReq.getName() );
            clusterNodeEntity1.setRemark( enoUpdateNodeReq.getRemark() );
            clusterNodeEntity1.setHost( enoUpdateNodeReq.getHost() );
            clusterNodeEntity1.setUsername( enoUpdateNodeReq.getUsername() );
            clusterNodeEntity1.setPasswd( enoUpdateNodeReq.getPasswd() );
        }
        if ( clusterNodeEntity != null ) {
            clusterNodeEntity1.setId( clusterNodeEntity.getId() );
            clusterNodeEntity1.setClusterId( clusterNodeEntity.getClusterId() );
            clusterNodeEntity1.setPort( clusterNodeEntity.getPort() );
            clusterNodeEntity1.setAgentHomePath( clusterNodeEntity.getAgentHomePath() );
            clusterNodeEntity1.setAgentPort( clusterNodeEntity.getAgentPort() );
            clusterNodeEntity1.setHadoopHomePath( clusterNodeEntity.getHadoopHomePath() );
            clusterNodeEntity1.setStatus( clusterNodeEntity.getStatus() );
            clusterNodeEntity1.setCheckDateTime( clusterNodeEntity.getCheckDateTime() );
            clusterNodeEntity1.setAllMemory( clusterNodeEntity.getAllMemory() );
            clusterNodeEntity1.setUsedMemory( clusterNodeEntity.getUsedMemory() );
            clusterNodeEntity1.setAllStorage( clusterNodeEntity.getAllStorage() );
            clusterNodeEntity1.setUsedStorage( clusterNodeEntity.getUsedStorage() );
            clusterNodeEntity1.setCpuPercent( clusterNodeEntity.getCpuPercent() );
            clusterNodeEntity1.setAgentLog( clusterNodeEntity.getAgentLog() );
            clusterNodeEntity1.setCreateDateTime( clusterNodeEntity.getCreateDateTime() );
            clusterNodeEntity1.setLastModifiedDateTime( clusterNodeEntity.getLastModifiedDateTime() );
            clusterNodeEntity1.setCreateBy( clusterNodeEntity.getCreateBy() );
            clusterNodeEntity1.setLastModifiedBy( clusterNodeEntity.getLastModifiedBy() );
            clusterNodeEntity1.setDeleted( clusterNodeEntity.getDeleted() );
            clusterNodeEntity1.setTenantId( clusterNodeEntity.getTenantId() );
        }

        return clusterNodeEntity1;
    }

    @Override
    public EnoQueryNodeRes nodeEntityToQueryNodeRes(ClusterNodeEntity nodeEntity) {
        if ( nodeEntity == null ) {
            return null;
        }

        EnoQueryNodeRes enoQueryNodeRes = new EnoQueryNodeRes();

        if ( nodeEntity.getCpuPercent() != null ) {
            enoQueryNodeRes.setCpu( String.valueOf( nodeEntity.getCpuPercent() ) );
        }
        if ( nodeEntity.getCheckDateTime() != null ) {
            enoQueryNodeRes.setCheckDateTime( dateTimeFormatter_yyyy_MM_dd_HH_mm_ss_11333195168.format( nodeEntity.getCheckDateTime() ) );
        }
        enoQueryNodeRes.setId( nodeEntity.getId() );
        enoQueryNodeRes.setName( nodeEntity.getName() );
        enoQueryNodeRes.setRemark( nodeEntity.getRemark() );
        enoQueryNodeRes.setHost( nodeEntity.getHost() );
        enoQueryNodeRes.setUsername( nodeEntity.getUsername() );
        enoQueryNodeRes.setStatus( nodeEntity.getStatus() );
        enoQueryNodeRes.setAgentHomePath( nodeEntity.getAgentHomePath() );
        enoQueryNodeRes.setAgentPort( nodeEntity.getAgentPort() );
        enoQueryNodeRes.setHadoopHomePath( nodeEntity.getHadoopHomePath() );
        enoQueryNodeRes.setPort( nodeEntity.getPort() );
        enoQueryNodeRes.setAgentLog( nodeEntity.getAgentLog() );

        enoQueryNodeRes.setMemory( nodeEntity.getUsedMemory()+ "G/" +nodeEntity.getAllMemory()+"G" );
        enoQueryNodeRes.setStorage( nodeEntity.getUsedStorage()+ "G/" +nodeEntity.getAllStorage()+"G" );

        return enoQueryNodeRes;
    }

    @Override
    public ScpFileEngineNodeDto engineNodeEntityToScpFileEngineNodeDto(ClusterNodeEntity engineNode) {
        if ( engineNode == null ) {
            return null;
        }

        ScpFileEngineNodeDto scpFileEngineNodeDto = new ScpFileEngineNodeDto();

        scpFileEngineNodeDto.setUsername( engineNode.getUsername() );
        scpFileEngineNodeDto.setPasswd( engineNode.getPasswd() );
        scpFileEngineNodeDto.setPort( engineNode.getPort() );
        scpFileEngineNodeDto.setHost( engineNode.getHost() );

        return scpFileEngineNodeDto;
    }
}
