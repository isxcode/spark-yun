package com.isxcode.star.api.agent.constants;

import com.isxcode.star.api.main.constants.ModuleCode;

/**
 * 代理接口访问地址.
 */
public interface AgentUrl {

    String SUBMIT_WORK_URL = "/" + ModuleCode.SPARK_YUN_AGENT + "/submitWork";

    String GET_WORK_STATUS_URL = "/" + ModuleCode.SPARK_YUN_AGENT + "/getWorkStatus";

    String GET_WORK_DATA_URL = "/" + ModuleCode.SPARK_YUN_AGENT + "/getWorkData";

    String GET_WORK_STDOUT_LOG_URL = "/" + ModuleCode.SPARK_YUN_AGENT + "/getWorkStdoutLog";

    String GET_ALL_WORK_STDOUT_LOG_URL = "/" + ModuleCode.SPARK_YUN_AGENT + "/getAllWorkStdoutLog";

    String GET_CUSTOM_WORK_STDOUT_LOG_URL = "/" + ModuleCode.SPARK_YUN_AGENT + "/getCustomWorkStdoutLog";

    String GET_WORK_STDERR_LOG_URL = "/" + ModuleCode.SPARK_YUN_AGENT + "/getWorkStderrLog";

    String STOP_WORK_URL = "/" + ModuleCode.SPARK_YUN_AGENT + "/stopWork";

    String HEART_CHECK_URL = "/" + ModuleCode.SPARK_YUN_AGENT + "/heartCheck";

    String CONTAINER_CHECK_URL = "/" + ModuleCode.SPARK_YUN_AGENT + "/containerCheck";

    String EXECUTE_CONTAINER_SQL_URL = "/" + ModuleCode.SPARK_YUN_AGENT + "/executeContainerSql";

    String DEPLOY_CONTAINER_URL = "/" + ModuleCode.SPARK_YUN_AGENT + "/deployContainer";
}
