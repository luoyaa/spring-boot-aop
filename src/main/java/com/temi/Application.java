package com.temi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 记录用户的操作日志和异常日志
 *
 * 用户信息从token中获取
 * @author anonymity
 * @create 2018-07-25 11:18
 **/
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
