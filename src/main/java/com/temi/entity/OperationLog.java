package com.temi.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @author anonymity
 * @create 2018-07-25 13:19
 **/
@Data
@Entity
@Table(name = "operation_log")
public class OperationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long userId;
    private String className;
    private String requestMethod;
    /**
     * 如果是post请求的时候参数很多，存入数据库就会占用很大空间，
     * spring data jpa 自动生成的varchar默认长度是不够的，我们要修改数据库的类型为longtext才可以
     * 也可以使用注解来代码指定类型
     */
    private String requestParams;
    private String requestType;
    private String description;
    private String serverAddress;
    private String remoteAddress;
    private String deviceName;
    private String browserName;
    private String userAgent;
    private String requestUri;

    public OperationLog() {
    }

    public OperationLog(Long userId, String className, String requestMethod, String requestParams, String requestType, String description, String serverAddress, String remoteAddress, String deviceName, String browserName, String userAgent, String requestUri) {
        this.userId = userId;
        this.className = className;
        this.requestMethod = requestMethod;
        this.requestParams = requestParams;
        this.requestType = requestType;
        this.description = description;
        this.serverAddress = serverAddress;
        this.remoteAddress = remoteAddress;
        this.deviceName = deviceName;
        this.browserName = browserName;
        this.userAgent = userAgent;
        this.requestUri = requestUri;
    }
}
