package com.isxcode.spark.common.jpa;

import com.isxcode.spark.common.security.ContextHolder;

import java.util.List;
import java.util.function.Supplier;
import org.apache.logging.log4j.util.Strings;

public final class JpaTenantContext {

    public static final String TENANT_FILTER = "tenantFilter";

    public static final String TENANT_IDS_PARAM = "tenantIds";

    private static final ThreadLocal<Boolean> DISABLE_TENANT_FILTER = ThreadLocal.withInitial(() -> false);

    private JpaTenantContext() {}

    public static List<String> getVisibleTenantIds() {

        if (Boolean.TRUE.equals(DISABLE_TENANT_FILTER.get()) || Strings.isEmpty(ContextHolder.getTenantId())) {
            return List.of();
        }

        return List.of(ContextHolder.getTenantId());
    }

    public static <T> T allData(Supplier<T> supplier) {

        return withoutTenantFilter(supplier);
    }

    public static <T> T noTenant(Supplier<T> supplier) {

        return withoutTenantFilter(supplier);
    }

    private static <T> T withoutTenantFilter(Supplier<T> supplier) {

        Boolean oldDisableTenantFilter = DISABLE_TENANT_FILTER.get();
        try {
            DISABLE_TENANT_FILTER.set(true);
            return supplier.get();
        } finally {
            restore(oldDisableTenantFilter);
        }
    }

    private static void restore(Boolean disableTenantFilter) {

        if (disableTenantFilter == null) {
            DISABLE_TENANT_FILTER.remove();
        } else {
            DISABLE_TENANT_FILTER.set(disableTenantFilter);
        }
    }
}
