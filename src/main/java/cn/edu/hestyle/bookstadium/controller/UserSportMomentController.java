package cn.edu.hestyle.bookstadium.controller;

import cn.edu.hestyle.bookstadium.controller.exception.RequestException;
import cn.edu.hestyle.bookstadium.entity.StadiumComment;
import cn.edu.hestyle.bookstadium.entity.StadiumManager;
import cn.edu.hestyle.bookstadium.entity.User;
import cn.edu.hestyle.bookstadium.entity.UserSportMoment;
import cn.edu.hestyle.bookstadium.jwt.JwtToken;
import cn.edu.hestyle.bookstadium.service.IUserSportMomentService;
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
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/16 5:18 下午
 */
@RestController
@RequestMapping("/userSportMoment")
public class UserSportMomentController extends BaseController {
    /**允许上传文件的名称*/
    public static final String UPLOAD_DIR_NAME = "upload/image/sportMoment";
    /**上传文件的大小*/
    public static final long FILE_MAX_SIZE = 5 * 1024 * 1024;
    /**允许上传的文件类型*/
    public static final List<String> FILE_CONTENT_TYPES = new ArrayList<>();
    static {
        FILE_CONTENT_TYPES.add("image/jpeg");
        FILE_CONTENT_TYPES.add("image/png");
    }
    private static final Logger logger = LoggerFactory.getLogger(UserSportMomentController.class);

    @Autowired
    private IUserSportMomentService userSportMomentService;

    @PostMapping("/add.do")
    @JwtToken(required = true, authorizedRoles = {User.USER_ROLE})
    public ResponseResult<Void> handleAdd(@RequestParam(value = "userSportMomentData") String userSportMomentData, HttpSession session) {
        // 从session中取出id
        Integer userId = (Integer) session.getAttribute("id");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        UserSportMoment userSportMoment = null;
        try {
            userSportMoment = objectMapper.readValue(userSportMomentData, UserSportMoment.class);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("UserSportMoment 增加失败，数据格式错误！userSportMomentData = " + userSportMomentData);
            throw new RequestException("保存失败，数据格式错误！");
        }
        if (userSportMoment != null) {
            userSportMoment.setUserId(userId);
        }
        userSportMomentService.add(userSportMoment);
        return new ResponseResult<Void>(SUCCESS, "保存成功！");
    }

    @PostMapping("/findByPage.do")
    public ResponseResult<List<UserSportMoment>> handleFindByPage(@RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex,
                                                                      @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                                      HttpSession session) {
        List<UserSportMoment> userSportMomentList = userSportMomentService.findByPage(pageIndex, pageSize);
        return new ResponseResult<List<UserSportMoment>>(SUCCESS, "查询成功！", userSportMomentList);
    }

    @PostMapping("/uploadImage.do")
    @JwtToken(required = true, authorizedRoles = {User.USER_ROLE})
    public ResponseResult<String> handleUploadImage(@RequestParam("file") MultipartFile file, HttpSession session) {
        // 从session中取出id
        Integer userId = (Integer) session.getAttribute("id");
        // 检查文件类型、大小
        String filePath = FileUploadProcessUtil.saveFile(file, UPLOAD_DIR_NAME,  FILE_MAX_SIZE, FILE_CONTENT_TYPES);
        logger.warn("User id = " + userId + "上传文件 url = " + filePath + "成功！");
        return new ResponseResult<String>(SUCCESS, "上传成功", filePath);
    }
}
