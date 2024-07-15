package com.isxcode.star.common.utils.http;

import com.isxcode.star.backend.api.base.properties.IsxAppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** 网页访问路径. */
@Slf4j
@Component
@RequiredArgsConstructor
public class HttpUrlUtils {

    private final IsxAppProperties isxAppProperties;

    /** 生成http访问路径. */
    public String genHttpUrl(String host, String port, String path) {

        String httpProtocol = isxAppProperties.isUseSsl() ? "https://" : "http://";
        String httpHost = isxAppProperties.isUsePort() ? host + ":" + port : host;

        return httpProtocol + httpHost + path;
    }
}
