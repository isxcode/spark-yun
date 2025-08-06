package com.isxcode.spark.modules.user.service;

import com.isxcode.spark.backend.api.base.exceptions.IsxAppException;
import com.isxcode.spark.security.user.UserEntity;
import com.isxcode.spark.security.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public UserEntity getUser(String userId) {

        return userRepository.findById(userId).orElseThrow(() -> new IsxAppException("用户不存在"));
    }

    public String getUserName(String userId) {

        UserEntity user = userRepository.findById(userId).orElse(null);
        return user == null ? userId : user.getUsername();
    }
}
