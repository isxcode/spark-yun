package com.isxcode.spark.agent.run.flink;


import com.isxcode.spark.api.agent.req.flink.GetWorkInfoReq;
import com.isxcode.spark.api.agent.req.flink.GetWorkLogReq;
import com.isxcode.spark.api.agent.req.flink.StopWorkReq;
import com.isxcode.spark.api.agent.req.flink.SubmitWorkReq;
import com.isxcode.spark.api.agent.res.flink.GetWorkInfoRes;
import com.isxcode.spark.api.agent.res.flink.GetWorkLogRes;
import com.isxcode.spark.api.agent.res.flink.StopWorkRes;
import com.isxcode.spark.api.agent.res.flink.SubmitWorkRes;

public interface FlinkAgentService {

    String getAgentType();

    SubmitWorkRes submitWork(SubmitWorkReq submitWorkReq) throws Exception;

    GetWorkInfoRes getWorkInfo(GetWorkInfoReq getWorkInfoReq) throws Exception;

    GetWorkLogRes getWorkLog(GetWorkLogReq getWorkLogReq) throws Exception;

    StopWorkRes stopWork(StopWorkReq stopWorkReq) throws Exception;
}
