package cn.edu.hestyle.bookstadium.entity;

import cn.edu.hestyle.bookstadium.util.ResponseResult;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/16 4:41 下午
 */
public class SportMoment {
    /** id */
    private Integer id;
    /** userId */
    private Integer userId;
    /** content */
    private String content;
    /** 照片 */
    private String imagePaths;
    /** likeCount */
    private Integer likeCount;
    /** commentCount */
    private Integer commentCount;
    /** 发表时间 */
    @JsonFormat(pattern = ResponseResult.DATETIME_FORMAT, timezone = "GMT+8")
    private Date sentTime;
    /** 是否删除，0未删除，1已删除，2因违规被拉黑、屏蔽 */
    private Integer isDelete;
    /** 修改者 */
    private String modifiedUser;
    /** 修改时间 */
    @JsonFormat(pattern = ResponseResult.DATETIME_FORMAT, timezone = "GMT+8")
    private Date modifiedTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImagePaths() {
        return imagePaths;
    }

    public void setImagePaths(String imagePaths) {
        this.imagePaths = imagePaths;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public Date getSentTime() {
        return sentTime;
    }

    public void setSentTime(Date sentTime) {
        this.sentTime = sentTime;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public String getModifiedUser() {
        return modifiedUser;
    }

    public void setModifiedUser(String modifiedUser) {
        this.modifiedUser = modifiedUser;
    }

    public Date getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    @Override
    public String toString() {
        return "SportMoment{" +
                "id=" + id +
                ", userId=" + userId +
                ", content=" + content +
                ", imagePaths='" + imagePaths + '\'' +
                ", likeCount=" + likeCount +
                ", commentCount=" + commentCount +
                ", sentTime=" + sentTime +
                ", isDelete=" + isDelete +
                ", modifiedUser='" + modifiedUser + '\'' +
                ", modifiedTime=" + modifiedTime +
                '}';
    }
}
