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

    return Arrays.asList(
      "sy_e3fd91cbbe714e9d9699b49a675540e4",
      "sy_4c12decb272945da97b0137e2e64e62e",
      "sy_4d61f91655e74b51992efa6a4e4ee488"
    );
  }

  /**
   * 从webConfig中解析出节点映射关系.
   */
  public static List<List<String>> parseNodeMapping(String webConfig) {

    List<List<String>> nodeMapping = new ArrayList<>();

    List<String> node1 = new ArrayList<>();
    node1.add("sy_e3fd91cbbe714e9d9699b49a675540e4");
    node1.add("sy_4c12decb272945da97b0137e2e64e62e");
    nodeMapping.add(node1);

    List<String> node2 = new ArrayList<>();
    node2.add("sy_4c12decb272945da97b0137e2e64e62e");
    node2.add("sy_4d61f91655e74b51992efa6a4e4ee488");
    nodeMapping.add(node2);

    return nodeMapping;
  }


}

