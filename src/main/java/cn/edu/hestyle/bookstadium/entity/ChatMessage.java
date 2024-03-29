package cn.edu.hestyle.bookstadium.entity;

import cn.edu.hestyle.bookstadium.util.ResponseResult;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/24 10:47 上午
 */
public class ChatMessage {
    /** message类型 */
    public static final Integer MESSAGE_TYPE_USER_TO_USER = 0;
    public static final Integer MESSAGE_TYPE_USER_TO_MANAGER = 1;
    public static final Integer MESSAGE_TYPE_MANAGER_TO_USER = 2;

    /** id */
    private Integer id;
    /** chatId */
    private Integer chatId;
    /** messageType */
    private Integer messageType;
    /** fromAccountId */
    private Integer fromAccountId;
    /** toAccountId */
    private Integer toAccountId;
    /** content */
    private String content;
    /** 发送时间 */
    @JsonFormat(pattern = ResponseResult.DATETIME_FORMAT, timezone = "GMT+8")
    private Date sentTime;
    /** 是否删除，0未删除，1已删除 */
    private Integer isDelete;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getChatId() {
        return chatId;
    }

    public void setChatId(Integer chatId) {
        this.chatId = chatId;
    }

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    public Integer getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(Integer fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public Integer getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(Integer toAccountId) {
        this.toAccountId = toAccountId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    @Override
    public String toString() {
        return "ChatMessage{" +
                "id=" + id +
                ", chatId=" + chatId +
                ", messageType=" + messageType +
                ", fromAccountId=" + fromAccountId +
                ", toAccountId=" + toAccountId +
                ", content='" + content + '\'' +
                ", sentTime=" + sentTime +
                ", isDelete=" + isDelete +
                '}';
    }
}
