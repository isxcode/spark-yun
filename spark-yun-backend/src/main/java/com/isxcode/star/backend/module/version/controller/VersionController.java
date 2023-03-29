package com.isxcode.star.backend.module.version.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class VersionController {

    @GetMapping("/getVersion")
    public String getVersion() {

        return "v0.0.1";
    }
}
