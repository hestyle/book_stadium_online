package cn.edu.hestyle.bookstadium.controller;

import cn.edu.hestyle.bookstadium.entity.ComplaintVO;
import cn.edu.hestyle.bookstadium.entity.SystemManager;
import cn.edu.hestyle.bookstadium.jwt.JwtToken;
import cn.edu.hestyle.bookstadium.service.IComplaintService;
import cn.edu.hestyle.bookstadium.util.ResponseResult;
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

    @Autowired
    private IComplaintService complaintService;

    @PostMapping("/systemManagerFindAllByPage.do")
    @JwtToken(required = true, authorizedRoles = {SystemManager.SYSTEM_MANAGER_ROLE})
    public ResponseResult<List<ComplaintVO>> handleSystemManagerFindAllByPage(@RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex,
                                                                              @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                                              HttpSession session) {
        List<ComplaintVO> complaintVOList = complaintService.systemManagerFindAllByPage(pageIndex, pageSize);
        Integer count = complaintService.getAllCount();
        return new ResponseResult<List<ComplaintVO>>(SUCCESS, count, complaintVOList, "查询成功！");
    }
}
