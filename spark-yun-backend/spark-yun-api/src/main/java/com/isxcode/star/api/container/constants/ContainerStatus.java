package com.isxcode.star.api.container.constants;

/**
 * 容器状态.
 */
public interface ContainerStatus {

    /**
     * 新建.
     */
    String NEW = "NEW";

    /**
     * 运行中.
     */
    String RUNNING = "RUNNING";

    /**
     * 停止.
     */
    String STOP = "STOP";

    /**
     * 启动.
     */
    String FAIL = "FAIL";

    /**
     * 启动中.
     */
    String DEPLOYING = "DEPLOYING";
}
