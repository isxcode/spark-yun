package com.isxcode.star.common.utils.aes;

import cn.hutool.crypto.SecureUtil;
import com.isxcode.star.backend.api.base.properties.IsxAppProperties;

import java.util.Arrays;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

/**
 * 对称加密工具类.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AesUtils {

    private final IsxAppProperties isxAppProperties;

    /**
     * 对称加密.
     */
    public String encrypt(String data) {

        if (data == null) {
            return null;
        }

        if (Strings.isEmpty(data)) {
            return "";
        }

        return SecureUtil.aes(Arrays.copyOf(isxAppProperties.getAesSlat().getBytes(), 1 << 5)).encryptBase64(data);
    }

    /**
     * 对称解密.
     */
    public String decrypt(String data) {

        if (data == null) {
            return null;
        }

        if (Strings.isEmpty(data)) {
            return "";
        }

        return SecureUtil.aes(Arrays.copyOf(isxAppProperties.getAesSlat().getBytes(), 1 << 5)).decryptStr(data);
    }
}
