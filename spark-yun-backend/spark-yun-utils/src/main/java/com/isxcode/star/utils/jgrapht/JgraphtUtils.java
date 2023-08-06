package com.isxcode.star.utils.jgrapht;

import com.isxcode.star.backend.api.base.exceptions.SparkYunException;
import java.util.List;
import org.jgrapht.Graph;
import org.jgrapht.alg.cycle.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public class JgraphtUtils {

  public static void isCycle(List<String> nodeIdList, List<List<String>> flowList) {

    Graph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
    nodeIdList.forEach(graph::addVertex);
    flowList.forEach(e -> graph.addEdge(e.get(0), e.get(1)));
    CycleDetector<String, DefaultEdge> cycleDetector = new CycleDetector<>(graph);
    if (cycleDetector.detectCycles()) {
      throw new SparkYunException("工作流闭环了");
    }
  }
}
