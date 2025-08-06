package com.isxcode.spark.modules.func.service;

import com.isxcode.spark.api.func.req.AddFuncReq;
import com.isxcode.spark.api.func.req.DeleteFuncReq;
import com.isxcode.spark.api.func.req.PageFuncReq;
import com.isxcode.spark.api.func.req.UpdateFuncReq;
import com.isxcode.spark.api.func.res.PageFuncRes;
import com.isxcode.spark.backend.api.base.exceptions.IsxAppException;
import com.isxcode.spark.modules.file.service.FileService;
import com.isxcode.spark.modules.func.entity.FuncEntity;
import com.isxcode.spark.modules.func.mapper.FuncMapper;
import com.isxcode.spark.modules.func.repository.FuncRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FuncBizService {

    private final FuncRepository funcRepository;

    private final FuncService funcService;

    private final FuncMapper funcMapper;

    private final FileService fileService;

    public void addFunc(AddFuncReq addFuncReq) {

        // 判断函数名是否重复
        Optional<FuncEntity> funcEntityOptional = funcRepository.findByFuncName(addFuncReq.getFuncName());
        if (funcEntityOptional.isPresent()) {
            throw new IsxAppException("函数已重复存在");
        }

        FuncEntity funcEntity = funcMapper.addFuncReqToFuncEntity(addFuncReq);

        // 持久化数据
        funcRepository.save(funcEntity);
    }

    public void updateFunc(UpdateFuncReq updateFuncReq) {

        FuncEntity func = funcService.getFunc(updateFuncReq.getId());

        // 判断函数名重复
        funcRepository.findByFuncName(updateFuncReq.getFuncName()).ifPresent(e -> {
            if (!e.getId().equals(updateFuncReq.getId())) {
                throw new IsxAppException("函数名称重复");
            }
        });

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

        Page<PageFuncRes> result = funcPage.map(funcMapper::funcEntityToPageFuncRes);
        result.getContent().forEach(e -> {
            e.setFileName(fileService.getFileName(e.getFileId()));
        });

        return result;
    }
}
