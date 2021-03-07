package cn.edu.hestyle.bookstadium.controller;

import cn.edu.hestyle.bookstadium.controller.exception.NotLoginException;
import cn.edu.hestyle.bookstadium.controller.exception.RequestParamException;
import cn.edu.hestyle.bookstadium.entity.Stadium;
import cn.edu.hestyle.bookstadium.service.IStadiumService;
import cn.edu.hestyle.bookstadium.util.FileUploadProcessUtil;
import cn.edu.hestyle.bookstadium.util.ResponseResult;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/4 10:43 上午
 */
@RestController
@RequestMapping("/stadium")
public class StadiumController extends BaseController {
    /**允许上传文件的名称*/
    public static final String UPLOAD_DIR_NAME = "upload/image/stadium";

    /**上传文件的大小*/
    public static final long FILE_MAX_SIZE = 5 * 1024 * 1024;

    /**允许上传的文件类型*/
    public static final List<String> FILE_CONTENT_TYPES = new ArrayList<>();

    static {
        FILE_CONTENT_TYPES.add("image/jpeg");
        FILE_CONTENT_TYPES.add("image/png");
    }

    private static final Logger logger = LoggerFactory.getLogger(StadiumController.class);

    @Autowired
    private IStadiumService stadiumService;

    @PostMapping("/findById.do")
    public ResponseResult<Stadium> handleFindById(@RequestParam(name = "stadiumId") Integer stadiumId, HttpSession session) {
        Stadium stadium = stadiumService.findById(stadiumId);
        return new ResponseResult<Stadium>(SUCCESS, "查询成功！", stadium);
    }

    @PostMapping("/stadiumManagerAdd.do")
    public ResponseResult<Void> handleStadiumManagerAdd(@RequestParam(name = "stadiumData") String stadiumData, HttpSession session) {
        // 判断是否登录
        String username = (String) session.getAttribute("stadiumManagerUsername");
        if (null == username) {
            throw new NotLoginException("请求失败，请先进行登录！");
        }
        ObjectMapper objectMapper = new ObjectMapper();
        Stadium stadium = null;
        try {
            stadium = objectMapper.readValue(stadiumData, Stadium.class);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Stadium 添加失败，数据格式错误！data = " + stadiumData);
            throw new RequestParamException("Stadium 添加失败，数据格式错误！");
        }
        stadiumService.add(username, stadium);
        return new ResponseResult<Void>(SUCCESS, "体育场馆添加成功！");
    }

    @PostMapping("/stadiumManagerModify.do")
    public ResponseResult<Void> handleStadiumManagerModify(@RequestParam(name = "modifyData") String modifyData, HttpSession session) {
        // 判断是否登录
        String username = (String) session.getAttribute("stadiumManagerUsername");
        if (null == username) {
            throw new NotLoginException("请求失败，请先进行登录！");
        }
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String, Object> modifyDataMap = null;
        // 从stadiumManagerData读取modifyDataMap对象
        try {
            modifyDataMap = objectMapper.readValue(modifyData, new TypeReference<HashMap<String, Object>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Stadium 修改失败，数据格式错误！data = " + modifyData);
            throw new RequestParamException("更新保存失败，数据格式错误！");
        }
        // 执行业务端的业务
        stadiumService.stadiumManagerModify(username, modifyDataMap);
        return new ResponseResult<>(SUCCESS, "账号更新保存成功！");
    }

    @PostMapping("/stadiumManagerDeleteByIdList.do")
    public ResponseResult<Void> handleStadiumManagerDeleteByIdList(@RequestParam(name = "idListJsonString") String idListJsonString, HttpSession session) {
        // 判断是否登录
        String username = (String) session.getAttribute("stadiumManagerUsername");
        if (null == username) {
            throw new NotLoginException("请求失败，请先进行登录！");
        }
        List<Integer> idList = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            idList = objectMapper.readValue(idListJsonString, new TypeReference<List<Integer>>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new ResponseResult<>(FAILURE, "id list数据格式不正确！");
        }
        System.err.println(idListJsonString);
        stadiumService.stadiumManagerDeleteByIds(username, idList);
        return new ResponseResult<>(SUCCESS, "批量删除成功！");
    }

    @PostMapping("/stadiumManagerFindByPage.do")
    public ResponseResult<List<Stadium>> handleStadiumManagerFindByPage(@RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex,
                                                                        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize, HttpSession session) {
        // 判断是否登录
        String username = (String) session.getAttribute("stadiumManagerUsername");
        if (null == username) {
            throw new NotLoginException("请求失败，请先进行登录！");
        }
        List<Stadium> stadiumList = stadiumService.stadiumManagerFindByPage(username, pageIndex, pageSize);
        Integer count = stadiumService.stadiumManagerGetCount(username);
        return new ResponseResult<List<Stadium>>(SUCCESS, count, stadiumList, "查询成功！");
    }

    @PostMapping("/uploadImage.do")
    public ResponseResult<String> handleUploadImage(@RequestParam("file") MultipartFile file, HttpSession session) {
        // 判断是否已经登录过
        String username = (String) session.getAttribute("stadiumManagerUsername");
        if (null == username) {
            throw new NotLoginException("请求失败，请先进行登录！");
        }
        // 检查文件类型、大小
        String filePath = FileUploadProcessUtil.saveFile(file, UPLOAD_DIR_NAME,  FILE_MAX_SIZE, FILE_CONTENT_TYPES);
        logger.warn("StadiumManager username = " + username + "上传文件 url = " + filePath + "成功！");
        return new ResponseResult<String>(SUCCESS, "上传成功", filePath);
    }

}
