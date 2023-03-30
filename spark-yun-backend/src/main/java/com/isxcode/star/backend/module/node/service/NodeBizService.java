package com.isxcode.star.backend.module.node.service;

import com.isxcode.star.api.pojos.node.req.AddNodeReq;
import com.isxcode.star.api.pojos.node.res.QueryNodeRes;
import com.isxcode.star.backend.module.node.entity.NodeEntity;
import com.isxcode.star.backend.module.node.mapper.NodeMapper;
import com.isxcode.star.backend.module.node.repository.NodeRepository;
import java.time.LocalDateTime;
import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** 用户模块接口的业务逻辑. */
@Service
@RequiredArgsConstructor
@Transactional
public class NodeBizService {

  private final NodeRepository nodeRepository;

  private final NodeMapper nodeMapper;

  public void addNode(AddNodeReq addNodeReq) {

    // req 转 entity
    NodeEntity node = nodeMapper.addNodeReqToNodeEntity(addNodeReq);
    node.setActiveMemory(0);
    node.setAllMemory(0);
    node.setActiveStorage(0);
    node.setAllStorage(0);
    node.setCpuPercent("0%");
    node.setCheckDate(LocalDateTime.now());
    node.setStatus("未安装");

    // 数据持久化
    nodeRepository.save(node);
  }

  public List<QueryNodeRes> queryNodes(String engineId) {

    List<NodeEntity> nodeEntities = nodeRepository.findAllByEngineId(engineId);

    return nodeMapper.nodeEntityListToQueryNodeResList(nodeEntities);
  }

  public void delNode(String nodeId) {

    nodeRepository.deleteById(nodeId);
  }
}
