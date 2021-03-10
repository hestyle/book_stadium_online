package cn.edu.hestyle.bookstadium.controller;

import cn.edu.hestyle.bookstadium.controller.exception.RequestException;
import cn.edu.hestyle.bookstadium.entity.Banner;
import cn.edu.hestyle.bookstadium.entity.SystemManager;
import cn.edu.hestyle.bookstadium.jwt.JwtToken;
import cn.edu.hestyle.bookstadium.service.IBannerService;
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
 * @date 2021/3/10 5:13 下午
 */
@RestController
@RequestMapping("/banner")
public class BannerController extends BaseController {
    /**允许上传文件的名称*/
    public static final String UPLOAD_DIR_NAME = "upload/image/banner";

    /**上传文件的大小*/
    public static final long FILE_MAX_SIZE = 5 * 1024 * 1024;

    /**允许上传的文件类型*/
    public static final List<String> FILE_CONTENT_TYPES = new ArrayList<>();

    static {
        FILE_CONTENT_TYPES.add("image/jpeg");
        FILE_CONTENT_TYPES.add("image/png");
    }

    private static final Logger logger = LoggerFactory.getLogger(BannerController.class);

    @Autowired
    private IBannerService bannerService;

    @PostMapping("/add.do")
    @JwtToken(required = true, authorizedRoles = {SystemManager.SYSTEM_MANAGER_ROLE})
    public ResponseResult<Void> handleAdd(@RequestParam(name = "bannerData") String bannerData, HttpSession session) {
        // 从session中取出id
        Integer systemManagerId = (Integer) session.getAttribute("id");
        ObjectMapper objectMapper = new ObjectMapper();
        Banner banner = null;
        try {
            banner = objectMapper.readValue(bannerData, Banner.class);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Banner 添加失败，数据格式错误！data = " + bannerData);
            throw new RequestException("Banner 添加失败，数据格式错误！");
        }
        bannerService.add(systemManagerId, banner);
        return new ResponseResult<Void>(SUCCESS, "添加成功！");
    }

    @PostMapping("/modify.do")
    @JwtToken(required = true, authorizedRoles = {SystemManager.SYSTEM_MANAGER_ROLE})
    public ResponseResult<Void> handleModify(@RequestParam(name = "bannerData") String bannerData, HttpSession session) {
        // 从session中取出id
        Integer systemManagerId = (Integer) session.getAttribute("id");
        ObjectMapper objectMapper = new ObjectMapper();
        Banner banner = null;
        try {
            banner = objectMapper.readValue(bannerData, Banner.class);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Banner 修改失败，数据格式错误！data = " + bannerData);
            throw new RequestException("Banner 修改失败，数据格式错误！");
        }
        bannerService.modify(systemManagerId, banner);
        return new ResponseResult<Void>(SUCCESS, "修改成功！");
    }

    @PostMapping("/deleteByIdList.do")
    @JwtToken(required = true, authorizedRoles = {SystemManager.SYSTEM_MANAGER_ROLE})
    public ResponseResult<Void> handleDeleteByIdList(@RequestParam(name = "bannerIdListData") String bannerIdListData, HttpSession session) {
        // 从session中取出id
        Integer systemManagerId = (Integer) session.getAttribute("id");
        ObjectMapper objectMapper = new ObjectMapper();
        List<Integer> bannerIdList = null;
        try {
            bannerIdList = objectMapper.readValue(bannerIdListData, new TypeReference<List<Integer>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Banner 删除失败，数据格式错误！bannerIdListData = " + bannerIdListData);
            throw new RequestException("删除失败，数据格式错误！");
        }
        bannerService.deleteByIdList(systemManagerId, bannerIdList);
        return new ResponseResult<Void>(SUCCESS, "删除成功！");
    }

    @PostMapping("/findAll.do")
    public ResponseResult<List<Banner>> handleFindAll(HttpSession session) {
        List<Banner> bannerList = bannerService.findAll();
        return new ResponseResult<List<Banner>>(SUCCESS, "查找成功", bannerList);
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
