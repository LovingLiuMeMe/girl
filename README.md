### 1.表单验证
controller
```java
    @PostMapping(value = "/girls")
    /**
     * @Desc 添加@Valid 表示验证的时对象,并将结果返回到 bindingResult
     * @Author LovingLiu
    */
    public Girl girlAdd(@Valid Girl girl, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            System.out.println(bindingResult.getFieldError().getDefaultMessage());
            return null;
        }
        return girlRepository.save(girl);
    }
```
domain
```java
package cn.lovingliu.girl.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Min;

/**
 * Created by 廖师兄
 * 2016-11-03 23:07
 */
@Entity
public class Girl {

    @Id
    @GeneratedValue
    private Integer id;

    private String cupSize;
    /**
     * @Desc 保存girl时 拦截age属性<18的 请求
     * @Author LovingLiu
    */
    @Min(value = 18, message = "未成年少女禁止入内")
    private Integer age;

    public Girl() {
    }
    /**
     * @Desc 省略get/set 方法
     * @Author LovingLiu
    */
}

```

### 2.认识AOP
1.pom.xml
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```
2.HttpAspect(常规写法)
```java
package cn.lovingliu.girl.aspect;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Author：LovingLiu
 * @Description: 登陆的权限校验
 * @Date：Created in 2019-09-16
 */
@Aspect
@Component
public class HttpAspect {
    private static final Logger logger = LoggerFactory.getLogger(HttpAspect.class);

    /**
     * @Desc 1.不定义切点
     * @Author LovingLiu
    */
    @Before("execution(public * cn.lovingliu.girl.controller.GirlController.*(..))")
    public void systemBefore(){
        System.out.println("systemBefore");
    }

    @After("execution(public * cn.lovingliu.girl.controller.GirlController.*(..))")
    public void systemAfter(){
        System.out.println("systemAfter");
    }
}
```
3.HttpAspect(定义切点)
```java
package cn.lovingliu.girl.aspect;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Author：LovingLiu
 * @Description: 登陆的权限校验
 * @Date：Created in 2019-09-16
 */
@Aspect
@Component
public class HttpAspect {
    private static final Logger logger = LoggerFactory.getLogger(HttpAspect.class);
    /**
     * @Desc 2.定义切点
     * @Author LovingLiu
    */
    @Pointcut("execution(public * cn.lovingliu.girl.controller.GirlController.*(..))")
    public void log(){
    }

    @Before("log()")
    public void logBefore(){
        logger.info("+++++++++++++++");
    }

    @After("log()")
    public void logAfter(){
        logger.info("---------------");
    }

}
```
### 3.AOP 实现日志的打印
```java
package cn.lovingliu.girl.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author：LovingLiu
 * @Description: 登陆的权限校验
 * @Date：Created in 2019-09-16
 */
@Aspect
@Component
public class HttpAspect {
    private static final Logger logger = LoggerFactory.getLogger(HttpAspect.class);

    /**
     * @Desc 2.定义切点
     * @Author LovingLiu
    */
    @Pointcut("execution(public * cn.lovingliu.girl.controller.GirlController.*(..))")
    public void log(){
    }

    @Before("log()")
    public void logBefore(JoinPoint joinPoint){
        ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        //url
        logger.info("url={}",request.getRequestURL());
        //method
        logger.info("method={}",request.getMethod());
        //ip
        logger.info("ip={}",request.getRemoteAddr());
        //类方法
        logger.info("class_method",joinPoint.getSignature().getDeclaringTypeName()+"."+joinPoint.getSignature().getName());
        //参数
        logger.info("args={}",joinPoint.getArgs());
    }

    @After("log()")
    public void logAfter(){
        logger.info("======");
    }

    @AfterReturning(returning = "object",pointcut = "log()")
    public void doAfterReturning(Object object){
        logger.info("response={}",object);
    }
}
```
### 统一异常处理
需求:根据年龄 判断妹子读书的阶段。
1.controller
```java
    @GetMapping("/girls/getAge/{id}")
    public void getAge(@PathVariable("id") Integer id) throws Exception{
        girlService.getAgeRole(id);
    }
```
2.service
```java
    public void getAgeRole(Integer id) throws Exception{
        Optional<Girl> optional = girlRepository.findById(id);
        Girl girl = optional.orElse(null);
        Integer age = girl.getAge();
        if(age < 10){
            //return ServerResponse.createBySuccessMessage("你可能上小学");
            throw new Exception("你可能上小学");
        }else if(age > 10 && age < 16){
            throw new Exception("你可能上初中");
        }else{
            throw new Exception("你可能是个社会人");
        }
    }
```
3.handle
```java
@ControllerAdvice
public class ExceptionHandle {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ServerResponse exceptionResolve(Exception e){
        return ServerResponse.createByErrorMessage(e.getMessage());
    }
}
```
