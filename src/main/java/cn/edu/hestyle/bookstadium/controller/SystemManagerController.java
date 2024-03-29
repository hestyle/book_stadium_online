package cn.edu.hestyle.bookstadium.controller;

import cn.edu.hestyle.bookstadium.controller.exception.RequestParamException;
import cn.edu.hestyle.bookstadium.entity.SystemManager;
import cn.edu.hestyle.bookstadium.jwt.JwtToken;
import cn.edu.hestyle.bookstadium.service.ISystemManagerService;
import cn.edu.hestyle.bookstadium.util.FileUploadProcessUtil;
import cn.edu.hestyle.bookstadium.util.ResponseResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * SystemManager 控制器
 * @author hestyle
 */
@RestController
@RequestMapping("/systemManager")
public class SystemManagerController extends BaseController {
    /**允许上传文件的名称*/
    public static final String UPLOAD_DIR_NAME = "upload/image/systemManager/avatar";

    /**上传文件的大小*/
    public static final long FILE_MAX_SIZE = 5 * 1024 * 1024;

    /**允许上传的文件类型*/
    public static final List<String> FILE_CONTENT_TYPES = new ArrayList<>();

    static {
        FILE_CONTENT_TYPES.add("image/jpeg");
        FILE_CONTENT_TYPES.add("image/png");
    }

    private static final Logger logger = LoggerFactory.getLogger(SystemManagerController.class);

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

    @PostMapping("/modify.do")
    @JwtToken(required = true, authorizedRoles = {SystemManager.SYSTEM_MANAGER_ROLE})
    public ResponseResult<Void> handleModify(@RequestParam("systemManagerModifyData") String systemManagerModifyData, HttpSession session) {
        // 从session中取出id
        Integer systemManagerId = (Integer) session.getAttribute("id");
        SystemManager systemManager = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            systemManager = objectMapper.readValue(systemManagerModifyData, SystemManager.class);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SystemManager 修改失败，数据格式错误！data = " + systemManagerModifyData);
            throw new RequestParamException("账号更新保存失败，数据格式错误！");
        }
        systemManagerService.modify(systemManagerId, systemManager);
        return new ResponseResult<>(SUCCESS, "修改成功！");
    }

    @PostMapping("/uploadAvatar.do")
    @JwtToken(required = true, authorizedRoles = {SystemManager.SYSTEM_MANAGER_ROLE})
    public ResponseResult<String> handleUploadAvatar(@RequestParam("file") MultipartFile file, HttpSession session) {
        // 从session中取出id
        Integer systemManagerId = (Integer) session.getAttribute("id");
        String filePath = FileUploadProcessUtil.saveFile(file, UPLOAD_DIR_NAME,  FILE_MAX_SIZE, FILE_CONTENT_TYPES);
        logger.warn("SystemManager systemManagerId = " + systemManagerId + "上传文件 url = " + filePath + "成功！");
        return new ResponseResult<String>(SUCCESS, "上传成功", filePath);
    }
}
