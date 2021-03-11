package cn.edu.hestyle.bookstadium.controller;

import cn.edu.hestyle.bookstadium.controller.exception.NotLoginException;
import cn.edu.hestyle.bookstadium.controller.exception.RequestParamException;
import cn.edu.hestyle.bookstadium.entity.StadiumManager;
import cn.edu.hestyle.bookstadium.jwt.JwtToken;
import cn.edu.hestyle.bookstadium.service.IStadiumManagerService;
import cn.edu.hestyle.bookstadium.util.FileUploadProcessUtil;
import cn.edu.hestyle.bookstadium.util.ResponseResult;
import com.fasterxml.jackson.core.type.TypeReference;
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
import java.util.*;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/3 10:22 上午
 */
@RestController
@RequestMapping("/stadiumManager")
public class StadiumManagerController extends BaseController {
    /**允许上传文件的名称*/
    public static final String UPLOAD_DIR_NAME = "upload/image/stadiumManager/avatar";

    /**上传文件的大小*/
    public static final long FILE_MAX_SIZE = 5 * 1024 * 1024;

    /**允许上传的文件类型*/
    public static final List<String> FILE_CONTENT_TYPES = new ArrayList<>();

    static {
        FILE_CONTENT_TYPES.add("image/jpeg");
        FILE_CONTENT_TYPES.add("image/png");
    }

    private static final Logger logger = LoggerFactory.getLogger(StadiumManagerController.class);

    @Autowired
    private IStadiumManagerService stadiumManagerService;

    @PostMapping("/login.do")
    public ResponseResult<StadiumManager> handleLogin(@RequestParam("username") String username, @RequestParam("password") String password, HttpSession session) {
        // 执行业务端的业务
        StadiumManager stadiumManager = stadiumManagerService.login(username, password);
        // 将id role发到session中，保存到服务端
        session.setAttribute("id", stadiumManager.getId());
        session.setAttribute("role", StadiumManager.STADIUM_MANAGER_ROLE);
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
    @JwtToken(required = true, authorizedRoles = {StadiumManager.STADIUM_MANAGER_ROLE})
    public ResponseResult<StadiumManager> handleGetInfo(HttpSession session) {
        // 从session中取出id
        Integer stadiumManagerId = (Integer) session.getAttribute("id");
        // 执行业务端的业务
        StadiumManager stadiumManager = stadiumManagerService.findById(stadiumManagerId);
        return new ResponseResult<>(SUCCESS, "获取成功！", stadiumManager);
    }

    @PostMapping("/modify.do")
    @JwtToken(required = true, authorizedRoles = {StadiumManager.STADIUM_MANAGER_ROLE})
    public ResponseResult<Void> handleModify(@RequestParam(name = "modifyData") String modifyData, HttpSession session) {
        // 从session中取出id
        Integer stadiumManagerId = (Integer) session.getAttribute("id");
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String, Object> modifyDataMap = null;
        // 从stadiumManagerData读取modifyDataMap对象
        try {
            modifyDataMap = objectMapper.readValue(modifyData, new TypeReference<HashMap<String, Object>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumManager 注册失败，数据格式错误！data = " + modifyData);
            throw new RequestParamException("账号更新保存失败，数据格式错误！");
        }
        // 执行业务端的业务
        stadiumManagerService.modifyInfo(stadiumManagerId, modifyDataMap);
        return new ResponseResult<>(SUCCESS, "账号更新保存成功！");
    }

    @PostMapping("/changePassword.do")
    @JwtToken(required = true, authorizedRoles = {StadiumManager.STADIUM_MANAGER_ROLE})
    public ResponseResult<Void> handleChangePassword(@RequestParam(name = "beforePassword") String beforePassword, @RequestParam(name = "newPassword") String newPassword, HttpSession session) {
        // 从session中取出id
        Integer stadiumManagerId = (Integer) session.getAttribute("id");
        // 执行业务端的业务
        stadiumManagerService.changePassword(stadiumManagerId, beforePassword, newPassword);
        return new ResponseResult<>(SUCCESS, "密码修改成功！");
    }

    @PostMapping("/uploadAvatar.do")
    @JwtToken(required = true, authorizedRoles = {StadiumManager.STADIUM_MANAGER_ROLE})
    public ResponseResult<String> handleUploadAvatar(@RequestParam("file") MultipartFile file, HttpSession session) {
        // 从session中取出id
        Integer stadiumManagerId = (Integer) session.getAttribute("id");
        String filePath = FileUploadProcessUtil.saveFile(file, UPLOAD_DIR_NAME,  FILE_MAX_SIZE, FILE_CONTENT_TYPES);
        logger.warn("StadiumManager stadiumManagerId = " + stadiumManagerId + "上传文件 url = " + filePath + "成功！");
        return new ResponseResult<String>(SUCCESS, "上传成功", filePath);
    }
}
