package cn.edu.hestyle.bookstadium.entity;

import cn.edu.hestyle.bookstadium.util.ResponseResult;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/4/3 2:54 下午
 */
public class Report {
    /** 账号类型 */
    public static final Integer REPORT_ACCOUNT_TYPE_USER = 1;
    public static final Integer REPORT_ACCOUNT_TYPE_STADIUM_MANAGER = 2;
    /** 内容类型 */
    public static final Integer REPORT_CONTENT_TYPE_SPORT_MOMENT = 3;
    public static final Integer REPORT_CONTENT_TYPE_SPORT_MOMENT_COMMENT = 4;
    public static final Integer REPORT_CONTENT_TYPE_STADIUM = 5;
    public static final Integer REPORT_CONTENT_TYPE_STADIUM_COMMENT = 6;


    /** id */
    private Integer id;
    /** 举报人账号类型 */
    private Integer reporterAccountType;
    /** 举报人人账号id */
    private Integer reporterAccountId;
    /** 被举报内容类型 */
    private Integer reportContentType;
    /** 被举报内容id，可能是动态、评论id */
    private Integer reportContentId;
    /** 被举报人账号类型User、StadiumManager等 */
    private Integer respondentAccountType;
    /** 被举报人的账号id */
    private Integer respondentAccountId;
    /** title */
    private String title;
    /** description */
    private String description;
    /** imagePaths */
    private String imagePaths;
    /** 举报时间 */
    @JsonFormat(pattern = ResponseResult.DATETIME_FORMAT, timezone = "GMT+8")
    private Date reportedTime;
    /** 是否已处理 */
    private Integer hasHandled;
    /** 举报处理时间 */
    @JsonFormat(pattern = ResponseResult.DATETIME_FORMAT, timezone = "GMT+8")
    private Date handledTime;
    /** complainant处理分数 */
    private Integer reporterHandleCreditScore;
    /** complainant处理描述 */
    private String reporterHandleDescription;
    /** respondent处理分数 */
    private Integer respondentHandleCreditScore;
    /** respondent处理描述 */
    private String respondentHandleDescription;
    /** 举报内容是否删除 */
    private Integer reportContentDelete;
    /** 举报内容处理描述 */
    private String reportContentHandleDescription;
    /** 是否删除，0未删除，1已删除 */
    private Integer isDelete;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getReporterAccountType() {
        return reporterAccountType;
    }

    public void setReporterAccountType(Integer reporterAccountType) {
        this.reporterAccountType = reporterAccountType;
    }

    public Integer getReporterAccountId() {
        return reporterAccountId;
    }

    public void setReporterAccountId(Integer reporterAccountId) {
        this.reporterAccountId = reporterAccountId;
    }

    public Integer getReportContentType() {
        return reportContentType;
    }

    public void setReportContentType(Integer reportContentType) {
        this.reportContentType = reportContentType;
    }

    public Integer getReportContentId() {
        return reportContentId;
    }

    public void setReportContentId(Integer reportContentId) {
        this.reportContentId = reportContentId;
    }

    public Integer getRespondentAccountType() {
        return respondentAccountType;
    }

    public void setRespondentAccountType(Integer respondentAccountType) {
        this.respondentAccountType = respondentAccountType;
    }

    public Integer getRespondentAccountId() {
        return respondentAccountId;
    }

    public void setRespondentAccountId(Integer respondentAccountId) {
        this.respondentAccountId = respondentAccountId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePaths() {
        return imagePaths;
    }

    public void setImagePaths(String imagePaths) {
        this.imagePaths = imagePaths;
    }

    public Date getReportedTime() {
        return reportedTime;
    }

    public void setReportedTime(Date reportedTime) {
        this.reportedTime = reportedTime;
    }

    public Integer getHasHandled() {
        return hasHandled;
    }

    public void setHasHandled(Integer hasHandled) {
        this.hasHandled = hasHandled;
    }

    public Date getHandledTime() {
        return handledTime;
    }

    public void setHandledTime(Date handledTime) {
        this.handledTime = handledTime;
    }

    public Integer getReporterHandleCreditScore() {
        return reporterHandleCreditScore;
    }

    public void setReporterHandleCreditScore(Integer reporterHandleCreditScore) {
        this.reporterHandleCreditScore = reporterHandleCreditScore;
    }

    public String getReporterHandleDescription() {
        return reporterHandleDescription;
    }

    public void setReporterHandleDescription(String reporterHandleDescription) {
        this.reporterHandleDescription = reporterHandleDescription;
    }

    public Integer getRespondentHandleCreditScore() {
        return respondentHandleCreditScore;
    }

    public void setRespondentHandleCreditScore(Integer respondentHandleCreditScore) {
        this.respondentHandleCreditScore = respondentHandleCreditScore;
    }

    public String getRespondentHandleDescription() {
        return respondentHandleDescription;
    }

    public void setRespondentHandleDescription(String respondentHandleDescription) {
        this.respondentHandleDescription = respondentHandleDescription;
    }

    public Integer getReportContentDelete() {
        return reportContentDelete;
    }

    public void setReportContentDelete(Integer reportContentDelete) {
        this.reportContentDelete = reportContentDelete;
    }

    public String getReportContentHandleDescription() {
        return reportContentHandleDescription;
    }

    public void setReportContentHandleDescription(String reportContentHandleDescription) {
        this.reportContentHandleDescription = reportContentHandleDescription;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", reporterAccountType=" + reporterAccountType +
                ", reporterAccountId=" + reporterAccountId +
                ", reportContentType=" + reportContentType +
                ", reportContentId=" + reportContentId +
                ", respondentAccountType=" + respondentAccountType +
                ", respondentAccountId=" + respondentAccountId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", imagePaths='" + imagePaths + '\'' +
                ", reportedTime=" + reportedTime +
                ", hasHandled=" + hasHandled +
                ", handledTime=" + handledTime +
                ", reporterHandleCreditScore=" + reporterHandleCreditScore +
                ", reporterHandleDescription='" + reporterHandleDescription + '\'' +
                ", respondentHandleCreditScore=" + respondentHandleCreditScore +
                ", respondentHandleDescription='" + respondentHandleDescription + '\'' +
                ", reportContentDelete=" + reportContentDelete +
                ", reportContentHandleDescription='" + reportContentHandleDescription + '\'' +
                ", isDelete=" + isDelete +
                '}';
    }

    /**
     * report to reportVO
     * @return      reportVO
     */
    public ReportVO toReportVO() {
        ReportVO reportVO = new ReportVO();
        reportVO.setId(id);
        reportVO.setReporterAccountType(reporterAccountType);
        reportVO.setReporterAccountId(reporterAccountId);
        reportVO.setReportContentType(reportContentType);
        reportVO.setReportContentId(reportContentId);
        reportVO.setRespondentAccountType(respondentAccountType);
        reportVO.setRespondentAccountId(respondentAccountId);
        reportVO.setTitle(title);
        reportVO.setDescription(description);
        reportVO.setImagePaths(imagePaths);
        reportVO.setReportedTime(reportedTime);
        reportVO.setHasHandled(hasHandled);
        reportVO.setHandledTime(handledTime);
        reportVO.setReporterHandleCreditScore(reporterHandleCreditScore);
        reportVO.setReporterHandleDescription(reporterHandleDescription);
        reportVO.setRespondentHandleCreditScore(respondentHandleCreditScore);
        reportVO.setRespondentHandleDescription(respondentHandleDescription);
        reportVO.setReportContentDelete(reportContentDelete);
        reportVO.setReportContentHandleDescription(reportContentHandleDescription);
        reportVO.setIsDelete(isDelete);
        return reportVO;
    }
}
