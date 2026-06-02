package com.isxcode.spark.modules.license.repository;

import com.alibaba.fastjson.JSON;
import com.isxcode.spark.api.license.req.LicenseReq;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class LicenseStore {
    private final ConcurrentMapCache licenseCache = new ConcurrentMapCache("license");

    public void setLicense(LicenseReq license) {
        licenseCache.put("license", JSON.toJSONString(license));
    }

    public LicenseReq getLicense() {
        if (licenseCache.get("license") == null) {
            return null;
        }
        return JSON.parseObject(String.valueOf(Objects.requireNonNull(licenseCache.get("license")).get()),
            LicenseReq.class);
    }

    public void clearLicense() {
        licenseCache.clear();
    }
}
