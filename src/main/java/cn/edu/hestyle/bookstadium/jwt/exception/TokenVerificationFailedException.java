package cn.edu.hestyle.bookstadium.jwt.exception;

/**
 * 请求异常，是当前项目中控制器类抛出的异常的基类
 * @author hestyle
 */
public class TokenVerificationFailedException extends RuntimeException {

	private static final long serialVersionUID = 1117101227251814290L;

	public TokenVerificationFailedException() {
		super();
	}

	public TokenVerificationFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public TokenVerificationFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public TokenVerificationFailedException(String message) {
		super(message);
	}

	public TokenVerificationFailedException(Throwable cause) {
		super(cause);
	}

}
