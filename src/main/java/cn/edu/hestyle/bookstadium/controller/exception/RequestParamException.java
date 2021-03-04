package cn.edu.hestyle.bookstadium.controller.exception;

/**
 * 请求参数错误
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/3 10:11 上午
 */
public class RequestParamException extends RequestException {
    public RequestParamException() {
        super();
    }

    public RequestParamException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public RequestParamException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestParamException(String message) {
        super(message);
    }

    public RequestParamException(Throwable cause) {
        super(cause);
    }
}
