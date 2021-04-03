package cn.edu.hestyle.bookstadium.service.impl;

import cn.edu.hestyle.bookstadium.entity.*;
import cn.edu.hestyle.bookstadium.mapper.*;
import cn.edu.hestyle.bookstadium.service.IReportService;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/4/3 4:18 下午
 */
@Service
public class ReportServiceImpl implements IReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportServiceImpl.class);

    @Resource
    private UserMapper userMapper;
    @Resource
    private ReportMapper reportMapper;
    @Resource
    private StadiumMapper stadiumMapper;
    @Resource
    private StadiumCommentMapper stadiumCommentMapper;
    @Resource
    private StadiumManagerMapper stadiumManagerMapper;
    @Resource
    private SportMomentMapper sportMomentMapper;
    @Resource
    private SportMomentCommentMapper sportMomentCommentMapper;

    @Override
    public List<ReportVO> systemManagerFindAllByPage(Integer pageIndex, Integer pageSize, String titleKey) {
        // 检查页码是否合法
        if (pageIndex < 1) {
            throw new FindFailedException("查询失败，页码 " + pageIndex + " 非法，必须大于0！");
        }
        // 检查页大小是否合法
        if (pageSize < 1) {
            throw new FindFailedException("查询失败，页大小 " + pageSize + " 非法，必须大于0！");
        }
        // 去除titleKey中的特殊字符
        if (titleKey != null && titleKey.length() != 0) {
            titleKey = titleKey.replaceAll("%", "").replaceAll("'", "").replaceAll("\\?", "");
        }
        List<Report> reportList = null;
        try {
            reportList = reportMapper.findAllByPage((pageIndex - 1) * pageSize, pageSize, titleKey);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Report 查找失败，数据库发生未知错误！pageIndex = " + pageIndex + "，pageSize = " + pageSize);
            throw new FindFailedException("查找失败，数据库发生未知错误！");
        }
        List<ReportVO> reportVOList = new ArrayList<>();
        if (reportList != null && reportList.size() != 0) {
            for (Report report : reportList) {
                reportVOList.add(toReportVO(report));
            }
        }
        return reportVOList;
    }

    @Override
    public Integer getAllCount(String titleKey) {
        // 去除titleKey中的特殊字符
        if (titleKey != null && titleKey.length() != 0) {
            titleKey = titleKey.replaceAll("%", "").replaceAll("'", "").replaceAll("\\?", "");
        }
        Integer count = 0;
        try {
            count = reportMapper.getAllCount(titleKey);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Report 数量查找失败，数据库发生未知错误！");
            throw new FindFailedException("查找失败，数据库发生未知错误！");
        }
        return count;
    }

    /**
     * report to reportVO（填充举报人、被举报人、处罚账号信息）
     * @param report        report
     * @return              reportVO
     */
    private ReportVO toReportVO(Report report) {
        ReportVO reportVO = report.toReportVO();
        // 填充投诉人账号信息
        if (reportVO.getReporterAccountType().equals(Report.REPORT_ACCOUNT_TYPE_USER)) {
            User reporterUser = null;
            try {
                reporterUser = userMapper.findById(reportVO.getReporterAccountId());
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("User 查找失败，数据库发生未知错误！reporterUserId = " + reportVO.getReporterAccountId());
                throw new FindFailedException("查找失败，数据库发生未知错误！");
            }
            if (reporterUser != null) {
                reportVO.setReporterUsername(reporterUser.getUsername());
                reportVO.setReporterAvatarPath(reporterUser.getAvatarPath());
            }
        } else if (reportVO.getReporterAccountType().equals(Report.REPORT_ACCOUNT_TYPE_STADIUM_MANAGER)) {
            StadiumManager reporterStadiumManager = null;
            try {
                reporterStadiumManager = stadiumManagerMapper.findById(reportVO.getReporterAccountType());
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("StadiumManager 查找失败，数据库发生未知错误！reporterStadiumManagerId = " + reportVO.getReporterAccountType());
                throw new FindFailedException("查找失败，数据库发生未知错误！");
            }
            if (reporterStadiumManager != null) {
                reportVO.setReporterUsername(reporterStadiumManager.getUsername());
                reportVO.setReporterAvatarPath(reporterStadiumManager.getAvatarPath());
            }
        }
        // 填充被投诉人账号信息
        if (reportVO.getRespondentAccountType().equals(Report.REPORT_ACCOUNT_TYPE_USER)) {
            User respondentUser = null;
            try {
                respondentUser = userMapper.findById(reportVO.getRespondentAccountId());
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("User 查找失败，数据库发生未知错误！respondentUserId = " + reportVO.getRespondentAccountId());
                throw new FindFailedException("查找失败，数据库发生未知错误！");
            }
            if (respondentUser != null) {
                reportVO.setRespondentUsername(respondentUser.getUsername());
                reportVO.setRespondentAvatarPath(respondentUser.getAvatarPath());
            }
        } else if (reportVO.getRespondentAccountType().equals(Report.REPORT_ACCOUNT_TYPE_STADIUM_MANAGER)) {
            StadiumManager respondentStadiumManager = null;
            try {
                respondentStadiumManager = stadiumManagerMapper.findById(reportVO.getRespondentAccountId());
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("StadiumManager 查找失败，数据库发生未知错误！respondentStadiumManagerId = " + reportVO.getRespondentAccountId());
                throw new FindFailedException("查找失败，数据库发生未知错误！");
            }
            if (respondentStadiumManager != null) {
                reportVO.setRespondentUsername(respondentStadiumManager.getUsername());
                reportVO.setRespondentAvatarPath(respondentStadiumManager.getAvatarPath());
            }
        }
        // 填充举报内容
        if (Report.REPORT_CONTENT_TYPE_SPORT_MOMENT.equals(reportVO.getReportContentType())) {
            // 举报的SportMoment
            SportMoment sportMoment = null;
            try {
                sportMoment = sportMomentMapper.findById(reportVO.getReportContentId());
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("SportMoment 查找失败，数据库发生未知错误！sportMomentId = " + reportVO.getReportContentId());
                throw new FindFailedException("查找失败，数据库发生未知错误！");
            }
            if (sportMoment != null) {
                reportVO.setReportContentObject(sportMoment);
            }
        } else if (Report.REPORT_CONTENT_TYPE_SPORT_MOMENT_COMMENT.equals(reportVO.getReportContentType())) {
            // 举报的SportMomentComment
            SportMomentComment sportMomentComment = null;
            try {
                sportMomentComment = sportMomentCommentMapper.findById(reportVO.getReportContentId());
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("SportMomentComment 查找失败，数据库发生未知错误！sportMomentCommentId = " + reportVO.getReportContentId());
                throw new FindFailedException("查找失败，数据库发生未知错误！");
            }
            if (sportMomentComment != null) {
                reportVO.setReportContentObject(sportMomentComment);
            }
        } else if (Report.REPORT_CONTENT_TYPE_STADIUM.equals(reportVO.getReportContentType())) {
            // 举报的Stadium
            Stadium stadium = null;
            try {
                stadium = stadiumMapper.findById(reportVO.getReportContentId());
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("Stadium 查找失败，数据库发生未知错误！stadiumId = " + reportVO.getReportContentId());
                throw new FindFailedException("查找失败，数据库发生未知错误！");
            }
            if (stadium != null) {
                reportVO.setReportContentObject(stadium);
            }
        } else if (Report.REPORT_CONTENT_TYPE_STADIUM_COMMENT.equals(reportVO.getReportContentType())) {
            // 举报的StadiumComment
            StadiumComment stadiumComment = null;
            try {
                stadiumComment = stadiumCommentMapper.findByStadiumCommentId(reportVO.getReportContentId());
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("StadiumComment 查找失败，数据库发生未知错误！stadiumCommentId = " + reportVO.getReportContentId());
                throw new FindFailedException("查找失败，数据库发生未知错误！");
            }
            if (stadiumComment != null) {
                reportVO.setReportContentObject(stadiumComment);
            }
        }
        return reportVO;
    }
}
