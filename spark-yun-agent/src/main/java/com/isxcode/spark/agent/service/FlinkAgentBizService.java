package com.isxcode.spark.agent.service;

import com.isxcode.spark.agent.run.flink.FlinkAgentFactory;
import com.isxcode.spark.agent.run.flink.FlinkAgentService;
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

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlinkAgentBizService {

    private final FlinkAgentFactory agentFactory;

    public SubmitWorkRes submitWork(SubmitWorkReq submitWorkReq) {

        try {
            FlinkAgentService agentService = agentFactory.getAgentService(submitWorkReq.getClusterType());
            return agentService.submitWork(submitWorkReq);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(translateExceptionMsg(e));
        }
    }

    public GetWorkInfoRes getWorkInfo(GetWorkInfoReq getWorkInfoReq) {

        try {
            FlinkAgentService agentService = agentFactory.getAgentService(getWorkInfoReq.getClusterType());
            return agentService.getWorkInfo(getWorkInfoReq);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    public GetWorkLogRes getWorkLog(GetWorkLogReq getWorkLogReq) {

        try {
            FlinkAgentService agentService = agentFactory.getAgentService(getWorkLogReq.getClusterType());
            return agentService.getWorkLog(getWorkLogReq);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    public StopWorkRes stopWork(StopWorkReq stopWorkReq) {

        try {
            FlinkAgentService agentService = agentFactory.getAgentService(stopWorkReq.getClusterType());
            return agentService.stopWork(stopWorkReq);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    public static String translateExceptionMsg(Exception e) {

        if (e instanceof HttpClientErrorException) {
            HttpClientErrorException httpClientErrorException = (HttpClientErrorException) e;
            if (HttpStatus.BAD_REQUEST.equals(httpClientErrorException.getStatusCode())) {
                String errStr = Objects.requireNonNull(httpClientErrorException.getMessage())
                    .replace("400 Bad Request: \"{\"errors\":[\"", "");
                return errStr.substring(0, errStr.length() - 4);
            }
            if (HttpStatus.METHOD_NOT_ALLOWED.equals(httpClientErrorException.getStatusCode())) {
                String errStr = Objects.requireNonNull(httpClientErrorException.getMessage())
                    .replace("405 Method Not Allowed: \"{\"errors\":[\"", "");
                return errStr.substring(0, errStr.length() - 4);
            }
        }

        return e.getMessage();
    }
}
