package cn.edu.hestyle.bookstadium.controller;

import cn.edu.hestyle.bookstadium.entity.ReportVO;
import cn.edu.hestyle.bookstadium.entity.SystemManager;
import cn.edu.hestyle.bookstadium.jwt.JwtToken;
import cn.edu.hestyle.bookstadium.service.IReportService;
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
 * @date 2021/4/3 5:10 下午
 */
@RestController
@RequestMapping("/report")
public class ReportController extends BaseController {

    @Autowired
    private IReportService reportService;

    @PostMapping("/systemManagerFindAllByPage.do")
    @JwtToken(required = true, authorizedRoles = {SystemManager.SYSTEM_MANAGER_ROLE})
    public ResponseResult<List<ReportVO>> handleSystemManagerFindAllByPage(@RequestParam(value = "titleKey", defaultValue = "") String titleKey,
                                                                           @RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex,
                                                                           @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                                           HttpSession session) {
        List<ReportVO> reportVOList = reportService.systemManagerFindAllByPage(pageIndex, pageSize, titleKey);
        Integer count = reportService.getAllCount(titleKey);
        return new ResponseResult<List<ReportVO>>(SUCCESS, count, reportVOList, "查询成功！");
    }
}
