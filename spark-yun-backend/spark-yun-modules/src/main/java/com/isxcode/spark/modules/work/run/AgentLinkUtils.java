package com.isxcode.spark.modules.work.run;

import com.alibaba.fastjson.JSON;
import com.isxcode.spark.api.main.properties.SparkYunProperties;
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

    private final SparkYunProperties sparkYunProperties;

    public AgentLinkResponse getAgentLinkResponse(ClusterNodeEntity agentNode, String url, Object body)
        throws WorkRunException {

        int retryCount = sparkYunProperties.getAgentRetryCount();
        long retryInterval = sparkYunProperties.getAgentRetryInterval();

        WorkRunException lastException = null;

        for (int attempt = 1; attempt <= retryCount; attempt++) {
            try {
                BaseResponse<?> baseResponse =
                    HttpUtils.doPost(httpUrlUtils.genHttpUrl(agentNode.getHost(), agentNode.getAgentPort(), url), body,
                        BaseResponse.class);

                // 打印调试日志
                if (baseResponse != null) {
                    log.debug("请求代理成功 : {}", JSON.toJSONString(baseResponse));
                } else {
                    throw new WorkRunException("代理请求中断");
                }

                if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())
                    || baseResponse.getData() == null) {
                    String errorMsg = baseResponse.getMsg();

                    // 检查是否需要重试
                    if (shouldRetry(errorMsg) && attempt < retryCount) {
                        log.warn("请求代理异常,第{}次重试 : {}", attempt, errorMsg);
                        lastException = new WorkRunException("请求代理异常 : " + errorMsg);
                        Thread.sleep(retryInterval);
                        continue;
                    }

                    throw new WorkRunException("请求代理异常 : " + errorMsg);
                }

                // 翻译成统一返回
                return JSON.parseObject(JSON.toJSONString(baseResponse.getData()), AgentLinkResponse.class);
            } catch (HttpServerErrorException e) {
                log.error("请求代理异常,第{}次尝试 : {}", attempt, e.getMessage(), e);

                if (HttpStatus.BAD_GATEWAY.value() == e.getRawStatusCode()) {
                    throw new WorkRunException("提交作业异常 : 无法访问节点服务器,请检查服务器防火墙或者计算集群");
                }

                // 检查是否需要重试
                if (shouldRetry(e.getMessage()) && attempt < retryCount) {
                    log.warn("检查集群状态异常,第{}次重试 : {}", attempt, e.getMessage());
                    lastException = new WorkRunException("检查集群状态 : " + e.getMessage());
                    try {
                        Thread.sleep(retryInterval);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new WorkRunException("重试被中断 : " + ie.getMessage());
                    }
                    continue;
                }

                throw new WorkRunException("检查集群状态 : " + e.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new WorkRunException("请求被中断 : " + e.getMessage());
            }
        }

        // 如果所有重试都失败,抛出最后一个异常
        if (lastException != null) {
            throw lastException;
        }

        throw new WorkRunException("请求代理失败,已重试" + retryCount + "次");
    }

    /**
     * 判断是否需要重试
     * @param errorMessage 错误信息
     * @return 是否需要重试
     */
    private boolean shouldRetry(String errorMessage) {
        if (errorMessage == null) {
            return false;
        }

        // 检查是否包含需要重试的关键字
        return errorMessage.contains("Couldn't retrieve standalone cluster")
            || errorMessage.contains("Connection refused") || errorMessage.contains("Connection timeout")
            || errorMessage.contains("Read timed out");
    }
}
