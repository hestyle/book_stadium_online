package cn.edu.hestyle.bookstadium.entity;

import cn.edu.hestyle.bookstadium.util.ResponseResult;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/4/6 11:25 上午
 */
public class WebSocketMessage {
    /** 普通消息 */
    public static Integer WEBSOCKET_MESSAGE_TYPE_COMMON = 0;
    /** ChatMessage消息 */
    public static Integer WEBSOCKET_MESSAGE_TYPE_CHAT_MESSAGE = 1;
    /** messageType */
    private Integer messageType;
    /** content */
    private String content;
    /** 发送时间 */
    @JsonFormat(pattern = ResponseResult.DATETIME_FORMAT, timezone = "GMT+8")
    private Date sentTime;

    public WebSocketMessage() {
    }

    public WebSocketMessage(Integer messageType, String content) {
        this.messageType = messageType;
        this.content = content;
        this.sentTime = new Date();
    }

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
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

    @Override
    public String toString() {
        return "WebSocketMessage{" +
                "messageType=" + messageType +
                ", content='" + content + '\'' +
                ", sentTime=" + sentTime +
                '}';
    }
}
