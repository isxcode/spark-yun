package com.isxcode.star.backend.module.workflow.run;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class SyncService {

  @Async("springEventThreadPool")
  public void test(Integer i) {

    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

}
