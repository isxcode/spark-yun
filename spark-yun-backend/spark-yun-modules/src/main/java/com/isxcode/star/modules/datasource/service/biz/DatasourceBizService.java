package com.isxcode.star.modules.datasource.service.biz;

import com.isxcode.star.api.datasource.constants.DatasourceStatus;
import com.isxcode.star.api.datasource.pojos.req.*;
import com.isxcode.star.api.datasource.pojos.res.GetConnectLogRes;
import com.isxcode.star.api.datasource.pojos.res.PageDatasourceRes;
import com.isxcode.star.api.datasource.pojos.res.TestConnectRes;
import com.isxcode.star.common.utils.AesUtils;
import com.isxcode.star.modules.datasource.entity.DatasourceEntity;
import com.isxcode.star.modules.datasource.mapper.DatasourceMapper;
import com.isxcode.star.modules.datasource.repository.DatasourceRepository;
import com.isxcode.star.modules.datasource.service.DatasourceService;
import java.sql.Connection;
import java.time.LocalDateTime;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class DatasourceBizService {

	private final DatasourceRepository datasourceRepository;

	private final DatasourceMapper datasourceMapper;

	private final AesUtils aesUtils;

	private final DatasourceService datasourceService;

	public void addDatasource(AddDatasourceReq addDatasourceReq) {

		DatasourceEntity datasource = datasourceMapper.dasAddDatasourceReqToDatasourceEntity(addDatasourceReq);

		// 密码对成加密
		datasource.setPasswd(aesUtils.encrypt(datasource.getPasswd()));

		datasource.setCheckDateTime(LocalDateTime.now());
		datasource.setStatus(DatasourceStatus.UN_CHECK);
		datasourceRepository.save(datasource);
	}

	public void updateDatasource(UpdateDatasourceReq updateDatasourceReq) {

		DatasourceEntity datasource = datasourceService.getDatasource(updateDatasourceReq.getId());

		datasource = datasourceMapper.dasUpdateDatasourceReqToDatasourceEntity(updateDatasourceReq, datasource);

		// 密码对成加密
		datasource.setPasswd(aesUtils.encrypt(datasource.getPasswd()));

		datasource.setCheckDateTime(LocalDateTime.now());
		datasource.setStatus(DatasourceStatus.UN_CHECK);
		datasourceRepository.save(datasource);
	}

	public Page<PageDatasourceRes> pageDatasource(PageDatasourceReq dasQueryDatasourceReq) {

		Page<DatasourceEntity> datasourceEntityPage = datasourceRepository.searchAll(
				dasQueryDatasourceReq.getSearchKeyWord(),
				PageRequest.of(dasQueryDatasourceReq.getPage(), dasQueryDatasourceReq.getPageSize()));

		return datasourceMapper.datasourceEntityToQueryDatasourceResPage(datasourceEntityPage);
	}

	public void deleteDatasource(DeleteDatasourceReq deleteDatasourceReq) {

		datasourceRepository.deleteById(deleteDatasourceReq.getDatasourceId());
	}

	public TestConnectRes testConnect(GetConnectLogReq testConnectReq) {

		DatasourceEntity datasource = datasourceService.getDatasource(testConnectReq.getDatasourceId());

		datasourceService.loadDriverClass(datasource.getDbType());

		// 测试连接
		datasource.setCheckDateTime(LocalDateTime.now());

		try (Connection connection = datasourceService.getDbConnection(datasource)) {
			if (connection != null) {
				datasource.setStatus(DatasourceStatus.ACTIVE);
				datasource.setConnectLog("测试连接成功！");
				datasourceRepository.save(datasource);
				return new TestConnectRes(true, "连接成功");
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			datasource.setStatus(DatasourceStatus.FAIL);
			datasource.setConnectLog("测试连接失败：" + e.getMessage());
			datasourceRepository.save(datasource);
			return new TestConnectRes(false, e.getMessage());
		}
		return new TestConnectRes(false, "连接失败");
	}

	public GetConnectLogRes getConnectLog(GetConnectLogReq getConnectLogReq) {

		DatasourceEntity datasource = datasourceService.getDatasource(getConnectLogReq.getDatasourceId());

		return GetConnectLogRes.builder().connectLog(datasource.getConnectLog()).build();
	}
}
