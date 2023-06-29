package com.isxcode.star.backend.module.workflow.run;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.isxcode.star.backend.module.workflow.run.WorkflowRunController.workStatus;
import static com.isxcode.star.backend.module.workflow.run.WorkflowRunController.workThread;

@Component
@RequiredArgsConstructor
public class WorkEventListener {

  private final ApplicationEventPublisher eventPublisher;

  @EventListener
  @Async("springEventThreadPool")
  public void onApplicationEvent(WorkEvent event) {

    // 修改状态前都要加锁，给实例加锁
    synchronized (event.getWork()) {

      // 跑过了，不可以再跑
      String status = workStatus.get(event.getWork().getId());
      if (!"PENDING".equals(status)) {

        // 任务开始执行
        System.out.println(event.getWork().getContent() + ":     " + LocalTime.now() + "推送未执行");
        return;
      }

      // 上面不成功，异步推送，一个不成功则全部不成功，全部中止。
      List<String> parentNodes = WorkflowUtils.getParentNodes(event.getFlowList(), event.getWork().getId());
      if (parentNodes.stream().map(e -> "FINISHED".equals(workStatus.get(e))).anyMatch(e -> !e)) {

        // 任务开始执行
        System.out.println(event.getWork().getContent() + ":     " + LocalTime.now() + "推送未执行");

        return;
      }

      // 更改状态开始运行
      workStatus.put(event.getWork().getId(), "RUNNING");
    }

    workThread.put(event.getWork().getContent(), Thread.currentThread());

    // 任务开始执行
    System.out.println(event.getWork().getContent() + ":     " + LocalTime.now());

    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    workStatus.put(event.getWork().getId(), "FINISHED");

    // 执行完后推送子节点
    List<String> sonNodeIds = WorkflowUtils.getSonNodes(event.getFlowList(), event.getWork().getId());
    List<Work> sonNodes = event.getWorks().stream().filter(e -> sonNodeIds.contains(e.getId())).collect(Collectors.toList());
    sonNodes.forEach(e -> {
      WorkEvent metaEvent = new WorkEvent(e, event.getFlowList(), event.getWorks());
      eventPublisher.publishEvent(metaEvent);
    });
  }
}
