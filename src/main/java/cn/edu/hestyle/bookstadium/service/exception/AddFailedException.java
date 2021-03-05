package cn.edu.hestyle.bookstadium.service.exception;

/**
 * 添加失败
 * @author hestyle
 */
public class AddFailedException extends ServiceException {

    public AddFailedException() {
        super();
    }

    public AddFailedException(String message, Throwable cause, boolean enableSuppression,
                              boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public AddFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AddFailedException(String message) {
        super(message);
    }

    public AddFailedException(Throwable cause) {
        super(cause);
    }
}
