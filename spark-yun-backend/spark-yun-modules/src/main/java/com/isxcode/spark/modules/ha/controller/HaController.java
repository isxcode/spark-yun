package com.isxcode.spark.modules.ha.controller;


import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.isxcode.spark.modules.work.run.WorkExecutor.WORK_THREAD;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ha")
@Slf4j
public class HaController {

    @Operation(summary = "中止作业进程")
    @GetMapping("/open/kill")
    public void kill(@RequestParam String workEventId) {

        Thread thread = WORK_THREAD.get(workEventId);
        if (thread != null) {
            thread.interrupt();
        }
    }
}
