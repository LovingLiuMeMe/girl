package cn.lovingliu.girl.handle;

import cn.lovingliu.girl.common.ServerResponse;
import cn.lovingliu.girl.exception.GirlException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author：LovingLiu
 * @Description:
 * @Date：Created in 2019-09-17
 */
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
