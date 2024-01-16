package com.isxcode.star.modules.monitor.service;

import com.isxcode.star.api.monitor.dto.SystemMonitorDto;
import com.isxcode.star.api.monitor.req.GetClusterMonitorReq;
import com.isxcode.star.api.monitor.req.GetInstanceMonitorReq;
import com.isxcode.star.api.monitor.req.QueryInstancesReq;
import com.isxcode.star.api.monitor.res.GetClusterMonitorRes;
import com.isxcode.star.api.monitor.res.GetInstanceMonitorRes;
import com.isxcode.star.api.monitor.res.GetSystemMonitorRes;
import com.isxcode.star.api.monitor.res.QueryInstancesRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class MonitorBizService {

	public GetSystemMonitorRes getSystemMonitor() {

		// 查询集群信息
		SystemMonitorDto clusterMonitor = new SystemMonitorDto();

		// 查询数据源
		SystemMonitorDto datasourceMonitor = new SystemMonitorDto();

		// 查询作业信息
		SystemMonitorDto workMonitor = new SystemMonitorDto();

		// 查询接口信息
		SystemMonitorDto apiMonitor = new SystemMonitorDto();
		apiMonitor.setTotal(10);
		apiMonitor.setActiveNum(7);

		return GetSystemMonitorRes.builder().apiMonitor(apiMonitor).workMonitor(workMonitor)
				.clusterMonitor(clusterMonitor).datasourceMonitor(datasourceMonitor).build();
	}

	public GetClusterMonitorRes getClusterMonitor(GetClusterMonitorReq getClusterMonitorReq) {

		return null;
	}

	public GetInstanceMonitorRes getInstanceMonitor(GetInstanceMonitorReq getInstanceMonitorReq) {

		return null;
	}

	public QueryInstancesRes queryInstances(QueryInstancesReq queryInstancesReq) {

		return null;
	}
}
