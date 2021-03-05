package cn.edu.hestyle.bookstadium.service.exception;

/**
 * 删除失败异常
 * @author hestyle
 */
public class DeleteFailedException extends ServiceException {

    public DeleteFailedException() {
        super();
    }

    public DeleteFailedException(String message, Throwable cause, boolean enableSuppression,
                                 boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public DeleteFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeleteFailedException(String message) {
        super(message);
    }

    public DeleteFailedException(Throwable cause) {
        super(cause);
    }
}
