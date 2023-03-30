package com.isxcode.star.backend.module.engine.service;

import com.isxcode.star.api.pojos.engine.req.AddEngineReq;
import com.isxcode.star.api.pojos.engine.res.QueryEngineRes;
import com.isxcode.star.backend.module.engine.entity.EngineEntity;
import com.isxcode.star.backend.module.engine.mapper.EngineMapper;
import com.isxcode.star.backend.module.engine.repository.EngineRepository;
import java.time.LocalDateTime;
import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** 用户模块接口的业务逻辑. */
@Service
@RequiredArgsConstructor
@Transactional
public class EngineBizService {

  private final EngineRepository engineRepository;

  private final EngineMapper engineMapper;

  public void addEngine(AddEngineReq addEngineReq) {

    // req 转 entity
    EngineEntity engine = engineMapper.addEngineReqToEngineEntity(addEngineReq);
    engine.setActiveNode(0);
    engine.setAllNode(0);
    engine.setActiveMemory(0);
    engine.setAllMemory(0);
    engine.setActiveStorage(0);
    engine.setAllStorage(0);
    engine.setCheckDate(LocalDateTime.now());
    engine.setStatus("未检测");

    // 数据持久化
    engineRepository.save(engine);
  }

  public List<QueryEngineRes> queryEngines() {

    List<EngineEntity> engineEntities = engineRepository.findAll();

    return engineMapper.engineEntityListToQueryEngineResList(engineEntities);
  }

  public void delEngine(String engineId) {

    engineRepository.deleteById(engineId);
  }
}
