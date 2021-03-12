package cn.edu.hestyle.bookstadium.controller;

import cn.edu.hestyle.bookstadium.controller.exception.RequestException;
import cn.edu.hestyle.bookstadium.entity.StadiumCategory;
import cn.edu.hestyle.bookstadium.entity.SystemManager;
import cn.edu.hestyle.bookstadium.jwt.JwtToken;
import cn.edu.hestyle.bookstadium.service.IStadiumCategoryService;
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
import java.util.ArrayList;
import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/4 3:30 下午
 */
@RestController
@RequestMapping("/stadiumCategory")
public class StadiumCategoryController extends BaseController {
    /**允许上传文件的名称*/
    public static final String UPLOAD_DIR_NAME = "upload/image/stadiumCategory";

    /**上传文件的大小*/
    public static final long FILE_MAX_SIZE = 5 * 1024 * 1024;

    /**允许上传的文件类型*/
    public static final List<String> FILE_CONTENT_TYPES = new ArrayList<>();

    static {
        FILE_CONTENT_TYPES.add("image/jpeg");
        FILE_CONTENT_TYPES.add("image/png");
    }

    private static final Logger logger = LoggerFactory.getLogger(StadiumCategoryController.class);

    @Autowired
    private IStadiumCategoryService stadiumCategoryService;

    @PostMapping("/add.do")
    @JwtToken(required = true, authorizedRoles = {SystemManager.SYSTEM_MANAGER_ROLE})
    public ResponseResult<Void> handleAdd(@RequestParam("stadiumCategoryData") String stadiumCategoryData, HttpSession session) {
        // 从session中取出id
        Integer systemManagerId = (Integer) session.getAttribute("id");
        ObjectMapper objectMapper = new ObjectMapper();
        StadiumCategory stadiumCategory = null;
        try {
            stadiumCategory = objectMapper.readValue(stadiumCategoryData, StadiumCategory.class);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumCategory 添加失败，数据格式错误！data = " + stadiumCategoryData);
            throw new RequestException("StadiumCategory 添加失败，数据格式错误！");
        }
        stadiumCategoryService.add(systemManagerId, stadiumCategory);
        return new ResponseResult<Void>(SUCCESS, "添加成功！");
    }

    @PostMapping("/modify.do")
    @JwtToken(required = true, authorizedRoles = {SystemManager.SYSTEM_MANAGER_ROLE})
    public ResponseResult<Void> handleModify(@RequestParam(name = "stadiumCategoryData") String stadiumCategoryData, HttpSession session) {
        // 从session中取出id
        Integer systemManagerId = (Integer) session.getAttribute("id");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        StadiumCategory stadiumCategory = null;
        try {
            stadiumCategory = objectMapper.readValue(stadiumCategoryData, StadiumCategory.class);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumCategory 修改失败，数据格式错误！data = " + stadiumCategoryData);
            throw new RequestException("StadiumCategory 修改失败，数据格式错误！");
        }
        stadiumCategoryService.modify(systemManagerId, stadiumCategory);
        return new ResponseResult<Void>(SUCCESS, "修改成功！");
    }

    @PostMapping("/deleteByIdList.do")
    @JwtToken(required = true, authorizedRoles = {SystemManager.SYSTEM_MANAGER_ROLE})
    public ResponseResult<Void> handleDeleteByIdList(@RequestParam(name = "stadiumCategoryIdListData") String stadiumCategoryIdListData, HttpSession session) {
        // 从session中取出id
        Integer systemManagerId = (Integer) session.getAttribute("id");
        ObjectMapper objectMapper = new ObjectMapper();
        List<Integer> stadiumCategoryIdList = null;
        try {
            stadiumCategoryIdList = objectMapper.readValue(stadiumCategoryIdListData, new TypeReference<List<Integer>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumCategory 删除失败，数据格式错误！stadiumCategoryIdListData = " + stadiumCategoryIdListData);
            throw new RequestException("删除失败，数据格式错误！");
        }
        stadiumCategoryService.deleteByIdList(systemManagerId, stadiumCategoryIdList);
        return new ResponseResult<Void>(SUCCESS, "删除成功！");
    }

    @PostMapping("/findByIds.do")
    public ResponseResult<List<StadiumCategory>> handleFindByIds(@RequestParam("stadiumCategoryIds") String stadiumCategoryIds, HttpSession session) {
        List<StadiumCategory> stadiumCategoryList = stadiumCategoryService.findByIds(stadiumCategoryIds);
        return new ResponseResult<List<StadiumCategory>>(SUCCESS, "查找成功", stadiumCategoryList);
    }

    @PostMapping("/findByPage.do")
    public ResponseResult<List<StadiumCategory>> handleFindByPage(@RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex,
                                                                        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize, HttpSession session) {
        List<StadiumCategory> stadiumCategoryList = stadiumCategoryService.findByPage(pageIndex, pageSize);
        Integer count = stadiumCategoryService.getAllCount();
        return new ResponseResult<List<StadiumCategory>>(SUCCESS, count, stadiumCategoryList, "查找成功");
    }

    @PostMapping("/uploadImage.do")
    @JwtToken(required = true, authorizedRoles = {SystemManager.SYSTEM_MANAGER_ROLE})
    public ResponseResult<String> handleUploadImage(@RequestParam("file") MultipartFile file, HttpSession session) {
        // 从session中取出id
        Integer systemManagerId = (Integer) session.getAttribute("id");
        // 检查文件类型、大小
        String filePath = FileUploadProcessUtil.saveFile(file, UPLOAD_DIR_NAME,  FILE_MAX_SIZE, FILE_CONTENT_TYPES);
        logger.warn("SystemManager id = " + systemManagerId + "上传文件 url = " + filePath + "成功！");
        return new ResponseResult<String>(SUCCESS, "上传成功", filePath);
    }
}
