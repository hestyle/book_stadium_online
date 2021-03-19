package cn.edu.hestyle.bookstadium.entity;

import cn.edu.hestyle.bookstadium.util.ResponseResult;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/19 10:05 上午
 */
public class SportMomentCommentLike {
    /** id */
    private Integer id;
    /** userId */
    private Integer userId;
    /** sportMomentCommentId */
    private Integer sportMomentCommentId;
    /** 点赞时间 */
    @JsonFormat(pattern = ResponseResult.DATETIME_FORMAT, timezone = "GMT+8")
    private Date likedTime;
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

    public Integer getSportMomentCommentId() {
        return sportMomentCommentId;
    }

    public void setSportMomentCommentId(Integer sportMomentCommentId) {
        this.sportMomentCommentId = sportMomentCommentId;
    }

    public Date getLikedTime() {
        return likedTime;
    }

    public void setLikedTime(Date likedTime) {
        this.likedTime = likedTime;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    @Override
    public String toString() {
        return "SportMomentCommentLike{" +
                "id=" + id +
                ", userId=" + userId +
                ", sportMomentCommentId=" + sportMomentCommentId +
                ", likedTime=" + likedTime +
                ", isDelete=" + isDelete +
                '}';
    }
}
