package com.test.logs;


import com.alibaba.fastjson.JSON;
import com.test.entity.OperationLog;
import com.test.repo.ExceptionLogRepo;
import com.test.repo.OperationLogRepo;
import com.test.util.IpUtils;
import com.test.util.StringUtils;
import com.test.util.UserAgentUtils;
import eu.bitwalker.useragentutils.UserAgent;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;

import org.springframework.context.annotation.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;


/**
 * 日志切点
 * @author anonymity
 * @create 2018-07-24 18:33
 **/
@Aspect
@Configuration
public class LogAspect {

    @Resource
    private OperationLogRepo operationLogRepo;
    @Resource
    private ExceptionLogRepo exceptionLogRepo;

    /**
     * 本地异常日志记录对象
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Service层切点
     */
    @Pointcut("@annotation(com.test.logs.ServiceLogs)")
    public void serviceAspect() {
    }

    /**
     * Controller层切点
     */
    @Pointcut("@annotation(com.test.logs.ControllerLogs)")
    public void controllerAspect() {}

    /**
     * 前置通知 用于拦截Controller层记录用户的操作
     * @param joinPoint 切点
     */
    @Before("controllerAspect()")
    public void doBefore(JoinPoint joinPoint){
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            // 类名
            String className = joinPoint.getTarget().getClass().getName();
            // 用户信息
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                /**
                 * 例如：我们生成token时是这样的规则
                 *
                 *
                 * public String generate(User user) {
                        return new DefaultJwtBuilder().
                        setId(UUID.randomUUID().toString()).
                        setSubject(user.getId().toString()).
                        setExpiration(Date.from(ZonedDateTime.now().plusWeeks(1).toInstant()))
                                    .claim("id", user.getId())
                                    .claim("email", user.getEmail())
                                    .claim("loginname",user.getLoginname()).
                                    signWith(SignatureAlgorithm.ES256, privateKey).compact();
                   }
                 */
                /**
                 * 那么我们从token中获取用户信息的时候就可以这么获取
                 * Jws<Claims> jws = new DefaultJwtParser()
                                    .setSigningKey(publicKey)
                                    .parseClaimsJws(token);
                    Claims claims = jws.getBody();
                    // 这个id是生成token时传入的
                    Integer id = (Integer) claims.get("id");
                 */

            }
            // 请求方法
            String method =  joinPoint.getSignature().getName() + "()";
            // 方法参数
            String methodParam = JSON.toJSONString(joinPoint.getArgs());
            // 方法描述
            String methodDescription = getControllerMethodDescription(joinPoint);
            StringBuilder sb = new StringBuilder(1000);
            sb.append("\n");
            sb.append("*********************************Request请求***************************************");
            sb.append("\n");
//            sb.append("userId        :  ").append(userId).append("\n");
            sb.append("ClassName     :  ").append(className).append("\n");
            sb.append("RequestMethod :  ").append(method).append("\n");
            sb.append("RequestParams :  ").append(methodParam).append("\n");
            sb.append("RequestType   :  ").append(request.getMethod()).append("\n");
            sb.append("Description   :  ").append(methodDescription).append("\n");
            sb.append("serverAddr    :  ").append(request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()).append("\n");
            sb.append("RemoteAddr    :  ").append(IpUtils.getRemoteAddr(request)).append("\n");
            UserAgent userAgent = UserAgentUtils.getUserAgent(request);
            sb.append("DeviceName    :  ").append(userAgent.getOperatingSystem().getName()).append("\n");
            sb.append("BrowserName   :  ").append(userAgent.getBrowser().getName()).append("\n");
            sb.append("UserAgent     :  ").append(request.getHeader("User-Agent")).append("\n");
            sb.append("RequestUri    :  ").append(StringUtils.abbr(request.getRequestURI(), 255)).append("\n");
            logger.info(sb.toString());
            // save db
            OperationLog operationLog = new OperationLog(null, className,method, methodParam,request.getMethod(),methodDescription,
                    request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort(),IpUtils.getRemoteAddr(request),
                    userAgent.getOperatingSystem().getName(), userAgent.getBrowser().getName(),request.getHeader("User-Agent"),
                    StringUtils.abbr(request.getRequestURI(), 255));
            operationLogRepo.save(operationLog);
        }catch (Exception e){
            logger.error("doBefore failed", e);
        }
    }

    @AfterReturning(returning = "ret", pointcut = "controllerAspect()")
    public void doAfterReturning(Object ret) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //请求方法
        String method = StringUtils.abbr(request.getRequestURI(), 255);
        StringBuilder sb = new StringBuilder(1000);
        // 处理完请求，返回内容
        sb.append("\n");
        sb.append("Result        :  ").append(ret);
        logger.info(sb.toString());
    }

    /**
     * 异常通知 用于拦截service层记录异常日志
     */
    @AfterThrowing(pointcut = "serviceAspect()", throwing = "ex")
    public void doAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            // 类名
            String className = joinPoint.getTarget().getClass().getName();
            // 请求方法
            String method =  (joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + "()");
            // 方法参数
            String methodParam = Arrays.toString(joinPoint.getArgs());
            // 方法描述
            String methodDescription = getServiceMthodDescription(joinPoint);
            // 获取用户请求方法的参数并序列化为JSON格式字符串
            String params = JSON.toJSONString(joinPoint.getArgs());
//            String params = "";
//            if (joinPoint.getArgs() != null && joinPoint.getArgs().length > 0) {
//                for (int i = 0; i < joinPoint.getArgs().length; i++) {
//                    params += JSON.toJSONString(joinPoint.getArgs()[i]) + ";";
//                }
//            }
            StringBuilder sb = new StringBuilder(1000);
            sb.append("\n");
            sb.append("*********************************Service异常***************************************");
            sb.append("\n");
            sb.append("ClassName        :  ").append(className).append("\n");
            sb.append("Method           :  ").append(method).append("\n");
            sb.append("Params           :  ").append("[" + params + "]").append("\n");
            sb.append("Description      :  ").append(methodDescription).append("\n");
            sb.append("ExceptionName    :  ").append(ex.getClass().getName()).append("\n");
            sb.append("ExceptionMessage :  ").append(ex.getMessage()).append("\n");
            logger.info(sb.toString());

            // save db 和 doBefore 中一样，这个是ExceptionLog表
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    /**
     * 获取注解中对方法的描述信息 用于service层注解
     */
    public static String getServiceMthodDescription(JoinPoint joinPoint)
            throws Exception {
        String targetName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();
        Class targetClass = Class.forName(targetName);
        Method[] methods = targetClass.getMethods();
        String description = "";
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class[] clazzs = method.getParameterTypes();
                if (clazzs.length == arguments.length) {
                    description = method.getAnnotation(ServiceLogs.class).description();
                    break;
                }
            }
        }
        return description;
    }

    /**
     * 获取注解中对方法的描述信息 用于Controller层注解
     */
    public static String getControllerMethodDescription(JoinPoint joinpoint) throws Exception {
        String targetName = joinpoint.getTarget().getClass().getName();
        String methodName = joinpoint.getSignature().getName();
        Object[] arguments = joinpoint.getArgs();
        Class targetClass = Class.forName(targetName);
        Method[] methods = targetClass.getMethods();
        String description = "";
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class[] clazzs = method.getParameterTypes();
                if (clazzs.length == arguments.length) {
                    description = method.getAnnotation(ControllerLogs.class).description();
                    break;
                }
            }
        }
        return description;
    }

}
