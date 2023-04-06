package com.isxcode.star.backend.module.calculate.engine.service;

import com.isxcode.star.api.pojos.calculate.engine.req.CaeAddEngineReq;
import com.isxcode.star.api.pojos.calculate.engine.req.CaeQueryEngineReq;
import com.isxcode.star.api.pojos.calculate.engine.res.CaeQueryEngineRes;
import com.isxcode.star.backend.module.calculate.engine.entity.CalculateEngineEntity;
import com.isxcode.star.backend.module.calculate.engine.mapper.CalculateEngineMapper;
import com.isxcode.star.backend.module.calculate.engine.repository.CalculateEngineRepository;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/** 计算引擎模块. */
@Service
@RequiredArgsConstructor
@Transactional
public class CalculateEngineBizService {

  private final CalculateEngineRepository engineRepository;

  private final CalculateEngineMapper engineMapper;

  public void addEngine(CaeAddEngineReq caeAddEngineReq) {

    CalculateEngineEntity engine = engineMapper.addEngineReqToEngineEntity(caeAddEngineReq);

    engineRepository.save(engine);
  }

  public Page<CaeQueryEngineRes> queryEngines(CaeQueryEngineReq caeQueryEngineReq) {

    Page<CalculateEngineEntity> engineEntities =
        engineRepository.findAll(
            PageRequest.of(caeQueryEngineReq.getPage(), caeQueryEngineReq.getPageSize()));

    return engineMapper.engineEntityPageToCaeQueryEngineResPage(engineEntities);
  }

  public void delEngine(String engineId) {

    engineRepository.deleteById(engineId);
  }
}
