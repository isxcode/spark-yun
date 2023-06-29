package com.isxcode.star.backend.module.workflow.run;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/flow")
public class WorkflowRunController {

  private final ApplicationEventPublisher eventPublisher;

  public static final Map<String, String> workStatus = new HashMap<>();

  public static final Map<String, Thread> workThread = new HashMap<>();

  @GetMapping("/open/flowRun")
  public void flowRun() {

    Workflow workflow = new Workflow();
    workflow.setWorks(Arrays.asList(
      new Work("1", "内容1"),
      new Work("2", "内容2"),
      new Work("3", "内容3"),
      new Work("4", "内容4"),
      new Work("5", "内容5"),
      new Work("6", "内容6"),
      new Work("7", "内容7"),
      new Work("8", "内容8"),
      new Work("9", "内容9"),
      new Work("10", "内容10"),
      new Work("11", "内容11"),
      new Work("12", "内容12"),
      new Work("13", "内容13")
    ));
    workflow.setFlowConfig("[\n" +
      "        [\"1\",\"2\"],\n" +
      "        [\"1\",\"3\"],\n" +
      "        [\"2\",\"5\"],\n" +
      "        [\"2\",\"6\"],\n" +
      "        [\"3\",\"6\"],\n" +
      "        [\"3\",\"7\"],\n" +
      "        [\"4\",\"3\"],\n" +
      "        [\"5\",\"8\"],\n" +
      "        [\"5\",\"11\"],\n" +
      "        [\"6\",\"8\"],\n" +
      "        [\"7\",\"9\"],\n" +
      "        [\"8\",\"9\"],\n" +
      "        [\"8\",\"10\"],\n" +
      "        [\"13\",\"2\"],\n" +
      "        [\"9\",\"12\"]\n" +
      "]");

    workflow.getWorks().forEach(e -> workStatus.put(e.getId(), "PENDING"));

    run(workflow);
  }

  /**
   * 开始运行作业.
   */
  public void run(Workflow workflow) {

    // 解析flowConfig
    List<List<String>> flowList = WorkflowUtils.translateFlow(workflow.getFlowConfig());

    List<String> workIdList = workflow.getWorks().stream().map(Work::getId).collect(Collectors.toList());

    // 检查flow合法性
    WorkflowUtils.checkFlow(workIdList, flowList);

    // 获取起始节点
    List<String> startNodeIds = WorkflowUtils.getStartNodes(flowList, workIdList);

    // 找到起始节点
    List<Work> startNodes = workflow.getWorks().stream().filter(e -> startNodeIds.contains(e.getId())).collect(Collectors.toList());

    // 异步触发工作流
    startNodes.forEach(e -> {
      WorkEvent metaEvent = new WorkEvent(e, flowList, workflow.getWorks());
      eventPublisher.publishEvent(metaEvent);
    });
  }

  /**
   * kill 线程.
   */
  @GetMapping("/open/flowKill")
  public void flowKill(@RequestParam String name) {

    Thread thread = workThread.get(name);
    thread.interrupt();
  }

}
