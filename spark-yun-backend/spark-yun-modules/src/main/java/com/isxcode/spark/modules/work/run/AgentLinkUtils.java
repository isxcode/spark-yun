package com.isxcode.spark.modules.work.run;

import com.alibaba.fastjson.JSON;
import com.isxcode.spark.api.work.res.AgentLinkResponse;
import com.isxcode.spark.backend.api.base.exceptions.WorkRunException;
import com.isxcode.spark.backend.api.base.pojos.BaseResponse;
import com.isxcode.spark.common.utils.http.HttpUrlUtils;
import com.isxcode.spark.common.utils.http.HttpUtils;
import com.isxcode.spark.modules.cluster.entity.ClusterNodeEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgentLinkUtils {

    private final HttpUrlUtils httpUrlUtils;

    public AgentLinkResponse getAgentLinkResponse(ClusterNodeEntity agentNode, String url, Object body)
        throws WorkRunException {

        try {
            BaseResponse<?> baseResponse = HttpUtils.doPost(
                httpUrlUtils.genHttpUrl(agentNode.getHost(), agentNode.getAgentPort(), url), body, BaseResponse.class);

            if (baseResponse != null) {
                log.debug("请求代理成功 : {}", JSON.toJSONString(baseResponse));
            }

            if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())
                || baseResponse.getData() == null) {
                throw new WorkRunException("请求代理异常 : " + baseResponse.getMsg());
            }

            return JSON.parseObject(JSON.toJSONString(baseResponse.getData()), AgentLinkResponse.class);
        } catch (HttpServerErrorException e) {
            log.error(e.getMessage(), e);

            if (HttpStatus.BAD_GATEWAY.value() == e.getRawStatusCode()) {
                throw new WorkRunException("提交作业异常 : 无法访问节点服务器,请检查服务器防火墙或者计算集群");
            }
            throw new WorkRunException("检查集群状态 : " + e.getMessage());
        }
    }
}
