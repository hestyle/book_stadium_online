package cn.edu.hestyle.bookstadium.controller;

import cn.edu.hestyle.bookstadium.controller.exception.RequestException;
import cn.edu.hestyle.bookstadium.entity.*;
import cn.edu.hestyle.bookstadium.jwt.JwtToken;
import cn.edu.hestyle.bookstadium.service.IComplaintService;
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
 * @date 2021/4/2 11:31 上午
 */
@RestController
@RequestMapping("/complaint")
public class ComplaintController extends BaseController {
    /**允许上传文件的名称*/
    public static final String UPLOAD_DIR_NAME = "upload/image/complaint";

    /**上传文件的大小*/
    public static final long FILE_MAX_SIZE = 5 * 1024 * 1024;

    /**允许上传的文件类型*/
    public static final List<String> FILE_CONTENT_TYPES = new ArrayList<>();

    static {
        FILE_CONTENT_TYPES.add("image/jpeg");
        FILE_CONTENT_TYPES.add("image/png");
    }

    private static final Logger logger = LoggerFactory.getLogger(ComplaintController.class);

    @Autowired
    private IComplaintService complaintService;

    @PostMapping("/userComplain.do")
    @JwtToken(required = true, authorizedRoles = {User.USER_ROLE})
    public ResponseResult<Void> handleUserComplain(@RequestParam(value = "complaintData") String complaintData, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("id");
        ObjectMapper objectMapper = new ObjectMapper();
        Complaint complaint = null;
        try {
            complaint = objectMapper.readValue(complaintData, Complaint.class);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Complaint 添加失败，数据格式错误！complaintData = " + complaintData);
            throw new RequestException("Complaint 添加失败，数据格式错误！");
        }
        complaintService.userComplain(userId, complaint);
        return new ResponseResult<Void>(SUCCESS, "投诉成功！");
    }


    @PostMapping("/systemManagerHandle.do")
    @JwtToken(required = true, authorizedRoles = {SystemManager.SYSTEM_MANAGER_ROLE})
    public ResponseResult<Void> handleSystemManagerHandle(@RequestParam(value = "complaintData") String complaintData, HttpSession session) {
        ObjectMapper objectMapper = new ObjectMapper();
        Complaint complaint = null;
        try {
            complaint = objectMapper.readValue(complaintData, Complaint.class);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Complaint 处理失败，数据格式错误！complaintData = " + complaintData);
            throw new RequestException("Complaint 处理失败，数据格式错误！");
        }
        complaintService.systemManagerHandle(complaint);
        return new ResponseResult<Void>(SUCCESS, "处理成功！");
    }

    @PostMapping("/systemManagerDeleteById.do")
    @JwtToken(required = true, authorizedRoles = {SystemManager.SYSTEM_MANAGER_ROLE})
    public ResponseResult<Void> handleSystemManagerDeleteById(@RequestParam(value = "complaintId") Integer complaintId,
                                                              @RequestParam(value = "deleteReason") String deleteReason,
                                                              HttpSession session) {
        complaintService.systemManagerDeleteById(complaintId, deleteReason);
        return new ResponseResult<Void>(SUCCESS, "操作成功！");
    }

    @PostMapping("/systemManagerFindAllByPage.do")
    @JwtToken(required = true, authorizedRoles = {SystemManager.SYSTEM_MANAGER_ROLE})
    public ResponseResult<List<ComplaintVO>> handleSystemManagerFindAllByPage(@RequestParam(value = "titleKey", defaultValue = "") String titleKey,
                                                                              @RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex,
                                                                              @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                                              HttpSession session) {
        List<ComplaintVO> complaintVOList = complaintService.systemManagerFindAllByPage(pageIndex, pageSize, titleKey);
        Integer count = complaintService.getAllCount(titleKey);
        return new ResponseResult<List<ComplaintVO>>(SUCCESS, count, complaintVOList, "查询成功！");
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
