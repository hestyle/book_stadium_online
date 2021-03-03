package cn.edu.hestyle.bookstadium.service.exception;

/**
 * 账号查找失败异常
 * @author hestyle
 */
public class AccountNotFoundException extends ServiceException {

    public AccountNotFoundException() {
        super();
    }

    public AccountNotFoundException(String message, Throwable cause, boolean enableSuppression,
                                    boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public AccountNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccountNotFoundException(String message) {
        super(message);
    }

    public AccountNotFoundException(Throwable cause) {
        super(cause);
    }
}
