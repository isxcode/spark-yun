package com.isxcode.star.modules.func.service;

import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.modules.func.entity.FuncEntity;
import com.isxcode.star.modules.func.repository.FuncRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FuncService {

    private final FuncRepository funcRepository;

    public FuncEntity getFunc(String funcId) {

        return funcRepository.findById(funcId).orElseThrow(() -> new IsxAppException("自定义函数不存在"));
    }
}
