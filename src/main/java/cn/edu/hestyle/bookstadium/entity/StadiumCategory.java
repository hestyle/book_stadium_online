package cn.edu.hestyle.bookstadium.entity;

import cn.edu.hestyle.bookstadium.util.ResponseResult;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * StadiumCategory 场馆分类实体类
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/4 2:49 下午
 */
public class StadiumCategory {
    /**id*/
    private Integer id;
    /**名称*/
    private String name;
    /**描述*/
    private String description;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        return "StadiumCategory{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", isDelete=" + isDelete +
                ", createdUser='" + createdUser + '\'' +
                ", createdTime=" + createdTime +
                ", modifiedUser='" + modifiedUser + '\'' +
                ", modifiedTime=" + modifiedTime +
                '}';
    }
}
