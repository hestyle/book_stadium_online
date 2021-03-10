package cn.edu.hestyle.bookstadium.controller;

import cn.edu.hestyle.bookstadium.entity.SystemManager;
import cn.edu.hestyle.bookstadium.jwt.JwtToken;
import cn.edu.hestyle.bookstadium.service.ISystemManagerService;
import cn.edu.hestyle.bookstadium.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * SystemManager 控制器
 * @author hestyle
 */
@RestController
@RequestMapping("/systemManager")
public class SystemManagerController extends BaseController {

    @Autowired
    private ISystemManagerService systemManagerService;

    @PostMapping("/login.do")
    public ResponseResult<SystemManager> handleLogin(@RequestParam("username") String username, @RequestParam("password") String password, HttpSession session) {
        // 执行业务端的业务
        SystemManager systemManager = systemManagerService.login(username, password);
        // 将id role发到session中，保存到服务端
        session.setAttribute("id", systemManager.getId());
        session.setAttribute("role", SystemManager.SYSTEM_MANAGER_ROLE);
        return new ResponseResult<>(SUCCESS, "登录成功！", systemManager);
    }

    @PostMapping("/getInfo.do")
    @JwtToken(required = true, authorizedRoles = {SystemManager.SYSTEM_MANAGER_ROLE})
    public ResponseResult<SystemManager> handleGetInfo(HttpSession session) {
        // 从session中取出id
        Integer systemManagerId = (Integer) session.getAttribute("id");
        // 执行业务端逻辑
        SystemManager systemManager = systemManagerService.findById(systemManagerId);
        return new ResponseResult<>(SUCCESS, "获取成功！", systemManager);
    }
}
