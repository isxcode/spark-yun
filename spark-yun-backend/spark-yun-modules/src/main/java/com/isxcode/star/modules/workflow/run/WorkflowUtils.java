package com.isxcode.star.modules.workflow.run;

import static com.isxcode.star.common.config.CommonConfig.TENANT_ID;
import static com.isxcode.star.common.config.CommonConfig.USER_ID;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.isxcode.star.api.work.dto.*;
import com.isxcode.star.api.workflow.dto.NodeInfo;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.common.utils.jgrapht.JgraphtUtils;
import com.isxcode.star.modules.work.entity.VipWorkVersionEntity;
import com.isxcode.star.modules.work.entity.WorkConfigEntity;
import com.isxcode.star.modules.work.entity.WorkEntity;
import com.isxcode.star.modules.work.run.WorkRunContext;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.logging.log4j.util.Strings;

public class WorkflowUtils {

    /**
     * 将数据库字段flowStr转成List<List<String>>结构.
     */
    public static List<List<String>> translateFlow(String flowStr) {

        List<List<String>> flowList = JSON.parseObject(flowStr, new TypeReference<List<List<String>>>() {});

        return flowList.stream().distinct().collect(Collectors.toList());
    }

    /**
     * 对工作流的flow进行检查.
     */
    public static void checkFlow(List<String> nodeIdList, List<List<String>> flowList) {

        // 校验节点是否有空
        flowList.forEach(e -> {
            if (Strings.isEmpty(e.get(0)) || Strings.isEmpty(e.get(1))) {
                throw new IsxAppException("工作流配置异常，节点有空值");
            }
        });

        // 校验闭环问题
        JgraphtUtils.isCycle(nodeIdList, flowList);
    }

    /**
     * 获取当前节点的父级节点.
     */
    public static List<String> getParentNodes(List<List<String>> flowList, String nodeId) {

        List<String> nodes = flowList.stream().filter(e -> Objects.equals(e.get(1), nodeId)).map(e -> e.get(0))
            .collect(Collectors.toList());

        if (nodes.isEmpty()) {
            return Collections.singletonList("");
        } else {
            return nodes;
        }
    }

    /**
     * 获取当前节点的子级节点..
     */
    public static List<String> getSonNodes(List<List<String>> flowList, String nodeId) {

        List<String> nodes = flowList.stream().filter(e -> Objects.equals(e.get(0), nodeId)).map(e -> e.get(1))
            .collect(Collectors.toList());

        if (nodes.isEmpty()) {
            return Collections.singletonList("");
        } else {
            return nodes;
        }
    }

    /**
     * 获取工作流的所有开始节点.
     */
    public static List<String> getStartNodes(List<List<String>> flowList, List<String> nodeIdList) {

        List<String> sonNodes = flowList.stream().map(sublist -> sublist.get(1)).collect(Collectors.toList());

        return nodeIdList.stream().filter(e -> !sonNodes.contains(e)).collect(Collectors.toList());
    }

    /**
     * 获取工作流的所有结束节点.
     */
    public static List<String> getEndNodes(List<List<String>> flowList, List<String> nodeIdList) {

        List<String> sonNodes = flowList.stream().map(sublist -> sublist.get(0)).collect(Collectors.toList());

        return nodeIdList.stream().filter(e -> !sonNodes.contains(e)).collect(Collectors.toList());
    }

    /**
     * 从webConfig中解析出节点list.
     */
    public static List<String> parseNodeList(String webConfig) {

        List<NodeInfo> nodeInfos = JSONArray.parseArray(webConfig, NodeInfo.class);

        return nodeInfos.stream().filter(e -> "dag-node".equals(e.getShape())).map(NodeInfo::getId)
            .collect(Collectors.toList());
    }

    /**
     * 从webConfig中解析出节点映射关系.
     */
    public static List<List<String>> parseNodeMapping(String webConfig) {

        List<NodeInfo> nodeInfos = JSONArray.parseArray(webConfig, NodeInfo.class);

        nodeInfos = nodeInfos.stream().filter(e -> "dag-edge".equals(e.getShape())).collect(Collectors.toList());

        List<List<String>> nodeMapping = new ArrayList<>();
        for (NodeInfo node : nodeInfos) {

            List<String> metaEdge = new ArrayList<>();
            metaEdge.add(node.getSource().getCell());
            metaEdge.add(node.getTarget().getCell());
            nodeMapping.add(metaEdge);
        }

        return nodeMapping;
    }

    public static WorkRunContext genWorkRunContext(String instanceId, WorkEntity work, WorkConfigEntity workConfig) {

        return WorkRunContext.builder().datasourceId(workConfig.getDatasourceId()).script(workConfig.getScript())
            .instanceId(instanceId).tenantId(TENANT_ID.get())
            .clusterConfig(JSON.parseObject(workConfig.getClusterConfig(), ClusterConfig.class))
            .syncWorkConfig(JSON.parseObject(workConfig.getSyncWorkConfig(), SyncWorkConfig.class))
            .excelSyncConfig(JSON.parseObject(workConfig.getExcelSyncConfig(), ExcelSyncConfig.class))
            .syncRule(JSON.parseObject(workConfig.getSyncRule(), SyncRule.class)).workType(work.getWorkType())
            .jarJobConfig(JSON.parseObject(workConfig.getJarJobConfig(), JarJobConfig.class))
            .funcConfig(JSON.parseArray(workConfig.getFuncConfig(), String.class))
            .libConfig(JSON.parseArray(workConfig.getLibConfig(), String.class)).workId(work.getId())
            .apiWorkConfig(JSON.parseObject(workConfig.getApiWorkConfig(), ApiWorkConfig.class))
            .containerId(workConfig.getContainerId()).workName(work.getName()).userId(USER_ID.get()).build();
    }

    public static WorkRunContext genWorkRunContext(String instanceId, VipWorkVersionEntity workVersion,
        WorkflowRunEvent event) {
        return WorkRunContext.builder().datasourceId(workVersion.getDatasourceId()).script(workVersion.getScript())
            .instanceId(instanceId).tenantId(TENANT_ID.get()).userId(USER_ID.get())
            .syncWorkConfig(JSON.parseObject(workVersion.getSyncWorkConfig(), SyncWorkConfig.class))
            .excelSyncConfig(JSON.parseObject(workVersion.getExcelSyncConfig(), ExcelSyncConfig.class))
            .syncRule(JSON.parseObject(workVersion.getSyncRule(), SyncRule.class))
            .clusterConfig(JSON.parseObject(workVersion.getClusterConfig(), ClusterConfig.class))
            .jarJobConfig(JSON.parseObject(workVersion.getJarJobConfig(), JarJobConfig.class))
            .funcConfig(JSON.parseArray(workVersion.getFuncConfig(), String.class))
            .libConfig(JSON.parseArray(workVersion.getLibConfig(), String.class))
            .containerId(workVersion.getContainerId()).workType(workVersion.getWorkType())
            .apiWorkConfig(JSON.parseObject(workVersion.getApiWorkConfig(), ApiWorkConfig.class))
            .workName(event.getWorkName()).workId(workVersion.getId()).build();
    }

    public static WorkRunContext genWorkRunContext(VipWorkVersionEntity workVersion) {

        return WorkRunContext.builder().workId(workVersion.getWorkId())
            .clusterConfig(JSON.parseObject(workVersion.getClusterConfig(), ClusterConfig.class))
            .datasourceId(workVersion.getDatasourceId()).userId(USER_ID.get()).tenantId(TENANT_ID.get())
            .script(workVersion.getScript()).workType(workVersion.getWorkType())
            .syncRule(JSON.parseObject(workVersion.getSyncRule(), SyncRule.class)).versionId(workVersion.getId())
            .build();
    }

    /**
     * 获取所有下游的节点id.
     */
    public static List<String> parseAfterNodes(List<List<String>> webConfig, String workId) {

        List<String> afterNodes = new ArrayList<>();
        afterNodes.add(workId);
        recursionAfterNode(afterNodes, webConfig, workId);
        return afterNodes.stream().distinct().collect(Collectors.toList());
    }

    public static void recursionAfterNode(List<String> result, List<List<String>> webConfig, String currentWorkId) {

        webConfig.forEach(e -> {
            if (currentWorkId.equals(e.get(0))) {
                result.add(e.get(1));
                recursionAfterNode(result, webConfig, e.get(1));
            }
        });
    }
}
