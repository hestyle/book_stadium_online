package cn.edu.hestyle.bookstadium.controller;

import cn.edu.hestyle.bookstadium.controller.exception.RequestException;
import cn.edu.hestyle.bookstadium.entity.Complaint;
import cn.edu.hestyle.bookstadium.entity.ComplaintVO;
import cn.edu.hestyle.bookstadium.entity.SportKnowledge;
import cn.edu.hestyle.bookstadium.entity.SystemManager;
import cn.edu.hestyle.bookstadium.jwt.JwtToken;
import cn.edu.hestyle.bookstadium.service.IComplaintService;
import cn.edu.hestyle.bookstadium.util.ResponseResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/4/2 11:31 上午
 */
@RestController
@RequestMapping("/complaint")
public class ComplaintController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(ComplaintController.class);

    @Autowired
    private IComplaintService complaintService;

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
}
