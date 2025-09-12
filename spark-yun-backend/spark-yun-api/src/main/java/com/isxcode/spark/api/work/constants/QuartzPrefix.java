package com.isxcode.spark.api.work.constants;

/**
 * 定时器前缀.
 */
public interface QuartzPrefix {

    /**
     * 作业运行每秒定时器.
     */
    String WORK_RUN_PROCESS = "work_run_event_";

    String USER_ID = "user_id";

    String TENANT_ID = "tenant_id";

    String WORK_TYPE = "work_type";

    String WORK_EVENT_TYPE = "work_event_type";

    String WORK_EVENT_ID = "work_event_id";
}
