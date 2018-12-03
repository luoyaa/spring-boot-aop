package com.test.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @author anonymity
 * @create 2018-07-25 13:26
 **/
@Data
@Entity
@Table(name = "exception_log")
public class ExceptionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String className;
    private String method;
    /**
     * 如果是post请求的时候参数很多，存入数据库就会占用很大空间，
     * spring data jpa 自动生成的varchar默认长度是不够的，我们要修改数据库的类型为longtext才可以，
     * 也可以使用注解来代码指定类型
     */
    private String params;
    private String description;
    private String exceptionName;
    private String exceptionMessage;
}
