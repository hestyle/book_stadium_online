package cn.edu.hestyle.bookstadium.service.exception;

/**
 * 登录失败异常
 * @author hestyle
 */
public class ModifyFailedException extends ServiceException {

    public ModifyFailedException() {
        super();
    }

    public ModifyFailedException(String message, Throwable cause, boolean enableSuppression,
                                 boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ModifyFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModifyFailedException(String message) {
        super(message);
    }

    public ModifyFailedException(Throwable cause) {
        super(cause);
    }
}
