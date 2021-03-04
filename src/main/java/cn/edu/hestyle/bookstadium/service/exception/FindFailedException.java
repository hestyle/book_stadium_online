package cn.edu.hestyle.bookstadium.service.exception;

/**
 * 查找失败
 * @author hestyle
 */
public class FindFailedException extends ServiceException {

    public FindFailedException() {
        super();
    }

    public FindFailedException(String message, Throwable cause, boolean enableSuppression,
                               boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public FindFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public FindFailedException(String message) {
        super(message);
    }

    public FindFailedException(Throwable cause) {
        super(cause);
    }
}
