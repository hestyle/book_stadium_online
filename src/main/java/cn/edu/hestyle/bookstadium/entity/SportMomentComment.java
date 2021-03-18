package cn.edu.hestyle.bookstadium.entity;

import cn.edu.hestyle.bookstadium.util.ResponseResult;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/18 4:00 下午
 */
public class SportMomentComment {
    /** id */
    private Integer id;
    /** userId */
    private Integer userId;
    /** sportMomentId */
    private Integer sportMomentId;
    /** pid == null表示是sportMoment的评论，否则表示是评论的回复 */
    private Integer parentId;
    /** 评论内容 */
    private String content;
    /** 评论时间 */
    @JsonFormat(pattern = ResponseResult.DATETIME_FORMAT, timezone = "GMT+8")
    private Date commentedTime;
    /** 点赞数量 */
    private Integer likeCount;
    /** 是否删除，0未删除，1已删除，2因违规被拉黑、屏蔽 */
    private Integer isDelete;

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

    public Integer getSportMomentId() {
        return sportMomentId;
    }

    public void setSportMomentId(Integer sportMomentId) {
        this.sportMomentId = sportMomentId;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCommentedTime() {
        return commentedTime;
    }

    public void setCommentedTime(Date commentedTime) {
        this.commentedTime = commentedTime;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    @Override
    public String toString() {
        return "SportMomentComment{" +
                "id=" + id +
                ", userId=" + userId +
                ", sportMomentId=" + sportMomentId +
                ", parentId=" + parentId +
                ", content='" + content + '\'' +
                ", commentedTime=" + commentedTime +
                ", likeCount=" + likeCount +
                ", isDelete=" + isDelete +
                '}';
    }

    /**
     * to UserSportMomentComment
     * @return  UserSportMomentComment
     */
    public UserSportMomentComment toUserSportMomentComment() {
        UserSportMomentComment userSportMomentComment = new UserSportMomentComment();
        userSportMomentComment.setId(id);
        userSportMomentComment.setUserId(userId);
        userSportMomentComment.setSportMomentId(sportMomentId);
        userSportMomentComment.setParentId(parentId);
        userSportMomentComment.setContent(content);
        userSportMomentComment.setCommentedTime(commentedTime);
        userSportMomentComment.setLikeCount(likeCount);
        userSportMomentComment.setIsDelete(isDelete);
        return userSportMomentComment;
    }
}
