package com.isxcode.spark.agent.run.flink;


import com.isxcode.spark.api.agent.req.flink.GetWorkInfoReq;
import com.isxcode.spark.api.agent.req.flink.GetWorkLogReq;
import com.isxcode.spark.api.agent.req.flink.StopWorkReq;
import com.isxcode.spark.api.agent.req.flink.SubmitWorkReq;
import com.isxcode.spark.api.agent.res.flink.GetWorkInfoRes;
import com.isxcode.spark.api.agent.res.flink.GetWorkLogRes;
import com.isxcode.spark.api.agent.res.flink.StopWorkRes;
import com.isxcode.spark.api.agent.res.flink.SubmitWorkRes;

public interface AgentService {

    /**
     * 获取当前代理的类型
     */
    String getAgentType();

    /**
     * 提交flink作业
     */
    SubmitWorkRes submitWork(SubmitWorkReq submitWorkReq) throws Exception;

    /**
     * 获取作业信息
     */
    GetWorkInfoRes getWorkInfo(GetWorkInfoReq getWorkInfoReq) throws Exception;

    /**
     * 获取作业日志
     */
    GetWorkLogRes getWorkLog(GetWorkLogReq getWorkLogReq) throws Exception;

    /**
     * 中止作业
     */
    StopWorkRes stopWork(StopWorkReq stopWorkReq) throws Exception;
}
