package cn.edu.hestyle.bookstadium.mapper;

import cn.edu.hestyle.bookstadium.entity.Chat;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/24 10:56 上午
 */
@Mapper
public interface ChatMapper {
    /**
     * 添加chat
     * @param chat          chat
     */
    void add(Chat chat);

    /**
     * 更新chat(只能更新fromUnreadCount、toUnreadCount、lastChatMessageId、modifiedTime、isDelete字段)
     * @param chat          chat
     */
    void update(Chat chat);

    /**
     * 通过id查找Chat
     * @param chatId        chatId
     * @return              Chat
     */
    Chat findById(Integer chatId);

    /**
     * 查找两个user的chat（chatType = Chat.CHAT_TYPE_USER_TO_USER）
     * @param userIdOne     userIdOne
     * @param userIdTwo     userIdTwo
     * @return              Chat
     */
    Chat findUserWithUserChat(Integer userIdOne, Integer userIdTwo);

    /**
     * 查找user与stadiumManager的chat（chatType = Chat.CHAT_TYPE_USER_TO_MANAGER or Chat.CHAT_TYPE_MANAGER_TO_USER）
     * @param userId                userId
     * @param stadiumManagerId      stadiumManagerId
     * @return                      Chat
     */
    Chat findUserWithStadiumManagerChat(Integer userId, Integer stadiumManagerId);

    /**
     * user分页查找Chat
     * @param userId            userId
     * @param beginIndex        beginIndex
     * @param pageSize          pageSize
     * @return                  List Chat
     */
    List<Chat> userFindByPage(Integer userId, Integer beginIndex, Integer pageSize);
}
