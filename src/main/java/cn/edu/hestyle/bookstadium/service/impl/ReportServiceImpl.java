package cn.edu.hestyle.bookstadium.service.impl;

import cn.edu.hestyle.bookstadium.entity.*;
import cn.edu.hestyle.bookstadium.mapper.*;
import cn.edu.hestyle.bookstadium.service.IReportService;
import cn.edu.hestyle.bookstadium.service.exception.DeleteFailedException;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/4/3 4:18 下午
 */
@Service
public class ReportServiceImpl implements IReportService {
    /** 举报人处理title、content */
    private static final String REPORTER_HANDLE_TITLE = "举报处理";
    private static final String REPORTER_HANDLE_CONTENT = "系统已处理您的举报【%s】，积分奖惩【%s】，奖惩描述【%s】";
    /** 被举报人处理title、content */
    private static final String RESPONDENT_HANDLE_TITLE = "举报处理";
    private static final String RESPONDENT_HANDLE_CONTENT = "您的账号被其他用户举报【%s...】，积分奖惩【%s】，奖惩描述【%s】";
    /** 被举报内容删除title、content */
    private static final String REPORT_CONTENT_DELETE_TITLE = "举报内容删除";
    private static final String REPORT_CONTENT_DELETE_CONTENT = "您的【%s】【%s】被其他用户举报【%s】已被系统删除，删除原因【%s】";
    /** （未处理）投诉删除title、content */
    private static final String REPORT_DELETE_TITLE = "举报删除";
    private static final String REPORT_DELETE_CONTENT = "您的举报【%s】已被系统删除，删除原因【%s】";

    private static final Logger logger = LoggerFactory.getLogger(ReportServiceImpl.class);

    @Resource
    private UserMapper userMapper;
    @Resource
    private ReportMapper reportMapper;
    @Resource
    private NoticeMapper noticeMapper;
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
    @Transactional
    public void systemManagerHandle(Report report) {
        if (report == null || report.getId() == null) {
            logger.warn("Report 处理失败，未指定需要处理的Report！");
            throw new FindFailedException("操作失败，未指定需要处理的Report！");
        }
        Report reportModify = null;
        try {
            reportModify = reportMapper.findById(report.getId());
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Report 查找失败，数据库发生未知错误！reportModifyId = " + report.getId());
            throw new FindFailedException("操作失败，数据库发生未知错误！");
        }
        if (reportModify == null || reportModify.getIsDelete() != 0) {
            logger.warn("Report 处理失败，report不存在或已被删除！reportModify = " + reportModify);
            throw new FindFailedException("操作失败，report不存在或已被删除！");
        }
        if (!reportModify.getHasHandled().equals(0)) {
            logger.warn("Report 处理失败，尝试重复处理report！reportModify = " + reportModify);
            throw new FindFailedException("操作失败，该report已经处理过了！");
        }
        // 检查举报人的奖惩
        Integer reporterHandleCreditScore = report.getReporterHandleCreditScore();
        String reporterHandleDescription = report.getReporterHandleDescription();
        reportModify.setReporterHandleCreditScore(reporterHandleCreditScore);
        reportModify.setReporterHandleDescription(reporterHandleDescription);
        if (reporterHandleCreditScore == null) {
            logger.warn("Report 处理失败，未指定对举报人的奖惩积分数！report = " + report);
            throw new FindFailedException("操作失败，未指定对举报人的奖惩积分数！");
        }
        if (reporterHandleDescription == null || reporterHandleDescription.length() == 0) {
            logger.warn("Report 处理失败，未指定对举报人的奖惩描述！report = " + report);
            throw new FindFailedException("操作失败，未填写对举报人的奖惩描述！");
        }
        if (!reporterHandleCreditScore.equals(0)) {
            // 判断举报人的类型，并更新积分
            if (Report.REPORT_ACCOUNT_TYPE_USER.equals(reportModify.getReporterAccountType())) {
                User reporterUser = null;
                try {
                    reporterUser = userMapper.findById(reportModify.getReporterAccountId());
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.warn("User 查找失败，数据库发生未知错误！reporterUserId = " + reportModify.getReporterAccountId());
                    throw new FindFailedException("操作失败，数据库发生未知错误！");
                }
                if (reporterUser != null) {
                    reporterUser.setCreditScore(reporterUser.getCreditScore() + reporterHandleCreditScore);
                    reporterUser.setModifiedUser("System_Report");
                    reporterUser.setModifiedTime(new Date());
                    try {
                        userMapper.update(reporterUser);
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.warn("User 更新失败，数据库发生未知错误！reporterUser = " + reporterUser);
                        throw new FindFailedException("操作失败，数据库发生未知错误！");
                    }
                    logger.warn("User 更新成功！reporterUser = " + reporterUser);
                }
            } else if (Report.REPORT_ACCOUNT_TYPE_STADIUM_MANAGER.equals(reportModify.getReporterAccountType())) {
                StadiumManager reporterStadiumManager = null;
                try {
                    reporterStadiumManager = stadiumManagerMapper.findById(reportModify.getReporterAccountId());
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.warn("StadiumManager 查找失败，数据库发生未知错误！reporterStadiumManagerId = " + reportModify.getReporterAccountId());
                    throw new FindFailedException("操作失败，数据库发生未知错误！");
                }
                if (reporterStadiumManager != null) {
                    reporterStadiumManager.setCreditScore(reporterStadiumManager.getCreditScore() + reporterHandleCreditScore);
                    reporterStadiumManager.setModifiedUser("System_Report");
                    reporterStadiumManager.setModifiedTime(new Date());
                    try {
                        stadiumManagerMapper.update(reporterStadiumManager);
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.warn("StadiumManager 更新失败，数据库发生未知错误！respondentStadiumManager = " + reporterStadiumManager);
                        throw new FindFailedException("操作失败，数据库发生未知错误！");
                    }
                    logger.warn("StadiumManager 更新成功！respondentStadiumManager = " + reporterStadiumManager);
                }
            }
        }
        // 发送通知
        Notice reporterNotice = new Notice();
        reporterNotice.setToAccountType(reportModify.getReporterAccountType() - 1);
        reporterNotice.setAccountId(reportModify.getReporterAccountId());
        reporterNotice.setTitle(REPORTER_HANDLE_TITLE);
        reporterNotice.setContent(String.format(REPORTER_HANDLE_CONTENT, reportModify.getTitle(), report.getReporterHandleCreditScore(), report.getReporterHandleDescription()));
        reporterNotice.setGeneratedTime(new Date());
        reporterNotice.setIsDelete(0);
        try {
            noticeMapper.add(reporterNotice);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Notice 添加失败，数据库发生未知错误！reporterNotice = " + reporterNotice);
            throw new FindFailedException("操作失败，数据库发生未知错误！");
        }
        logger.warn("Notice 添加成功！reporterNotice = " + reporterNotice);
        // 检查被举报人奖惩
        Integer respondentHandleCreditScore = report.getRespondentHandleCreditScore();
        String respondentHandleDescription = report.getRespondentHandleDescription();
        reportModify.setRespondentHandleCreditScore(respondentHandleCreditScore);
        reportModify.setRespondentHandleDescription(respondentHandleDescription);
        if (respondentHandleCreditScore == null) {
            logger.warn("Report 处理失败，未指定对被举报人的奖惩积分数！report = " + report);
            throw new FindFailedException("操作失败，未指定对被举报人的奖惩积分数！");
        }
        if (!respondentHandleCreditScore.equals(0)) {
            if (respondentHandleDescription == null || respondentHandleDescription.length() == 0) {
                logger.warn("Report 处理失败，未指定对被举报人的奖惩描述！report = " + report);
                throw new FindFailedException("操作失败，未填写对被举报人的奖惩描述！");
            }
            // 更新用户积分
            if (Report.REPORT_ACCOUNT_TYPE_USER.equals(reportModify.getRespondentAccountType())) {
                User respondentUser = null;
                try {
                    respondentUser = userMapper.findById(reportModify.getRespondentAccountId());
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.warn("User 查找失败，数据库发生未知错误！respondentUserId = " + reportModify.getRespondentAccountId());
                    throw new FindFailedException("操作失败，数据库发生未知错误！");
                }
                if (respondentUser != null) {
                    respondentUser.setCreditScore(respondentUser.getCreditScore() + respondentHandleCreditScore);
                    respondentUser.setModifiedUser("System_Report");
                    respondentUser.setModifiedTime(new Date());
                    try {
                        userMapper.update(respondentUser);
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.warn("User 更新失败，数据库发生未知错误！respondentUser = " + respondentUser);
                        throw new FindFailedException("操作失败，数据库发生未知错误！");
                    }
                    logger.warn("User 更新成功！respondentUser = " + respondentUser);
                }
            } else if (Report.REPORT_ACCOUNT_TYPE_STADIUM_MANAGER.equals(reportModify.getRespondentAccountType())) {
                StadiumManager respondentStadiumManager = null;
                try {
                    respondentStadiumManager = stadiumManagerMapper.findById(reportModify.getRespondentAccountId());
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.warn("StadiumManager 查找失败，数据库发生未知错误！respondentStadiumManagerId = " + reportModify.getRespondentAccountId());
                    throw new FindFailedException("操作失败，数据库发生未知错误！");
                }
                if (respondentStadiumManager != null) {
                    respondentStadiumManager.setCreditScore(respondentStadiumManager.getCreditScore() + respondentHandleCreditScore);
                    respondentStadiumManager.setModifiedUser("System_Report");
                    respondentStadiumManager.setModifiedTime(new Date());
                    try {
                        stadiumManagerMapper.update(respondentStadiumManager);
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.warn("StadiumManager 更新失败，数据库发生未知错误！respondentStadiumManager = " + respondentStadiumManager);
                        throw new FindFailedException("操作失败，数据库发生未知错误！");
                    }
                    logger.warn("StadiumManager 更新成功！respondentStadiumManager = " + respondentStadiumManager);
                }
            }
        }
        if (respondentHandleDescription != null && respondentHandleDescription.length() != 0) {
            // 发送通知
            Notice respondentNotice = new Notice();
            reporterNotice.setToAccountType(reportModify.getRespondentAccountType() - 1);
            reporterNotice.setAccountId(reportModify.getRespondentAccountId());
            reporterNotice.setTitle(RESPONDENT_HANDLE_TITLE);
            reporterNotice.setContent(String.format(RESPONDENT_HANDLE_CONTENT, reportModify.getTitle(), report.getRespondentHandleCreditScore(), report.getRespondentHandleDescription()));
            reporterNotice.setGeneratedTime(new Date());
            reporterNotice.setIsDelete(0);
            try {
                noticeMapper.add(reporterNotice);
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("Notice 添加失败，数据库发生未知错误！reporterNotice = " + reporterNotice);
                throw new FindFailedException("操作失败，数据库发生未知错误！");
            }
            logger.warn("Notice 添加成功！reporterNotice = " + reporterNotice);
        }
        // 是否删除了被举报的内容
        Integer reportContentDelete = report.getReportContentDelete();
        String reportContentHandleDescription = report.getReportContentHandleDescription();
        reportModify.setReportContentDelete(reportContentDelete);
        reportModify.setReportContentHandleDescription(reportContentHandleDescription);
        if (reportContentDelete != null && reportContentDelete.equals(1)) {
            if (reportContentHandleDescription == null || reportContentHandleDescription.length() == 0) {
                logger.warn("Report 处理失败，未指定对被举报内容操作描述！report = " + report);
                throw new FindFailedException("操作失败，未填写对被举报内容操作描述！");
            }
            String contentTypeTitle = "";
            String content = "";
            // 删除了被举报的内容
            if (Report.REPORT_CONTENT_TYPE_SPORT_MOMENT.equals(reportModify.getReportContentType())) {
                // 举报的SportMoment
                SportMoment sportMoment = null;
                try {
                    sportMoment = sportMomentMapper.findById(reportModify.getReportContentId());
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.warn("SportMoment 查找失败，数据库发生未知错误！sportMomentId = " + reportModify.getReportContentId());
                    throw new FindFailedException("查找失败，数据库发生未知错误！");
                }
                if (sportMoment != null) {
                    contentTypeTitle = "运动动态";
                    content = sportMoment.getContent();
                    sportMoment.setIsDelete(1);
                    sportMoment.setModifiedUser("System_Report");
                    sportMoment.setModifiedTime(new Date());
                    try {
                        sportMomentMapper.update(sportMoment);
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.warn("SportMoment 更新失败，数据库发生未知错误！sportMoment = " + sportMoment);
                        throw new FindFailedException("查找失败，数据库发生未知错误！");
                    }
                    logger.warn("SportMoment 更新成功！sportMoment = " + sportMoment);
                }
            } else if (Report.REPORT_CONTENT_TYPE_SPORT_MOMENT_COMMENT.equals(reportModify.getReportContentType())) {
                // 举报的SportMomentComment
                SportMomentComment sportMomentComment = null;
                try {
                    sportMomentComment = sportMomentCommentMapper.findById(reportModify.getReportContentId());
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.warn("SportMomentComment 查找失败，数据库发生未知错误！sportMomentCommentId = " + reportModify.getReportContentId());
                    throw new FindFailedException("查找失败，数据库发生未知错误！");
                }
                if (sportMomentComment != null) {
                    contentTypeTitle = "运动动态评论";
                    content = sportMomentComment.getContent();
                    sportMomentComment.setIsDelete(1);
                    try {
                        sportMomentCommentMapper.update(sportMomentComment);
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.warn("SportMomentComment 更新失败，数据库发生未知错误！sportMomentComment = " + sportMomentComment);
                        throw new FindFailedException("查找失败，数据库发生未知错误！");
                    }
                    logger.warn("SportMomentComment 更新成功！sportMomentComment = " + sportMomentComment);
                }
            } else if (Report.REPORT_CONTENT_TYPE_STADIUM_COMMENT.equals(reportModify.getReportContentType())) {
                // 举报的StadiumComment
                StadiumComment stadiumComment = null;
                try {
                    stadiumComment = stadiumCommentMapper.findByStadiumCommentId(reportModify.getReportContentId());
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.warn("StadiumComment 查找失败，数据库发生未知错误！stadiumCommentId = " + reportModify.getReportContentId());
                    throw new FindFailedException("查找失败，数据库发生未知错误！");
                }
                if (stadiumComment != null) {
                    contentTypeTitle = "体育场馆评论";
                    content = stadiumComment.getContent();
                    stadiumComment.setIsDelete(1);
                    try {
                        stadiumCommentMapper.update(stadiumComment);
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.warn("StadiumComment 更新失败，数据库发生未知错误！stadiumComment = " + stadiumComment);
                        throw new FindFailedException("查找失败，数据库发生未知错误！");
                    }
                    logger.warn("StadiumComment 更新成功！stadiumComment = " + stadiumComment);
                }
            }
            if (content != null && content.length() > 20) {
                content = content.substring(0, 20) + "...";
            }
            // 通知被举报人
            Notice reportContentDeleteNotice = new Notice();
            reportContentDeleteNotice.setToAccountType(reportModify.getRespondentAccountType() - 1);
            reportContentDeleteNotice.setAccountId(reportModify.getRespondentAccountId());
            reportContentDeleteNotice.setTitle(REPORT_CONTENT_DELETE_TITLE);
            reportContentDeleteNotice.setContent(String.format(REPORT_CONTENT_DELETE_CONTENT, contentTypeTitle, content, reportModify.getTitle(), report.getReportContentHandleDescription()));
            reportContentDeleteNotice.setGeneratedTime(new Date());
            reportContentDeleteNotice.setIsDelete(0);
            try {
                noticeMapper.add(reportContentDeleteNotice);
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("Notice 添加失败，数据库发生未知错误！reportContentDeleteNotice = " + reportContentDeleteNotice);
                throw new FindFailedException("操作失败，数据库发生未知错误！");
            }
            logger.warn("Notice 添加成功！reportContentDeleteNotice = " + reportContentDeleteNotice);
        }
        // 更新report
        reportModify.setHasHandled(1);
        reportModify.setHandledTime(new Date());
        try {
            reportMapper.update(reportModify);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Report 更新失败，数据库发生未知错误！reportModify = " + reportModify);
            throw new FindFailedException("操作失败，数据库发生未知错误！");
        }
        logger.warn("Report 更新成功！reportModify = " + reportModify);
    }

    @Override
    @Transactional
    public void systemManagerDeleteById(Integer reportId, String deleteReason) {
        if (reportId == null) {
            logger.warn("Report 删除失败，未指定需要删除的举报！");
            throw new DeleteFailedException("操作失败，未指定需要删除的投诉！");
        }
        Report report = null;
        try {
            report = reportMapper.findById(reportId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Report 查找失败，数据库发生未知错误！reportId = " + reportId);
            throw new FindFailedException("查找失败，数据库发生未知错误！");
        }
        if (report == null) {
            logger.warn("Report 删除失败，不存在该投诉！reportId = " + reportId);
            throw new FindFailedException("操作失败，不存在该投诉！");
        }
        if (deleteReason == null || deleteReason.length() == 0) {
            logger.warn("Report 删除失败，未设置删除原因！");
            throw new FindFailedException("操作失败，未设置删除原因！");
        }
        if (report.getIsDelete() == 0 && report.getHasHandled() == 0) {
            // 当前report未处理，通知投诉人
            Notice reporterNotice = new Notice();
            reporterNotice.setToAccountType(report.getReporterAccountType() - 1);
            reporterNotice.setAccountId(report.getReporterAccountId());
            reporterNotice.setTitle(REPORT_DELETE_TITLE);
            reporterNotice.setContent(String.format(REPORT_DELETE_CONTENT, report.getTitle(), deleteReason));
            reporterNotice.setGeneratedTime(new Date());
            reporterNotice.setIsDelete(0);
            try {
                noticeMapper.add(reporterNotice);
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("Notice 添加失败，数据库发生未知错误！reporterNotice = " + reporterNotice);
                throw new FindFailedException("操作失败，数据库发生未知错误！");
            }
            logger.warn("Notice 添加成功！reporterNotice = " + reporterNotice);
        }
        report.setIsDelete(1);
        try {
            reportMapper.update(report);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Report 更新失败，数据库发生未知错误！report = " + report);
            throw new FindFailedException("操作失败，数据库发生未知错误！");
        }
        logger.warn("Report 更新成功！report = " + report);
    }

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
