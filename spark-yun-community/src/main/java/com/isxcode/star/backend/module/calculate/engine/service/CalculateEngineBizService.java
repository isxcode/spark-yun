package com.isxcode.star.backend.module.calculate.engine.service;

import com.isxcode.star.api.constants.CalculateEngineStatus;
import com.isxcode.star.api.constants.EngineNodeStatus;
import com.isxcode.star.api.pojos.calculate.engine.req.CaeAddEngineReq;
import com.isxcode.star.api.pojos.calculate.engine.req.CaeQueryEngineReq;
import com.isxcode.star.api.pojos.calculate.engine.req.CaeUpdateEngineReq;
import com.isxcode.star.api.pojos.calculate.engine.res.CaeQueryEngineRes;
import com.isxcode.star.backend.module.calculate.engine.entity.CalculateEngineEntity;
import com.isxcode.star.backend.module.calculate.engine.mapper.CalculateEngineMapper;
import com.isxcode.star.backend.module.calculate.engine.repository.CalculateEngineRepository;
import javax.transaction.Transactional;

import com.isxcode.star.backend.module.engine.node.entity.EngineNodeEntity;
import com.isxcode.star.backend.module.engine.node.repository.EngineNodeRepository;
import com.isxcode.star.api.exception.SparkYunException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/** 计算引擎模块. */
@Service
@RequiredArgsConstructor
@Transactional
public class CalculateEngineBizService {

  private final CalculateEngineRepository engineRepository;

  private final EngineNodeRepository engineNodeRepository;

  private final CalculateEngineMapper engineMapper;

  public void addEngine(CaeAddEngineReq caeAddEngineReq) {

    CalculateEngineEntity engine = engineMapper.addEngineReqToEngineEntity(caeAddEngineReq);

    engine.setStatus(CalculateEngineStatus.NEW);

    engineRepository.save(engine);
  }

  public void updateEngine(CaeUpdateEngineReq caeUpdateEngineReq) {

    Optional<CalculateEngineEntity> calculateEngineEntityOptional = engineRepository.findById(caeUpdateEngineReq.getCalculateEngineId());
    if (!calculateEngineEntityOptional.isPresent()) {
      throw new SparkYunException("计算引擎不存在");
    }

    CalculateEngineEntity engine = engineMapper.updateEngineReqToEngineEntity(caeUpdateEngineReq,calculateEngineEntityOptional.get());

    engineRepository.save(engine);
  }

  public Page<CaeQueryEngineRes> queryEngines(CaeQueryEngineReq caeQueryEngineReq) {

    Page<CalculateEngineEntity> engineEntities =
      engineRepository.searchAll(caeQueryEngineReq.getSearchKeyWord(),
        PageRequest.of(caeQueryEngineReq.getPage(), caeQueryEngineReq.getPageSize()));

    return engineMapper.engineEntityPageToCaeQueryEngineResPage(engineEntities);
  }

  public void delEngine(String engineId) {

    engineRepository.deleteById(engineId);
  }

  public void checkEngine(String engineId) {

    Optional<CalculateEngineEntity> calculateEngineEntityOptional = engineRepository.findById(engineId);
    if (!calculateEngineEntityOptional.isPresent()) {
      throw new SparkYunException("计算引擎不存在");
    }
    CalculateEngineEntity calculateEngineEntity = calculateEngineEntityOptional.get();

    List<EngineNodeEntity> engineNodes = engineNodeRepository.findAllByClusterId(engineId);

    // 激活节点
    List<EngineNodeEntity> activeNodes = engineNodes.stream().filter(e -> EngineNodeStatus.ACTIVE.equals(e.getStatus())).collect(Collectors.toList());
    calculateEngineEntity.setActiveNodeNum(activeNodes.size());
    calculateEngineEntity.setAllNodeNum(engineNodes.size());

    // 内存
    double allMemory = activeNodes.stream().mapToDouble(EngineNodeEntity::getAllMemory).sum();
    calculateEngineEntity.setAllMemoryNum(allMemory);
    double usedMemory = activeNodes.stream().mapToDouble(EngineNodeEntity::getUsedMemory).sum();
    calculateEngineEntity.setUsedMemoryNum(usedMemory);

    // 存储
    double allStorage = activeNodes.stream().mapToDouble(EngineNodeEntity::getAllStorage).sum();
    calculateEngineEntity.setAllStorageNum(allStorage);
    double usedStorage = activeNodes.stream().mapToDouble(EngineNodeEntity::getUsedStorage).sum();
    calculateEngineEntity.setUsedStorageNum(usedStorage);

    if (!activeNodes.isEmpty()) {
      calculateEngineEntity.setStatus(CalculateEngineStatus.ACTIVE);
    } else {
      calculateEngineEntity.setStatus(CalculateEngineStatus.NO_ACTIVE);
    }

    calculateEngineEntity.setCheckDateTime(LocalDateTime.now());
    engineRepository.save(calculateEngineEntity);
  }
}
