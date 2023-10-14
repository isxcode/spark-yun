package com.isxcode.star.modules.work.service.biz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.isxcode.star.api.work.pojos.req.GetSyncWorkConfigReq;
import com.isxcode.star.api.work.pojos.req.SaveSyncWorkConfigReq;
import com.isxcode.star.api.work.pojos.res.GetSyncWorkConfigRes;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.modules.work.entity.SyncWorkConfigEntity;
import com.isxcode.star.modules.work.entity.WorkEntity;
import com.isxcode.star.modules.work.mapper.SyncWorkConfigMapper;
import com.isxcode.star.modules.work.repository.SyncWorkConfigRepository;
import com.isxcode.star.modules.work.repository.WorkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/** 用户模块接口的业务逻辑. */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SyncWorkConfigBizService {

	private final WorkRepository workRepository;

	private final SyncWorkConfigRepository syncWorkConfigRepository;

  private final SyncWorkConfigMapper syncWorkConfigMapper;

	public GetSyncWorkConfigRes getSyncWorkConfig(GetSyncWorkConfigReq getSyncWorkConfigReq) {

		Optional<SyncWorkConfigEntity> syncWorkConfigEntityOptional = Optional.ofNullable(syncWorkConfigRepository.findByWorkId(getSyncWorkConfigReq.getWorkId()));
		if (!syncWorkConfigEntityOptional.isPresent()) {
			throw new IsxAppException("作业异常，请联系开发者");
		}

    GetSyncWorkConfigRes getSyncWorkConfigRes = syncWorkConfigMapper
      .syncWorkConfigEntityToGetSyncWorkConfigRes(syncWorkConfigEntityOptional.get());
    getSyncWorkConfigRes.setColumMapping(JSON.parseObject(getSyncWorkConfigRes.getColumMapping().toString(),
      new TypeReference<HashMap<String, List<String>>>() {
      }));
    return getSyncWorkConfigRes;
	}

	public void saveSyncWorkConfig(SaveSyncWorkConfigReq saveSyncWorkConfigReq) {

		Optional<WorkEntity> workEntityOptional = workRepository.findById(saveSyncWorkConfigReq.getWorkId());
		if (!workEntityOptional.isPresent()) {
			throw new IsxAppException("作业不存在");
		}

    SyncWorkConfigEntity syncWorkConfigEntity = syncWorkConfigRepository.findByWorkId(workEntityOptional.get().getId());
    saveSyncWorkConfigReq.setColumMapping(JSON.toJSON(saveSyncWorkConfigReq.getColumMapping()));
    syncWorkConfigRepository.save(syncWorkConfigMapper.saveSyncWorkConfigReqAndSyncWorkConfigEntityToSyncWorkConfigEntity(saveSyncWorkConfigReq,syncWorkConfigEntity));
	}
}
