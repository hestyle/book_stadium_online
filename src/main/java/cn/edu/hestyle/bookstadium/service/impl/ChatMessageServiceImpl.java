package cn.edu.hestyle.bookstadium.service.impl;

import cn.edu.hestyle.bookstadium.entity.Chat;
import cn.edu.hestyle.bookstadium.entity.ChatMessage;
import cn.edu.hestyle.bookstadium.mapper.ChatMapper;
import cn.edu.hestyle.bookstadium.mapper.ChatMessageMapper;
import cn.edu.hestyle.bookstadium.service.IChatMessageService;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/24 9:05 下午
 */
@Service
public class ChatMessageServiceImpl implements IChatMessageService {
    private static final Logger logger = LoggerFactory.getLogger(ChatMessageServiceImpl.class);

    @Resource
    private ChatMapper chatMapper;
    @Resource
    private ChatMessageMapper chatMessageMapper;

    @Override
    public List<ChatMessage> userFindByChatIdAndPage(Integer userId, Integer chatId, Integer pageIndex, Integer pageSize) {
        if (chatId == null) {
            logger.warn("ChatMessage 查找失败，未传入chatId！");
            throw new FindFailedException("查找失败，未传入chatId参数！");
        }
        Chat chat = null;
        try {
            chat = chatMapper.findById(chatId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Chat 查找失败，数据库发生未知异常！chatId = " + chatId);
            throw new FindFailedException("查找失败，数据库发生未知异常！");
        }
        boolean isFrom = true;
        // 检查user是否是chat的发起者或者接受者
        if (chat.getChatType().equals(Chat.CHAT_TYPE_USER_TO_MANAGER)) {
            if (!chat.getFromAccountId().equals(userId)) {
                logger.warn("ChatMessage 查找失败，用户无法查找非自己账号的聊天！userId = " + userId + "，chat = " + chat);
                throw new FindFailedException("查找失败，无法查看其他用户之间的聊天！");
            }
            // 当前账号是chat发起者
        } else if (chat.getChatType().equals(Chat.CHAT_TYPE_MANAGER_TO_USER)) {
            if (!chat.getToAccountId().equals(userId)) {
                logger.warn("ChatMessage 查找失败，用户无法查找非自己账号的聊天！userId = " + userId + "，chat = " + chat);
                throw new FindFailedException("查找失败，无法查看其他用户之间的聊天！");
            }
            // 当前账号是chat接受者
            isFrom = false;
        } else if (!chat.getFromAccountId().equals(userId)) {
            if (!chat.getToAccountId().equals(userId)) {
                logger.warn("ChatMessage 查找失败，用户无法查找非自己账号的聊天！userId = " + userId + "，chat = " + chat);
                throw new FindFailedException("查找失败，无法查看其他用户之间的聊天！");
            }
            // 当前账号是chat接受者
            isFrom = false;
        } else {
            // 当前账号是chat发起者
            isFrom = true;
        }
        boolean chatNeedUpdate = false;
        if (isFrom && chat.getFromUnreadCount() != 0) {
            // 发起者请求ChatMessage，清除它的未读消息数
            chat.setFromUnreadCount(0);
            chatNeedUpdate = true;
        } else if (!isFrom && chat.getToUnreadCount() != 0) {
            // 接收者请求ChatMessage，清除它的未读消息数
            chat.setToUnreadCount(0);
            chatNeedUpdate = true;
        }
        if (chatNeedUpdate) {
            try {
                chatMapper.update(chat);
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("Chat 更新失败，数据库发生未知异常！chat = " + chat);
                throw new FindFailedException("查找失败，数据库发生未知异常！");
            }
            logger.warn("Chat 更新成功！chat = " + chat);
        }
        // 检查页码是否合法
        if (pageIndex < 1) {
            throw new FindFailedException("查询失败，页码 " + pageIndex + " 非法，必须大于0！");
        }
        // 检查页大小是否合法
        if (pageSize < 1) {
            throw new FindFailedException("查询失败，页大小 " + pageSize + " 非法，必须大于0！");
        }
        List<ChatMessage> chatMessageList = null;
        try {
            chatMessageList = chatMessageMapper.findByChatIdAndPage(chatId, (pageIndex - 1) * pageSize, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("ChatMessage 查找失败，数据库发生未知异常！chatId = " + chatId);
            throw new FindFailedException("查找失败，数据库发生未知异常！");
        }
        logger.warn("ChatMessage 查找陈宫！chatMessageList = " + chatMessageList);
        return chatMessageList;
    }
}
