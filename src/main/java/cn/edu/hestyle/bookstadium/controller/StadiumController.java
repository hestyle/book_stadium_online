package cn.edu.hestyle.bookstadium.controller;

import cn.edu.hestyle.bookstadium.controller.exception.RequestParamException;
import cn.edu.hestyle.bookstadium.entity.Stadium;
import cn.edu.hestyle.bookstadium.entity.StadiumManager;
import cn.edu.hestyle.bookstadium.jwt.JwtToken;
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

    @PostMapping("/findByNameKeyAndPage.do")
    public ResponseResult<List<Stadium>> handleFindByNameKeyAndPage(@RequestParam(name = "nameKey", defaultValue = "") String nameKey,
                                                                    @RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex,
                                                                    @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                                    HttpSession session) {
        List<Stadium> stadiumList = stadiumService.findByNameKeyAndPage(nameKey, pageIndex, pageSize);
        Integer count = stadiumService.getCount(nameKey);
        return new ResponseResult<List<Stadium>>(SUCCESS, count, stadiumList, "查询成功！");
    }

    @PostMapping("/findByPage.do")
    public ResponseResult<List<Stadium>> handleFindByPage(@RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex,
                                                                       @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize, HttpSession session) {
        List<Stadium> stadiumList = stadiumService.findByPage(pageIndex, pageSize);
        return new ResponseResult<List<Stadium>>(SUCCESS, "查询成功！", stadiumList);
    }

    @PostMapping("/findByStadiumCategoryId.do")
    public ResponseResult<List<Stadium>> handleFindByStadiumCategoryId(Integer stadiumCategoryId, @RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize, HttpSession session) {
        List<Stadium> stadiumList = stadiumService.findByStadiumCategoryId(stadiumCategoryId, pageIndex, pageSize);
        return new ResponseResult<List<Stadium>>(SUCCESS, "查询成功！", stadiumList);
    }

    @PostMapping("/stadiumManagerAdd.do")
    @JwtToken(required = true, authorizedRoles = {StadiumManager.STADIUM_MANAGER_ROLE})
    public ResponseResult<Void> handleStadiumManagerAdd(@RequestParam(name = "stadiumData") String stadiumData, HttpSession session) {
        // 从session中取出id
        Integer stadiumManagerId = (Integer) session.getAttribute("id");
        ObjectMapper objectMapper = new ObjectMapper();
        Stadium stadium = null;
        try {
            stadium = objectMapper.readValue(stadiumData, Stadium.class);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Stadium 添加失败，数据格式错误！data = " + stadiumData);
            throw new RequestParamException("Stadium 添加失败，数据格式错误！");
        }
        stadiumService.add(stadiumManagerId, stadium);
        return new ResponseResult<Void>(SUCCESS, "体育场馆添加成功！");
    }

    @PostMapping("/stadiumManagerModify.do")
    @JwtToken(required = true, authorizedRoles = {StadiumManager.STADIUM_MANAGER_ROLE})
    public ResponseResult<Void> handleStadiumManagerModify(@RequestParam(name = "modifyData") String modifyData, HttpSession session) {
        // 从session中取出id
        Integer stadiumManagerId = (Integer) session.getAttribute("id");
        ObjectMapper objectMapper = new ObjectMapper();
        Stadium stadium = null;
        // 从stadiumManagerData读取modifyDataMap对象
        try {
            stadium = objectMapper.readValue(modifyData, Stadium.class);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Stadium 修改失败，数据格式错误！data = " + modifyData);
            throw new RequestParamException("更新保存失败，数据格式错误！");
        }
        // 执行业务端的业务
        stadiumService.stadiumManagerModify(stadiumManagerId, stadium);
        return new ResponseResult<>(SUCCESS, "更新保存成功！");
    }

    @PostMapping("/stadiumManagerDeleteByIdList.do")
    @JwtToken(required = true, authorizedRoles = {StadiumManager.STADIUM_MANAGER_ROLE})
    public ResponseResult<Void> handleStadiumManagerDeleteByIdList(@RequestParam(name = "idListJsonString") String idListJsonString, HttpSession session) {
        // 从session中取出id
        Integer stadiumManagerId = (Integer) session.getAttribute("id");
        List<Integer> idList = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            idList = objectMapper.readValue(idListJsonString, new TypeReference<List<Integer>>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new ResponseResult<>(FAILURE, "id list数据格式不正确！");
        }
        System.err.println(idListJsonString);
        stadiumService.stadiumManagerDeleteByIds(stadiumManagerId, idList);
        return new ResponseResult<>(SUCCESS, "批量删除成功！");
    }

    @PostMapping("/stadiumManagerFindByPage.do")
    @JwtToken(required = true, authorizedRoles = {StadiumManager.STADIUM_MANAGER_ROLE})
    public ResponseResult<List<Stadium>> handleStadiumManagerFindByPage(@RequestParam(value = "nameKey", defaultValue = "") String nameKey,
                                                                        @RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex,
                                                                        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize, HttpSession session) {
        // 从session中取出id
        Integer stadiumManagerId = (Integer) session.getAttribute("id");
        List<Stadium> stadiumList = stadiumService.stadiumManagerFindByPage(stadiumManagerId, pageIndex, pageSize, nameKey);
        Integer count = stadiumService.stadiumManagerGetCount(stadiumManagerId, nameKey);
        return new ResponseResult<List<Stadium>>(SUCCESS, count, stadiumList, "查询成功！");
    }

    @PostMapping("/uploadImage.do")
    @JwtToken(required = true, authorizedRoles = {StadiumManager.STADIUM_MANAGER_ROLE})
    public ResponseResult<String> handleUploadImage(@RequestParam("file") MultipartFile file, HttpSession session) {
        // 从session中取出id
        Integer stadiumManagerId = (Integer) session.getAttribute("id");
        // 检查文件类型、大小
        String filePath = FileUploadProcessUtil.saveFile(file, UPLOAD_DIR_NAME,  FILE_MAX_SIZE, FILE_CONTENT_TYPES);
        logger.warn("StadiumManager id = " + stadiumManagerId + "上传文件 url = " + filePath + "成功！");
        return new ResponseResult<String>(SUCCESS, "上传成功", filePath);
    }

}
