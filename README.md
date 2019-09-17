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
### 4.统一异常处理
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
### 5.编写项目专属统一异常处理类
1.GirlException
```java
package cn.lovingliu.girl.exception;

/**
 * @Author：LovingLiu
 * @Description: Girl项目的统一异常处理
 * @Date：Created in 2019-09-17
 */
public class GirlException extends RuntimeException {
    /**
     * @Desc 为什么要继承RuntimeException 而不继承Exception呢 因为Spring只会对RuntimeException 进行事务回滚
     * @Author LovingLiu
    */

    private Integer code;

    public GirlException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}

```
2.GirlService
```java
    public void getAgeRole(Integer id) throws GirlException{
        Optional<Girl> optional = girlRepository.findById(id);
        Girl girl = optional.orElse(null);
        Integer age = girl.getAge();
        if(age < 10){
            //return ServerResponse.createBySuccessMessage("你可能上小学");
            throw new GirlException(ResponseCode.ERROR.getCode(),"你可能上小学");
        }else if(age > 10 && age < 16){
            throw new GirlException(ResponseCode.ERROR.getCode(),"你可能上初中");
        }else{
            throw new GirlException(ResponseCode.ERROR.getCode(),"你可能是个社会人");
        }
    }
```
3.GirlController
```java
    @GetMapping("/girls/getAge/{id}")
    public void getAge(@PathVariable("id") Integer id) throws GirlException {
        girlService.getAgeRole(id);
    }
```
4.ExceptionHandle
```java
@ControllerAdvice
public class ExceptionHandle {

    @ExceptionHandler(value = GirlException.class)
    @ResponseBody
    public ServerResponse exceptionResolve(Exception e){
        if(e instanceof GirlException){
            GirlException girlException = (GirlException) e;
            return ServerResponse.createByErrorCodeMessage(girlException.getCode(),girlException.getMessage());
        }
        return ServerResponse.createByErrorMessage(e.getMessage());
    }
}
```

#### aspect/HttpAspect.java Aspect注解实现切面编程
1.`@Aspect`,`@Component`  
首先定义一个切面类，加上@Component  @Aspect这两个注解  
```java
@Aspect
@Component
public class HttpAspect {
    
}
```
2.`@Pointcut`  
定义切点
```java

    private static final String POINT_CUT = "execution(public * cn.lovingliu.girl.controller.GirlController.*(..))";
    /**
     * @Desc 2.定义切点
     * @Author LovingLiu
    */
    @Pointcut(POINT_CUT)    //Pointcut表示式
    public void log(){ //Point签名(空方法)
    }
```
3.`@After`等
配置增强  
   @Before  在切点方法之前执行  
   @After  在切点方法之后执行  
   @AfterReturning 切点方法返回后执行
```java
    @Before("log()") //指定签名
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
    /**
     * @Desc returning 是切点的返回值 注意:返回的结果如果null  对其操作会报错哦
     * @Author LovingLiu
    */
    @AfterReturning(returning = "object",pointcut = "log()")
    public void doAfterReturning(Object object){
        logger.info("response={}",object);
    }
```

#### @ExceptionHandler 和 @ControllerAdvice
`应用场景`:事务配置在 Service层，当数据库操作失败时让Service层抛出运行时异常，Spring事物管理器就会进行回滚。  
如此一来，我们的Controller层就不得不进行try-catch Service层的异常，否则会返回一些不友好的错误信息到客户端。  
但是，Controller层每个方法体都写一些模板化的try-catch的代码，很难看也难维护，特别是还需要对Service层的不同异常进行不同处理的时候。

1.`@ExceptionHandler`  
`@ExceptionHandler` 是Controller层面上异常处理
如下TestController发生的任何RuntimeException异常，都将被resolve方法捕获.  
注意事项: 1. 一个Controller下多个@ExceptionHandler上的异常类型不能出现一样的，否则运行时抛异常.
```java
@Controller
@RequestMapping("/testController")
public class TestController {
 
    @RequestMapping("/demo1")
    @ResponseBody
    public Object demo1(){
        int i = 1 / 0;
        return new Date();
    }
 
    @ExceptionHandler({RuntimeException.class})
    public ServerResponse resolve(Exception e){
        return ServerReponse.createByErroeMessage(e.getMessage());
    }
}
```
2.@ControllerAdvice  
    实际上controller的一个辅助类，最常用的就是作为全局异常处理的切面
    可以指定扫描的范围
    
