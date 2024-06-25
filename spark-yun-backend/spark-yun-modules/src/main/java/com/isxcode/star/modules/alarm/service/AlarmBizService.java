package com.isxcode.star.modules.alarm.service;

import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.modules.cluster.entity.ClusterEntity;
import com.isxcode.star.modules.cluster.repository.ClusterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/** 计算引擎模块. */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AlarmBizService {

	private final ClusterRepository clusterRepository;

	public ClusterEntity getCluster(String clusterId) {

		return clusterRepository.findById(clusterId).orElseThrow(() -> new IsxAppException("计算引擎不存在"));
	}

	public void checkCluster(String clusterId) {

		clusterRepository.findById(clusterId).orElseThrow(() -> new IsxAppException("计算引擎不存在"));
	}
}
