package com.isxcode.spark.modules.test.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
@Slf4j
public class TestController {

    @Operation(summary = "本地调试")
    @GetMapping("/open/test")
    public void test() {

        System.out.println("本地调试");
    }
}
