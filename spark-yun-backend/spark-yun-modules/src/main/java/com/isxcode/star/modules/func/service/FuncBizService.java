package com.isxcode.star.modules.func.service;

import com.isxcode.star.api.func.pojos.req.AddFuncReq;
import com.isxcode.star.api.func.pojos.req.DeleteFuncReq;
import com.isxcode.star.api.func.pojos.req.PageFuncReq;
import com.isxcode.star.api.func.pojos.req.UpdateFuncReq;
import com.isxcode.star.api.func.pojos.res.PageFuncRes;
import com.isxcode.star.modules.func.entity.FuncEntity;
import com.isxcode.star.modules.func.mapper.FuncMapper;
import com.isxcode.star.modules.func.repository.FuncRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FuncBizService {

	private final FuncRepository funcRepository;

	private final FuncService funcService;

	private final FuncMapper funcMapper;

	public void addFunc(AddFuncReq addFuncReq) {

		FuncEntity funcEntity = funcMapper.addFuncReqToFuncEntity(addFuncReq);

		// 持久化数据
		funcRepository.save(funcEntity);
	}

	public void updateFunc(UpdateFuncReq updateFuncReq) {

		FuncEntity func = funcService.getFunc(updateFuncReq.getId());
		func = funcMapper.updateFuncReqToFuncEntity(updateFuncReq, func);
		funcRepository.save(func);
	}

	public void deleteFunc(DeleteFuncReq deleteFuncReq) {

		FuncEntity func = funcService.getFunc(deleteFuncReq.getId());

		funcRepository.deleteById(func.getId());
	}

	public Page<PageFuncRes> pageFunc(PageFuncReq pageFuncReq) {

		Page<FuncEntity> funcPage = funcRepository.pageSearch(pageFuncReq.getSearchKeyWord(),
				PageRequest.of(pageFuncReq.getPage(), pageFuncReq.getPageSize()));

		return funcPage.map(funcMapper::funcEntityToPageFuncRes);
	}
}
