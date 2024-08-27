package com.isxcode.star.modules.work.service;

import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.modules.work.entity.WorkEntity;
import com.isxcode.star.modules.work.repository.WorkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkService {

    private final WorkRepository workRepository;

    public WorkEntity getWorkEntity(String workId) {

        return workRepository.findById(workId).orElseThrow(() -> new IsxAppException("作业不存在"));
    }
}
