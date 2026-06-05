package com.isxcode.spark.common.jpa;

import com.isxcode.spark.common.security.ContextHolder;

import java.util.List;
import java.util.function.Supplier;
import org.apache.logging.log4j.util.Strings;

public final class JpaTenantContext {

    public static final String TENANT_FILTER = "tenantFilter";

    public static final String TENANT_IDS_PARAM = "tenantIds";

    public static final String SHARE_TENANT_ID = "zhiqingyun";

    private static final ThreadLocal<TenantMode> TENANT_MODE = new ThreadLocal<>();

    private JpaTenantContext() {}

    public static List<String> getVisibleTenantIds() {

        if (TenantMode.ALL_DATA.equals(TENANT_MODE.get())) {
            return List.of();
        }

        if (TenantMode.SHARE_DATA.equals(TENANT_MODE.get())) {
            if (Strings.isEmpty(ContextHolder.getTenantId())) {
                return List.of(SHARE_TENANT_ID);
            }

            if (SHARE_TENANT_ID.equals(ContextHolder.getTenantId())) {
                return List.of(SHARE_TENANT_ID);
            }

            return List.of(ContextHolder.getTenantId(), SHARE_TENANT_ID);
        }

        if (Strings.isEmpty(ContextHolder.getTenantId())) {
            return List.of();
        }

        return List.of(ContextHolder.getTenantId());
    }

    public static <T> T allData(Supplier<T> supplier) {

        return runWithTenantMode(TenantMode.ALL_DATA, supplier);
    }

    public static <T> T noTenant(Supplier<T> supplier) {

        return runWithTenantMode(TenantMode.SHARE_DATA, supplier);
    }

    private static <T> T runWithTenantMode(TenantMode tenantMode, Supplier<T> supplier) {

        TenantMode oldTenantMode = TENANT_MODE.get();
        try {
            TENANT_MODE.set(tenantMode);
            return supplier.get();
        } finally {
            restore(oldTenantMode);
        }
    }

    private static void restore(TenantMode tenantMode) {

        if (tenantMode == null) {
            TENANT_MODE.remove();
        } else {
            TENANT_MODE.set(tenantMode);
        }
    }

    private enum TenantMode {
        SHARE_DATA,
        ALL_DATA
    }
}
