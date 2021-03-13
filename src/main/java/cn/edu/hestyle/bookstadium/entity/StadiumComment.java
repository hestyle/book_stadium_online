package cn.edu.hestyle.bookstadium.entity;

import cn.edu.hestyle.bookstadium.util.ResponseResult;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/13 8:31 下午
 */
public class StadiumComment {
    /** id */
    private Integer id;
    /** stadiumId */
    private Integer stadiumId;
    /** userId */
    private Integer userId;
    /** content */
    private String content;
    /** comment user */
    private User commentUser;
    /** starCount */
    private Integer starCount;
    /** 评论时间 */
    @JsonFormat(pattern = ResponseResult.DATETIME_FORMAT, timezone = "GMT+8")
    private Date commentedTime;
    /** managerReply */
    private String managerReply;
    /**是否删除，0未删除，1已删除*/
    private Integer isDelete;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getStadiumId() {
        return stadiumId;
    }

    public void setStadiumId(Integer stadiumId) {
        this.stadiumId = stadiumId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public User getCommentUser() {
        return commentUser;
    }

    public void setCommentUser(User commentUser) {
        this.commentUser = commentUser;
    }

    public Integer getStarCount() {
        return starCount;
    }

    public void setStarCount(Integer starCount) {
        this.starCount = starCount;
    }

    public Date getCommentedTime() {
        return commentedTime;
    }

    public void setCommentedTime(Date commentedTime) {
        this.commentedTime = commentedTime;
    }

    public String getManagerReply() {
        return managerReply;
    }

    public void setManagerReply(String managerReply) {
        this.managerReply = managerReply;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    @Override
    public String toString() {
        return "StadiumComment{" +
                "id=" + id +
                ", stadiumId=" + stadiumId +
                ", userId=" + userId +
                ", content='" + content + '\'' +
                ", commentUser=" + commentUser +
                ", starCount=" + starCount +
                ", commentedTime=" + commentedTime +
                ", managerReply='" + managerReply + '\'' +
                ", isDelete=" + isDelete +
                '}';
    }
}
