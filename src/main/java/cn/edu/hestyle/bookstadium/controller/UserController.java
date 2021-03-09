package cn.edu.hestyle.bookstadium.controller;

import cn.edu.hestyle.bookstadium.controller.exception.RequestParamException;
import cn.edu.hestyle.bookstadium.entity.User;
import cn.edu.hestyle.bookstadium.service.IUserService;
import cn.edu.hestyle.bookstadium.util.ResponseResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/9 5:01 下午
 */
@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private IUserService userService;

    @PostMapping("/register.do")
    public ResponseResult<Void> handleRegister(@RequestParam(name = "userData") String userData, HttpSession session) {
        ObjectMapper objectMapper = new ObjectMapper();
        User user = null;
        // 从userData读取user对象
        try {
            user = objectMapper.readValue(userData, User.class);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("User 注册失败，数据格式错误！data = " + userData);
            throw new RequestParamException("User 注册失败，数据格式错误！");
        }
        userService.register(user);
        return new ResponseResult<>(SUCCESS, user.getUsername() + " 注册成功！");
    }
}
