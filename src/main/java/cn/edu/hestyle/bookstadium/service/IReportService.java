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
     * systemManager处理举报
     * @param report            report
     */
    void systemManagerHandle(Report report);

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
