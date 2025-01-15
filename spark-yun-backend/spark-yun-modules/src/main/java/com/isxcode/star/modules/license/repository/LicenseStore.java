package com.isxcode.star.modules.license.repository;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.license.req.LicenseReq;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class LicenseStore {
    private final ConcurrentMapCache LicenseCache = new ConcurrentMapCache("license");

    public void setLicense(LicenseReq license) {
        LicenseCache.put("license", JSON.toJSONString(license));
    }

    public LicenseReq getLicense() {
        if (LicenseCache.get("license") == null) {
            return null;
        }
        return JSON.parseObject(String.valueOf(Objects.requireNonNull(LicenseCache.get("license")).get()),
            LicenseReq.class);
    }

    public void clearLicense() {
        LicenseCache.clear();
    }
}
