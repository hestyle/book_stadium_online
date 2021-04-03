package cn.edu.hestyle.bookstadium.controller;

import cn.edu.hestyle.bookstadium.controller.exception.RequestException;
import cn.edu.hestyle.bookstadium.entity.Report;
import cn.edu.hestyle.bookstadium.entity.ReportVO;
import cn.edu.hestyle.bookstadium.entity.SystemManager;
import cn.edu.hestyle.bookstadium.jwt.JwtToken;
import cn.edu.hestyle.bookstadium.service.IReportService;
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
 * @date 2021/4/3 5:10 下午
 */
@RestController
@RequestMapping("/report")
public class ReportController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    private IReportService reportService;

    @PostMapping("/systemManagerHandle.do")
    @JwtToken(required = true, authorizedRoles = {SystemManager.SYSTEM_MANAGER_ROLE})
    public ResponseResult<Void> handleSystemManagerHandle(@RequestParam(value = "reportData") String reportData, HttpSession session) {
        ObjectMapper objectMapper = new ObjectMapper();
        Report report = null;
        try {
            report = objectMapper.readValue(reportData, Report.class);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Report 处理失败，数据格式错误！reportData = " + reportData);
            throw new RequestException("Report 处理失败，数据格式错误！");
        }
        reportService.systemManagerHandle(report);
        return new ResponseResult<Void>(SUCCESS, "处理成功！");
    }

    @PostMapping("/systemManagerDeleteById.do")
    @JwtToken(required = true, authorizedRoles = {SystemManager.SYSTEM_MANAGER_ROLE})
    public ResponseResult<Void> handleSystemManagerDeleteById(@RequestParam(value = "reportId") Integer reportId,
                                                              @RequestParam(value = "deleteReason") String deleteReason,
                                                              HttpSession session) {
        reportService.systemManagerDeleteById(reportId, deleteReason);
        return new ResponseResult<Void>(SUCCESS, "操作成功！");
    }

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
