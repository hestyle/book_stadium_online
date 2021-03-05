package cn.edu.hestyle.bookstadium.service.exception;

/**
 * 账号注册失败异常
 * @author hestyle
 */
public class RegisterFailedException extends ServiceException {

    public RegisterFailedException() {
        super();
    }

    public RegisterFailedException(String message, Throwable cause, boolean enableSuppression,
                                   boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public RegisterFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public RegisterFailedException(String message) {
        super(message);
    }

    public RegisterFailedException(Throwable cause) {
        super(cause);
    }
}
