package cn.edu.hestyle.bookstadium.service;

import cn.edu.hestyle.bookstadium.entity.Report;
import cn.edu.hestyle.bookstadium.entity.ReportVO;

import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/4/3 4:17 下午
 */
public interface IReportService {

    /**
     * user进行report
     * @param userId        userId
     * @param report        report
     */
    void userReport(Integer userId, Report report);

    /**
     * systemManager处理举报
     * @param report            report
     */
    void systemManagerHandle(Report report);

    /**
     * systemManager通过id删除Report
     * @param reportId      reportId
     * @param deleteReason  deleteReason
     */
    void systemManagerDeleteById(Integer reportId, String deleteReason);

    /**
     * 分页查询Report（未删除）
     * @param pageIndex     pageIndex
     * @param pageSize      pageSize
     * @return              List ReportVO
     */
    List<ReportVO> systemManagerFindAllByPage(Integer pageIndex, Integer pageSize, String titleKey);

    /**
     * 获取所有未删除的Report数量
     * @return              未删除的Report数量
     */
    Integer getAllCount(String titleKey);
}
