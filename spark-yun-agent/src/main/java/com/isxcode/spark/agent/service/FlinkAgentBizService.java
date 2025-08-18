package com.isxcode.spark.agent.service;

import com.isxcode.spark.agent.run.flink.AgentFactory;
import com.isxcode.spark.agent.run.flink.AgentService;
import com.isxcode.spark.api.agent.req.flink.GetWorkInfoReq;
import com.isxcode.spark.api.agent.req.flink.GetWorkLogReq;
import com.isxcode.spark.api.agent.req.flink.StopWorkReq;
import com.isxcode.spark.api.agent.req.flink.SubmitWorkReq;
import com.isxcode.spark.api.agent.res.flink.GetWorkInfoRes;
import com.isxcode.spark.api.agent.res.flink.GetWorkLogRes;
import com.isxcode.spark.api.agent.res.flink.StopWorkRes;
import com.isxcode.spark.api.agent.res.flink.SubmitWorkRes;
import com.isxcode.spark.backend.api.base.exceptions.IsxAppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlinkAgentBizService {

    private final AgentFactory agentFactory;

    public SubmitWorkRes submitWork(SubmitWorkReq submitWorkReq) {

        AgentService agentService = agentFactory.getAgentService(submitWorkReq.getClusterType());
        try {
            return agentService.submitWork(submitWorkReq);
        } catch (HttpClientErrorException httpClientErrorException) {
            log.error(httpClientErrorException.getMessage(), httpClientErrorException);
            if (HttpStatus.BAD_REQUEST.equals(httpClientErrorException.getStatusCode())) {
                String errStr = httpClientErrorException.getMessage().replace("400 Bad Request: \"{\"errors\":[\"", "");
                String substring = errStr.substring(0, errStr.length() - 4);
                throw new IsxAppException(substring);
            }
            if (HttpStatus.METHOD_NOT_ALLOWED.equals(httpClientErrorException.getStatusCode())) {
                String errStr =
                    httpClientErrorException.getMessage().replace("405 Method Not Allowed: \"{\"errors\":[\"", "");
                String substring = errStr.substring(0, errStr.length() - 4);
                throw new IsxAppException(substring);
            }
            throw new IsxAppException(httpClientErrorException.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    public GetWorkInfoRes getWorkInfo(GetWorkInfoReq getWorkInfoReq) {

        AgentService agentService = agentFactory.getAgentService(getWorkInfoReq.getClusterType());
        try {
            return agentService.getWorkInfo(getWorkInfoReq);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    public GetWorkLogRes getWorkLog(GetWorkLogReq getWorkLogReq) {

        AgentService agentService = agentFactory.getAgentService(getWorkLogReq.getClusterType());
        try {
            return agentService.getWorkLog(getWorkLogReq);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    public StopWorkRes stopWork(StopWorkReq stopWorkReq) {

        AgentService agentService = agentFactory.getAgentService(stopWorkReq.getClusterType());
        try {
            return agentService.stopWork(stopWorkReq);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }
}
