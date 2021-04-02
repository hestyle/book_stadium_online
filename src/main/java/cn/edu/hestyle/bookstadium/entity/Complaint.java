package cn.edu.hestyle.bookstadium.entity;

import cn.edu.hestyle.bookstadium.util.ResponseResult;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/4/2 9:51 上午
 */
public class Complaint {
    /** 账号类型 */
    public static final Integer COMPLAIN_ACCOUNT_TYPE_USER = 1;
    public static final Integer COMPLAIN_ACCOUNT_TYPE_STADIUM_MANAGER = 2;
    /** 内容类型 */
    public static final Integer COMPLAIN_CONTENT_TYPE_STADIUM = 3;
    public static final Integer COMPLAIN_CONTENT_TYPE_STADIUM_COMMENT = 4;
    public static final Integer COMPLAIN_CONTENT_TYPE_SPORT_MOMENT = 5;
    public static final Integer COMPLAIN_CONTENT_TYPE_SPORT_MOMENT_COMMENT = 6;

    /** id */
    private Integer id;
    /** 投诉人账号类型 */
    private Integer complainantAccountType;
    /** 投诉人账号id，可能是用户id，也可能是动态、评论id */
    private Integer complainantAccountId;
    /** 被投诉人账号类型User、StadiumManager等 */
    private Integer respondentAccountType;
    /** 被投诉人的账号id */
    private Integer respondentAccountId;
    /** title */
    private String title;
    /** description */
    private String description;
    /** imagePaths */
    private String imagePaths;
    /** 投诉时间 */
    @JsonFormat(pattern = ResponseResult.DATETIME_FORMAT, timezone = "GMT+8")
    private Date complainedTime;
    /** 是否已处理 */
    private Integer hasHandled;
    /** 投诉处理时间 */
    @JsonFormat(pattern = ResponseResult.DATETIME_FORMAT, timezone = "GMT+8")
    private Date handledTime;
    /** complainant处理分数 */
    private Integer complainantHandleCreditScore;
    /** complainant处理描述 */
    private String complainantHandleDescription;
    /** respondent处理分数 */
    private Integer respondentHandleCreditScore;
    /** respondent处理描述 */
    private String respondentHandleDescription;
    /** 是否删除，0未删除，1已删除 */
    private Integer isDelete;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getComplainantAccountType() {
        return complainantAccountType;
    }

    public void setComplainantAccountType(Integer complainantAccountType) {
        this.complainantAccountType = complainantAccountType;
    }

    public Integer getComplainantAccountId() {
        return complainantAccountId;
    }

    public void setComplainantAccountId(Integer complainantAccountId) {
        this.complainantAccountId = complainantAccountId;
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

    public Date getComplainedTime() {
        return complainedTime;
    }

    public void setComplainedTime(Date complainedTime) {
        this.complainedTime = complainedTime;
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

    public Integer getComplainantHandleCreditScore() {
        return complainantHandleCreditScore;
    }

    public void setComplainantHandleCreditScore(Integer complainantHandleCreditScore) {
        this.complainantHandleCreditScore = complainantHandleCreditScore;
    }

    public String getComplainantHandleDescription() {
        return complainantHandleDescription;
    }

    public void setComplainantHandleDescription(String complainantHandleDescription) {
        this.complainantHandleDescription = complainantHandleDescription;
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

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    @Override
    public String toString() {
        return "Complaint{" +
                "id=" + id +
                ", complainantAccountType=" + complainantAccountType +
                ", complainantAccountId=" + complainantAccountId +
                ", respondentAccountType=" + respondentAccountType +
                ", respondentAccountId=" + respondentAccountId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", imagePaths='" + imagePaths + '\'' +
                ", complainedTime=" + complainedTime +
                ", hasHandled=" + hasHandled +
                ", handledTime=" + handledTime +
                ", complainantHandleCreditScore=" + complainantHandleCreditScore +
                ", complainantHandleDescription='" + complainantHandleDescription + '\'' +
                ", respondentHandleCreditScore=" + respondentHandleCreditScore +
                ", respondentHandleDescription='" + respondentHandleDescription + '\'' +
                ", isDelete=" + isDelete +
                '}';
    }

    /**
     * 转成ComplaintVO
     * @return          ComplaintVO
     */
    public ComplaintVO toComplaintVO() {
        ComplaintVO complaintVO = new ComplaintVO();
        complaintVO.setId(id);
        complaintVO.setComplainantAccountType(complainantAccountType);
        complaintVO.setComplainantAccountId(complainantAccountId);
        complaintVO.setRespondentAccountType(respondentAccountType);
        complaintVO.setRespondentAccountId(respondentAccountId);
        complaintVO.setTitle(title);
        complaintVO.setDescription(description);
        complaintVO.setImagePaths(imagePaths);
        complaintVO.setComplainedTime(complainedTime);
        complaintVO.setHasHandled(hasHandled);
        complaintVO.setHandledTime(handledTime);
        complaintVO.setComplainantHandleCreditScore(complainantHandleCreditScore);
        complaintVO.setComplainantHandleDescription(complainantHandleDescription);
        complaintVO.setRespondentHandleCreditScore(respondentHandleCreditScore);
        complaintVO.setRespondentHandleDescription(respondentHandleDescription);
        complaintVO.setIsDelete(isDelete);
        return complaintVO;
    }
}
