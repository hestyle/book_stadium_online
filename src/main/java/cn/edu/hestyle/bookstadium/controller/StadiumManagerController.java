package cn.edu.hestyle.bookstadium.controller;

import cn.edu.hestyle.bookstadium.controller.exception.NotLoginException;
import cn.edu.hestyle.bookstadium.entity.StadiumManager;
import cn.edu.hestyle.bookstadium.service.IStadiumManagerService;
import cn.edu.hestyle.bookstadium.util.ResponseResult;
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
