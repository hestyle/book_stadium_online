package cn.edu.hestyle.bookstadium.controller.exception;

/**
 * 未登录，就发出请求
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/3 10:11 上午
 */
public class NotLoginException extends RequestException {
    public NotLoginException() {
        super();
    }

    public NotLoginException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public NotLoginException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotLoginException(String message) {
        super(message);
    }

    public NotLoginException(Throwable cause) {
        super(cause);
    }
}
