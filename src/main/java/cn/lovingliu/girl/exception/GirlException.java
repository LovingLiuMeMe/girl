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
