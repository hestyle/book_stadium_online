package cn.edu.hestyle.bookstadium.entity;

import cn.edu.hestyle.bookstadium.util.ResponseResult;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/23 4:39 下午
 */
public class Notice {
    /** 通知可能是发给user 或者stadiumManager */
    public static final Integer TO_ACCOUNT_USER = 0;
    public static final Integer TO_ACCOUNT_STADIUM_MANAGER = 1;
    /** id */
    private Integer id;
    /** toAccountType */
    private Integer toAccountType;
    /** accountId */
    private Integer accountId;
    /** title */
    private String title;
    /** content */
    private String content;
    /** 通知产生时间 */
    @JsonFormat(pattern = ResponseResult.DATETIME_FORMAT, timezone = "GMT+8")
    private Date generatedTime;
    /** 是否删除，0未删除，1已删除 */
    private Integer isDelete;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getToAccountType() {
        return toAccountType;
    }

    public void setToAccountType(Integer toAccountType) {
        this.toAccountType = toAccountType;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getGeneratedTime() {
        return generatedTime;
    }

    public void setGeneratedTime(Date generatedTime) {
        this.generatedTime = generatedTime;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    @Override
    public String toString() {
        return "Notice{" +
                "id=" + id +
                ", toAccountType=" + toAccountType +
                ", accountId=" + accountId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", generatedTime=" + generatedTime +
                ", isDelete=" + isDelete +
                '}';
    }
}
