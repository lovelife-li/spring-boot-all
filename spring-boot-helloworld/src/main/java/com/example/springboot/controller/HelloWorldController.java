package com.example.springboot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ldb
 * @date 2020/05/20
 * @description 控制器
 */
@RestController
public class HelloWorldController {

    @GetMapping("/hello")
    public String hello(){
        return "hello,world";
    }
}
