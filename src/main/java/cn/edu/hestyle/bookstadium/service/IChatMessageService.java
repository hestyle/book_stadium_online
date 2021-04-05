package cn.edu.hestyle.bookstadium.service;

import cn.edu.hestyle.bookstadium.entity.ChatMessage;

import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/24 9:03 下午
 */
public interface IChatMessageService {
    /**
     * user发送chatMessage
     * @param userId        userId
     * @param chatMessage   chatMessage
     * @return              ChatMessage
     */
    ChatMessage userSend(Integer userId, ChatMessage chatMessage);

    /**
     * user通过chatId分页查询ChatMessage
     * @param userId        userId
     * @param chatId        chatId
     * @param pageIndex     pageIndex
     * @param pageSize      pageSize
     * @return              List ChatMessage
     */
    List<ChatMessage> userFindByChatIdAndPage(Integer userId, Integer chatId, Integer pageIndex, Integer pageSize);

    /**
     * stadiumManager获取chatMessageId之前的消息
     * @param stadiumManagerId  stadiumManagerId
     * @param chatId            chatId
     * @param chatMessageId     chatMessageId
     * @param pageSize          pageSize
     * @return                  List    ChatMessage
     */
    List<ChatMessage> stadiumManagerFindBeforePage(Integer stadiumManagerId, Integer chatId, Integer chatMessageId, Integer pageSize);
}
