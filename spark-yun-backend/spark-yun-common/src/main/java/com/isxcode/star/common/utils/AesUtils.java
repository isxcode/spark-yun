package com.isxcode.star.common.utils;

import cn.hutool.crypto.SecureUtil;
import com.isxcode.star.backend.api.base.properties.SparkYunProperties;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** 对称加密工具类. */
@Slf4j
@Component
@RequiredArgsConstructor
public class AesUtils {

  private final SparkYunProperties sparkYunProperties;

  /** 对称加密. */
  public String encrypt(String data) {

    return SecureUtil.aes(Arrays.copyOf(sparkYunProperties.getAesSlat().getBytes(), 1 << 5))
        .encryptBase64(data);
  }

  /** 对称解密. */
  public String decrypt(String data) {

    return SecureUtil.aes(Arrays.copyOf(sparkYunProperties.getAesSlat().getBytes(), 1 << 5))
        .decryptStr(data);
  }
}
