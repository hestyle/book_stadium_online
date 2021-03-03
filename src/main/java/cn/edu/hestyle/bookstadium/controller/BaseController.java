package cn.edu.hestyle.bookstadium.controller;

import cn.edu.hestyle.bookstadium.controller.exception.NotLoginException;
import cn.edu.hestyle.bookstadium.controller.exception.RequestException;
import cn.edu.hestyle.bookstadium.controller.exception.RequestParamException;
import cn.edu.hestyle.bookstadium.service.exception.*;
import cn.edu.hestyle.bookstadium.util.ResponseResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 当前项目中所有控制器类的基类
 * @author hestyle
 */
public abstract class BaseController {
    /**
     * 正确响应的代号
     */
    public static final Integer SUCCESS = 200;
    public static final Integer SUCCESSFUL = 0;
    /**
     * 未知原因的错误响应代号
     */
    public static final Integer FAILURE = -1;

    @ExceptionHandler({ServiceException.class, RequestException.class})// 异常的范围
    @ResponseBody
    public ResponseResult<Void> handleException(Exception e) {

        Integer code = null;
        if (e instanceof LoginFailedException) {
            // 400-登录失败
            code = 400;
        } else if (e instanceof NotLoginException) {
            // 401-未登录
            code = 401;
        } else if (e instanceof AccountNotFoundException) {
            // 402-账号未查到
            code = 402;
        } else if (e instanceof RegisterFailedException) {
            // 403-账号注册失败
            code = 403;
        } else if (e instanceof RequestParamException) {
            // 404-请求参数错误
            code = 404;
        } else if (e instanceof ModifyFailedException) {
            // 405-更新保存失败
            code = 405;
        }
        return new ResponseResult<>(code, e);
    }
}
