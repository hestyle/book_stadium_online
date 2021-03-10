package cn.edu.hestyle.bookstadium.service.exception;

/**
 * 注销失败异常
 * @author hestyle
 */
public class LogoutFailedException extends ServiceException {

    public LogoutFailedException() {
        super();
    }

    public LogoutFailedException(String message, Throwable cause, boolean enableSuppression,
                                 boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public LogoutFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public LogoutFailedException(String message) {
        super(message);
    }

    public LogoutFailedException(Throwable cause) {
        super(cause);
    }
}
