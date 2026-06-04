package com.isxcode.spark.common.jpa;

import com.isxcode.spark.common.security.ContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import org.apache.logging.log4j.util.Strings;

public final class JpaTenantContext {

    public static final String TENANT_FILTER = "tenantFilter";

    public static final String TENANT_IDS_PARAM = "tenantIds";

    public static final String SHARE_TENANT_ID = "zhiqingyun";

    private static final ThreadLocal<List<String>> VISIBLE_TENANT_IDS = new ThreadLocal<>();

    private static final ThreadLocal<Boolean> ALL_DATA = ThreadLocal.withInitial(() -> false);

    private JpaTenantContext() {
    }

    public static List<String> getVisibleTenantIds() {

        if (Boolean.TRUE.equals(ALL_DATA.get()) || Strings.isEmpty(ContextHolder.getTenantId())) {
            return Collections.emptyList();
        }

        List<String> visibleTenantIds = VISIBLE_TENANT_IDS.get();
        if (visibleTenantIds != null) {
            return visibleTenantIds;
        }

        return Collections.singletonList(ContextHolder.getTenantId());
    }

    public static <T> T joinShareData(Supplier<T> supplier) {

        if (Strings.isEmpty(ContextHolder.getTenantId())) {
            return runWithTenantIds(Collections.singletonList(SHARE_TENANT_ID), supplier);
        }

        return runWithTenantIds(List.of(ContextHolder.getTenantId(), SHARE_TENANT_ID), supplier);
    }

    public static void joinShareData(Runnable runnable) {

        joinShareData(() -> {
            runnable.run();
            return null;
        });
    }

    public static <T> T joinAllData(Supplier<T> supplier) {

        return runWithAllData(supplier);
    }

    public static void joinAllData(Runnable runnable) {

        runWithAllData(runnable);
    }

    public static <T> T runWithAllData(Supplier<T> supplier) {

        List<String> oldTenantIds = VISIBLE_TENANT_IDS.get();
        Boolean oldAllData = ALL_DATA.get();
        try {
            VISIBLE_TENANT_IDS.remove();
            ALL_DATA.set(true);
            return supplier.get();
        } finally {
            restore(oldTenantIds, oldAllData);
        }
    }

    public static void runWithAllData(Runnable runnable) {

        runWithAllData(() -> {
            runnable.run();
            return null;
        });
    }

    private static <T> T runWithTenantIds(List<String> tenantIds, Supplier<T> supplier) {

        List<String> oldTenantIds = VISIBLE_TENANT_IDS.get();
        Boolean oldAllData = ALL_DATA.get();
        try {
            ALL_DATA.set(false);
            VISIBLE_TENANT_IDS.set(tenantIds.stream().filter(Strings::isNotEmpty).distinct().toList());
            return supplier.get();
        } finally {
            restore(oldTenantIds, oldAllData);
        }
    }

    private static void runWithTenantIds(List<String> tenantIds, Runnable runnable) {

        runWithTenantIds(tenantIds, () -> {
            runnable.run();
            return null;
        });
    }

    private static void restore(List<String> tenantIds, Boolean allData) {

        if (tenantIds == null) {
            VISIBLE_TENANT_IDS.remove();
        } else {
            VISIBLE_TENANT_IDS.set(tenantIds);
        }

        if (allData == null) {
            ALL_DATA.remove();
        } else {
            ALL_DATA.set(allData);
        }
    }
}
