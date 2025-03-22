package com.isxcode.star;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/")
@SpringBootApplication
public class SparkYunApplication {

    public static void main(String[] args) {

        SpringApplication.run(SparkYunApplication.class, args);
    }

    @RequestMapping(value = {"/*", "/home/**", "/share/**", "/dashboard/**", "/ssoauth/**"})
    public String index() {

        return "index";
    }
}
