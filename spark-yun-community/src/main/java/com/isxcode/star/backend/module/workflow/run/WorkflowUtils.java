package com.isxcode.star.backend.module.workflow.run;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.isxcode.star.api.exceptions.SparkYunException;
import org.apache.logging.log4j.util.Strings;
import org.jgrapht.Graph;
import org.jgrapht.alg.cycle.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 工作流工具类.
 */
public class WorkflowUtils {

  /**
   * 将数据库字段flowStr转成List<List<String>>结构.
   */
  public static List<List<String>> translateFlow(String flowStr) {

    List<List<String>> flowList = JSON.parseObject(flowStr, new TypeReference<List<List<String>>>() {
    });

    return flowList.stream().distinct().collect(Collectors.toList());
  }

  /**
   * 对工作流的flow进行检查.
   */
  public static void checkFlow(List<String> nodeIdList, List<List<String>> flowList) {

    // 校验节点是否有空
    flowList.forEach(e -> {
      if (Strings.isEmpty(e.get(0)) || Strings.isEmpty(e.get(1))) {
        throw new SparkYunException("工作流配置异常，节点有空值");
      }
    });

    // 校验闭环问题
    Graph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
    nodeIdList.forEach(graph::addVertex);
    flowList.forEach(e -> graph.addEdge(e.get(0), e.get(1)));
    CycleDetector<String, DefaultEdge> cycleDetector = new CycleDetector<>(graph);
    if (cycleDetector.detectCycles()) {
      throw new SparkYunException("工作流闭环了");
    }
  }

  /**
   * 获取当前节点的父级节点.
   */
  public static List<String> getParentNodes(List<List<String>> flowList, String nodeId) {

    List<String> nodes = flowList.stream().filter(e -> Objects.equals(e.get(1), nodeId)).map(e -> e.get(0)).collect(Collectors.toList());

    if (nodes.isEmpty()) {
      return Collections.singletonList("");
    }else{
      return nodes;
    }
  }

  /**
   * 获取当前节点的子级节点..
   */
  public static List<String> getSonNodes(List<List<String>> flowList, String nodeId) {

    List<String> nodes = flowList.stream().filter(e -> Objects.equals(e.get(0), nodeId)).map(e -> e.get(1)).collect(Collectors.toList());

    if (nodes.isEmpty()) {
      return Collections.singletonList("");
    }else{
      return nodes;
    }
  }

  /**
   * 获取工作流的所有开始节点.
   */
  public static List<String> getStartNodes(List<List<String>> flowList, List<String> nodeIdList) {

    List<String> sonNodes = flowList.stream()
      .map(sublist -> sublist.get(1))
      .collect(Collectors.toList());

    return nodeIdList.stream().filter(e -> !sonNodes.contains(e)).collect(Collectors.toList());
  }

  /**
   * 获取工作流的所有结束节点.
   */
  public static List<String> getEndNodes(List<List<String>> flowList, List<String> nodeIdList) {

    List<String> sonNodes = flowList.stream()
      .map(sublist -> sublist.get(0))
      .collect(Collectors.toList());

    return nodeIdList.stream().filter(e -> !sonNodes.contains(e)).collect(Collectors.toList());
  }

  /**
   * 从webConfig中解析出节点list.
   */
  public static List<String> parseNodeList(String webConfig) {


    // todo 节点解析重复报错

    return Arrays.asList(
      "sy_134879c1f4d44a22a18ea5e51b8b3609",
      "sy_312eae734d394cfaa74a859498c013bd",
      "sy_35f48b20f8c1433d87ef5206fd0d3e1d",
      "sy_4c12decb272945da97b0137e2e64e62e",
      "sy_4d61f91655e74b51992efa6a4e4ee488",
      "sy_623d9d2493d547d0ac14d2f35d84a77b",
      "sy_65fe0d29ec054af28a241fb17da357a2",
      "sy_691908fa93c1473cbf8ac8a047590176",
      "sy_831191f53e144efa9c7ba3bc5e475e33",
      "sy_a9f0fbafe00545c598f4c710ae52c56d",
      "sy_d71599fb12d54e33ae6d211027b15168",
      "sy_e3fd91cbbe714e9d9699b49a675540e4",
      "sy_fa862b0f4373402c89e5ac42e220609b"
      );
  }

  /**
   * 从webConfig中解析出节点映射关系.
   */
  public static List<List<String>> parseNodeMapping(String webConfig) {

    String flowConfig = "[\n" +
      "        [\"sy_e3fd91cbbe714e9d9699b49a675540e4\",\"sy_4c12decb272945da97b0137e2e64e62e\"],\n" +
      "        [\"sy_e3fd91cbbe714e9d9699b49a675540e4\",\"sy_4d61f91655e74b51992efa6a4e4ee488\"],\n" +
      "        [\"sy_4c12decb272945da97b0137e2e64e62e\",\"sy_623d9d2493d547d0ac14d2f35d84a77b\"],\n" +
      "        [\"sy_4c12decb272945da97b0137e2e64e62e\",\"sy_312eae734d394cfaa74a859498c013bd\"],\n" +
      "        [\"sy_4d61f91655e74b51992efa6a4e4ee488\",\"sy_312eae734d394cfaa74a859498c013bd\"],\n" +
      "        [\"sy_4d61f91655e74b51992efa6a4e4ee488\",\"sy_a9f0fbafe00545c598f4c710ae52c56d\"],\n" +
      "        [\"sy_35f48b20f8c1433d87ef5206fd0d3e1d\",\"sy_4d61f91655e74b51992efa6a4e4ee488\"],\n" +
      "        [\"sy_623d9d2493d547d0ac14d2f35d84a77b\",\"sy_134879c1f4d44a22a18ea5e51b8b3609\"],\n" +
      "        [\"sy_623d9d2493d547d0ac14d2f35d84a77b\",\"sy_d71599fb12d54e33ae6d211027b15168\"],\n" +
      "        [\"sy_312eae734d394cfaa74a859498c013bd\",\"sy_134879c1f4d44a22a18ea5e51b8b3609\"],\n" +
      "        [\"sy_a9f0fbafe00545c598f4c710ae52c56d\",\"sy_65fe0d29ec054af28a241fb17da357a2\"],\n" +
      "        [\"sy_a9f0fbafe00545c598f4c710ae52c56d\",\"sy_fa862b0f4373402c89e5ac42e220609b\"],\n" +
      "        [\"sy_134879c1f4d44a22a18ea5e51b8b3609\",\"sy_65fe0d29ec054af28a241fb17da357a2\"],\n" +
      "        [\"sy_134879c1f4d44a22a18ea5e51b8b3609\",\"sy_831191f53e144efa9c7ba3bc5e475e33\"],\n" +
      "        [\"sy_65fe0d29ec054af28a241fb17da357a2\",\"sy_691908fa93c1473cbf8ac8a047590176\"]\n" +
      "]";

    return JSON.parseObject(flowConfig, new TypeReference<List<List<String>>>() {
    });
  }


}

