package cn.edu.hestyle.bookstadium.mapper;

import cn.edu.hestyle.bookstadium.entity.Report;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/4/3 3:31 下午
 */
@Mapper
public interface ReportMapper {

    /**
     * 更新report(has_handled、handled_time等字段)
     * @param report            report
     */
    void update(Report report);

    /**
     * 通过reportId查找
     * @param reportId          reportId
     * @return                  Report
     */
    Report findById(Integer reportId);

    /**
     * 查找所有的Report（未删除）
     * @param beginIndex        beginIndex
     * @param pageSize          pageSize
     * @return                  List Complaint
     */
    List<Report> findAllByPage(Integer beginIndex, Integer pageSize, String titleKey);

    /**
     * 获取所有未删除的Complaint数量
     * @return                  未删除的Report数量
     */
    Integer getAllCount(String titleKey);
}
