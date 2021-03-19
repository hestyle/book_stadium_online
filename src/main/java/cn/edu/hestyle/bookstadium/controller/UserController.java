package cn.edu.hestyle.bookstadium.controller;

import cn.edu.hestyle.bookstadium.controller.exception.RequestParamException;
import cn.edu.hestyle.bookstadium.entity.User;
import cn.edu.hestyle.bookstadium.jwt.JwtToken;
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

    @PostMapping("/login.do")
    public ResponseResult<User> handleLogin(@RequestParam("username") String username, @RequestParam("password") String password, HttpSession session) {
        // 执行业务端的业务
        User user = userService.login(username, password);
        // 将userId发到session中，保存到服务端
        session.setAttribute("userId", user.getId());
        return new ResponseResult<>(SUCCESS, "登录成功！", user);
    }

    @PostMapping("/logout.do")
    @JwtToken(required = true, authorizedRoles = {User.USER_ROLE})
    public ResponseResult<Void> handleLogout(HttpSession session) {
        // 从session中取出id
        Integer userId = (Integer) session.getAttribute("id");
        userService.logout(userId);
        return new ResponseResult<>(SUCCESS, "登录注销成功！");
    }

    @PostMapping("/modifyPassword.do")
    @JwtToken(required = true, authorizedRoles = {User.USER_ROLE})
    public ResponseResult<Void> handleModifyPassword(@RequestParam("password") String password,
                                                     @RequestParam("newPassword") String newPassword,
                                                     HttpSession session) {
        // 从session中取出id
        Integer userId = (Integer) session.getAttribute("id");
        userService.modifyPassword(userId, password, newPassword);
        return new ResponseResult<>(SUCCESS, "密码修改成功！");
    }

    @PostMapping("/getInfo.do")
    @JwtToken(required = true, authorizedRoles = {User.USER_ROLE})
    public ResponseResult<User> handleGetInfo(HttpSession session) {
        // 从session中取出id
        Integer userId = (Integer) session.getAttribute("id");
        User user = userService.findById(userId);
        return new ResponseResult<User>(SUCCESS, "查找成功！", user);
    }

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
