package cn.edu.hestyle.bookstadium.mapper;

import cn.edu.hestyle.bookstadium.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/24 3:56 下午
 */
@Mapper
public interface ChatMessageMapper {
    /**
     * 添加chatMessage
     * @param chatMessage   chatMessage
     */
    void add(ChatMessage chatMessage);

    /**
     * 通过id查找ChatMessage
     * @param id            id
     * @return              ChatMessage
     */
    ChatMessage findById(Integer id);

    /**
     * 通过chatId分页查询ChatMessage
     * @param chatId        chatId
     * @param beginIndex    beginIndex
     * @param pageSize      pageSize
     * @return              List ChatMessage
     */
    List<ChatMessage> findByChatIdAndPage(Integer chatId, Integer beginIndex, Integer pageSize);

    /**
     * 获取chatMessageId之前的message
     * @param chatId            chatId
     * @param chatMessageId     chatMessageId
     * @param pageSize          页大小
     * @return                  List ChatMessage
     */
    List<ChatMessage> findBeforePage(Integer chatId, Integer chatMessageId, Integer pageSize);

    /**
     * 获取chatMessageId之后的message
     * @param chatId            chatId
     * @param chatMessageId     chatMessageId
     * @param pageSize          页大小
     * @return                  List ChatMessage
     */
    List<ChatMessage> findAfterPage(Integer chatId, Integer chatMessageId, Integer pageSize);
}
