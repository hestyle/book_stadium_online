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
    /** 处罚账号类型 */
    private Integer punishAccountType;
    /** 处罚账号对应的id，可能是用户id，也可能是动态、评论id */
    private Integer punishAccountId;
    /** 处罚账号减分数 */
    private Integer punishCreditScore;
    /** punishDescription */
    private String punishDescription;
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

    public Integer getPunishAccountType() {
        return punishAccountType;
    }

    public void setPunishAccountType(Integer punishAccountType) {
        this.punishAccountType = punishAccountType;
    }

    public Integer getPunishAccountId() {
        return punishAccountId;
    }

    public void setPunishAccountId(Integer punishAccountId) {
        this.punishAccountId = punishAccountId;
    }

    public Integer getPunishCreditScore() {
        return punishCreditScore;
    }

    public void setPunishCreditScore(Integer punishCreditScore) {
        this.punishCreditScore = punishCreditScore;
    }

    public String getPunishDescription() {
        return punishDescription;
    }

    public void setPunishDescription(String punishDescription) {
        this.punishDescription = punishDescription;
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
                ", punishAccountType=" + punishAccountType +
                ", punishAccountId=" + punishAccountId +
                ", punishCreditScore=" + punishCreditScore +
                ", punishDescription='" + punishDescription + '\'' +
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
        complaintVO.setPunishAccountType(punishAccountType);
        complaintVO.setPunishAccountId(punishAccountId);
        complaintVO.setPunishCreditScore(punishCreditScore);
        complaintVO.setPunishDescription(punishDescription);
        complaintVO.setIsDelete(isDelete);
        return complaintVO;
    }
}
