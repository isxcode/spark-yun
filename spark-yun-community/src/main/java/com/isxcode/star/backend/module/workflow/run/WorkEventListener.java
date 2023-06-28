package com.isxcode.star.backend.module.workflow.run;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.isxcode.star.backend.module.workflow.run.WorkflowRunController.workStatus;

@Component
@RequiredArgsConstructor
public class WorkEventListener {

  private final ApplicationEventPublisher eventPublisher;

  @EventListener
  public void onApplicationEvent(WorkEvent event) {

    // 修改状态加锁
    synchronized (this) {
      // 如果当前状态已执行，直接return
      String status = workStatus.get(event.getWork().getId());
      if (!"PENDING".equals(status)) {
        return;
      }

      // 判断父级作业是否都运行完，没有运行完，则直接return
      List<String> parentNodes = WorkflowUtils.getParentNodes(event.getFlowList(), event.getWork().getId());
      if (parentNodes.stream().map(e -> "FINISHED".equals(workStatus.get(e))).anyMatch(e -> !e)) {
        return;
      }

      // 修改任务状态,上面程序运行的太快，还更新PENDING状态，就直接运行了
      workStatus.put(event.getWork().getId(), "RUNNING");
    }

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    // 任务开始执行
    System.out.println(event.getWork().getContent() + ":" + LocalTime.now());

    workStatus.put(event.getWork().getId(), "FINISHED");

    // 执行完后推送子节点
    List<String> sonNodeIds = WorkflowUtils.getSonNodes(event.getFlowList(), event.getWork().getId());
    List<Work> sonNodes = event.getWorks().stream().filter(e -> sonNodeIds.contains(e.getId())).collect(Collectors.toList());
    sonNodes.forEach(e -> new Thread(() -> {
      WorkEvent metaEvent = new WorkEvent(e, event.getFlowList(), event.getWorks());
      eventPublisher.publishEvent(metaEvent);
    }).start());
  }
}
