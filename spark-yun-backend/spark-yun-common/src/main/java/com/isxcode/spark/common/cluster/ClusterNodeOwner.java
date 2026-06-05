package com.isxcode.spark.common.cluster;

import java.lang.management.ManagementFactory;

public final class ClusterNodeOwner {

    private ClusterNodeOwner() {}

    public static String getOwner() {

        String hostname = System.getenv("HOSTNAME");
        if (hostname != null && !hostname.isBlank()) {
            return hostname;
        }

        return "local-" + ManagementFactory.getRuntimeMXBean().getName();
    }
}
