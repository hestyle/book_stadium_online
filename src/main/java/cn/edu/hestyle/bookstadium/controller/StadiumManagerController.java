package cn.edu.hestyle.bookstadium.controller;

import cn.edu.hestyle.bookstadium.controller.exception.NotLoginException;
import cn.edu.hestyle.bookstadium.controller.exception.RequestParamException;
import cn.edu.hestyle.bookstadium.entity.StadiumManager;
import cn.edu.hestyle.bookstadium.service.IStadiumManagerService;
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
 * @date 2021/3/3 10:22 上午
 */
@RestController
@RequestMapping("/stadiumManager")
public class StadiumManagerController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(StadiumManagerController.class);

    @Autowired
    private IStadiumManagerService stadiumManagerService;

    @PostMapping("/login.do")
    public ResponseResult<StadiumManager> handleLogin(@RequestParam("username") String username, @RequestParam("password") String password, HttpSession session) {
        // 执行业务端的业务
        StadiumManager stadiumManager = stadiumManagerService.login(username, password);
        // 将用户名发到session中，保存到服务端
        session.setAttribute("stadiumManagerUsername", stadiumManager.getUsername());
        return new ResponseResult<>(SUCCESS, "登录成功！", stadiumManager);
    }

    @PostMapping("/register.do")
    public ResponseResult<Void> handleRegister(@RequestParam(name = "stadiumManagerData") String stadiumManagerData, HttpSession session) {
        ObjectMapper objectMapper = new ObjectMapper();
        StadiumManager stadiumManager = null;
        // 从stadiumManagerData读取stadiumManager对象
        try {
            stadiumManager = objectMapper.readValue(stadiumManagerData, StadiumManager.class);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumManager 注册失败，数据格式错误！data = " + stadiumManagerData);
            throw new RequestParamException("StadiumManager 注册失败，数据格式错误！");
        }
        // 执行业务端的业务
        stadiumManagerService.register(stadiumManager);
        return new ResponseResult<>(SUCCESS, stadiumManager.getUsername() + "注册成功！");
    }

    @PostMapping("/getInfo.do")
    public ResponseResult<StadiumManager> handleGetInfo(HttpSession session) {
        // 判断是否登录
        String username = (String) session.getAttribute("stadiumManagerUsername");
        if (null == username) {
            throw new NotLoginException("请求失败，请先进行登录！");
        }
        // 执行业务端的业务
        StadiumManager stadiumManager = stadiumManagerService.findByUsername(username);
        return new ResponseResult<>(SUCCESS, "获取成功！", stadiumManager);
    }

}
