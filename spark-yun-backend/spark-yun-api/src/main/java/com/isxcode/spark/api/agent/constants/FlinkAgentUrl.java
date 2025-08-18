package com.isxcode.spark.api.agent.constants;


import com.isxcode.spark.api.main.constants.ModuleCode;

/**
 * 代理接口访问地址.
 */
public interface FlinkAgentUrl {

    String SUBMIT_WORK_URL = "/" + ModuleCode.FLINK_YUN_AGENT + "/submitWork";

    String GET_WORK_INFO_URL = "/" + ModuleCode.FLINK_YUN_AGENT + "/getWorkInfo";

    String GET_WORK_LOG_URL = "/" + ModuleCode.FLINK_YUN_AGENT + "/getWorkLog";

    String STOP_WORK_URL = "/" + ModuleCode.FLINK_YUN_AGENT + "/stopWork";

    String HEART_CHECK_URL = "/" + ModuleCode.FLINK_YUN_AGENT + "/heartCheck";
}
