package cn.edu.hestyle.bookstadium.controller.exception;

/**
 * 文件上传错误
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/3 10:11 上午
 */
public class FileUploadFailedException extends RequestException {
    public FileUploadFailedException() {
        super();
    }

    public FileUploadFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public FileUploadFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileUploadFailedException(String message) {
        super(message);
    }

    public FileUploadFailedException(Throwable cause) {
        super(cause);
    }
}
