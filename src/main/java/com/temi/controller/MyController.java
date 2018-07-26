package com.temi.controller;

import com.temi.logs.ControllerLogs;
import com.temi.logs.ServiceLogs;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author anonymity
 * @create 2018-07-25 11:21
 **/
@RestController
@RequestMapping()
public class MyController {

    @GetMapping("/{userId}")
    @ControllerLogs(description = "index页面")
    public String index(){
        return "010-110";
    }

    @GetMapping("/log")
    @ServiceLogs(description = "异常捕获")
    public String exLog() {
        int a = 0 / 0;
        return "010-120";
    }
}
