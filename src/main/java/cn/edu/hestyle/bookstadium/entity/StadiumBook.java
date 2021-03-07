package cn.edu.hestyle.bookstadium.entity;

import cn.edu.hestyle.bookstadium.util.ResponseResult;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * StadiumBook 实体类
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/6 10:48 上午
 */
public class StadiumBook {
    /** id */
    private Integer id;
    /** stadium id */
    private Integer stadiumId;
    /** 起始时间 */
    @JsonFormat(pattern = ResponseResult.DATETIME_FORMAT, timezone = "GMT+8")
    private Date startTime;
    /** 结束时间 */
    @JsonFormat(pattern = ResponseResult.DATETIME_FORMAT, timezone = "GMT+8")
    private Date endTime;
    /** 预约状态，0 未开始预约，1 正在预约，2 已结束预约 */
    private Integer bookState;
    /** 最大可预约数量 */
    private Integer maxBookCount;
    /** 已经预约的数量 */
    private Integer nowBookCount;
    /**是否删除，0未删除，1已删除*/
    private Integer isDelete;
    /**创建者*/
    private String createdUser;
    /**创建时间*/
    @JsonFormat(pattern = ResponseResult.DATETIME_FORMAT, timezone = "GMT+8")
    private Date createdTime;
    /**修改者*/
    private String modifiedUser;
    /**修改时间*/
    @JsonFormat(pattern = ResponseResult.DATETIME_FORMAT, timezone = "GMT+8")
    private Date modifiedTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStadiumId() {
        return stadiumId;
    }

    public void setStadiumId(Integer stadiumId) {
        this.stadiumId = stadiumId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getBookState() {
        return bookState;
    }

    public void setBookState(Integer bookState) {
        this.bookState = bookState;
    }

    public Integer getMaxBookCount() {
        return maxBookCount;
    }

    public void setMaxBookCount(Integer maxBookCount) {
        this.maxBookCount = maxBookCount;
    }

    public Integer getNowBookCount() {
        return nowBookCount;
    }

    public void setNowBookCount(Integer nowBookCount) {
        this.nowBookCount = nowBookCount;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public String getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(String createdUser) {
        this.createdUser = createdUser;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
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
        return "StadiumBook{" +
                "id=" + id +
                ", stadiumId=" + stadiumId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", bookState=" + bookState +
                ", maxBookCount=" + maxBookCount +
                ", nowBookCount=" + nowBookCount +
                ", isDelete=" + isDelete +
                ", createdUser='" + createdUser + '\'' +
                ", createdTime=" + createdTime +
                ", modifiedUser='" + modifiedUser + '\'' +
                ", modifiedTime=" + modifiedTime +
                '}';
    }
}
