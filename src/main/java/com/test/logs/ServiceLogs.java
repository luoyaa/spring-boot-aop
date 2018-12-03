package com.test.logs;

import java.lang.annotation.*;

/**
 * Service 日志记录
 *
 * @author anonymity
 * @create 2018-07-24 18:30
 **/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ServiceLogs {
    /**
     * 描述
     */
    String description() default "";
}
