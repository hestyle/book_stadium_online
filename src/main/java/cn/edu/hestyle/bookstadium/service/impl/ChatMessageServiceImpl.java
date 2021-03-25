package cn.edu.hestyle.bookstadium.service.impl;

import cn.edu.hestyle.bookstadium.entity.Chat;
import cn.edu.hestyle.bookstadium.entity.ChatMessage;
import cn.edu.hestyle.bookstadium.mapper.ChatMapper;
import cn.edu.hestyle.bookstadium.mapper.ChatMessageMapper;
import cn.edu.hestyle.bookstadium.service.IChatMessageService;
import cn.edu.hestyle.bookstadium.service.exception.AddFailedException;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/24 9:05 下午
 */
@Service
public class ChatMessageServiceImpl implements IChatMessageService {
    /** ChatMessage content的最大长度 */
    private static final Integer CHAT_MESSAGE_CONTENT_MAX_LENGTH = 500;
    private static final Logger logger = LoggerFactory.getLogger(ChatMessageServiceImpl.class);

    @Resource
    private ChatMapper chatMapper;
    @Resource
    private ChatMessageMapper chatMessageMapper;

    @Override
    @Transactional
    public ChatMessage userSend(Integer userId, ChatMessage chatMessage) {
        if (chatMessage == null || chatMessage.getChatId() == null) {
            logger.warn("ChatMessage 发送失败，未传入chatId！");
            throw new AddFailedException("发送失败，未传入chatId参数！");
        }
        // 检查content
        String chatMessageContent = chatMessage.getContent();
        if (chatMessageContent == null || chatMessageContent.length() == 0) {
            logger.warn("ChatMessage 发送失败，无法发送空消息！chatMessage = " + chatMessage);
            throw new AddFailedException("发送失败，无法发送空消息！");
        }
        if (chatMessageContent.length() > CHAT_MESSAGE_CONTENT_MAX_LENGTH) {
            logger.warn("ChatMessage 发送失败，消息content超过了" + CHAT_MESSAGE_CONTENT_MAX_LENGTH + "个字符！chatMessage = " + chatMessage);
            throw new AddFailedException("发送失败，消息content超过了" + CHAT_MESSAGE_CONTENT_MAX_LENGTH + "个字符！");
        }
        Chat chat = null;
        try {
            chat = chatMapper.findById(chatMessage.getChatId());
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Chat 查找失败，数据库发生未知异常！chatId = " + chatMessage.getChatId());
            throw new FindFailedException("查找失败，数据库发生未知异常！");
        }
        if (chat == null) {
            logger.warn("Chat 查找失败，chatId对应的chat不存在！chatMessage = " + chatMessage);
            throw new FindFailedException("发送失败，不存在这个聊天！");
        }
        chatMessage.setChatType(chat.getChatType());
        // 检查user是否是chat的发起者或者接受者
        if (chat.getChatType().equals(Chat.CHAT_TYPE_USER_TO_MANAGER)) {
            if (!chat.getFromAccountId().equals(userId)) {
                logger.warn("ChatMessage 发送失败，用户无法操作非自己账号的聊天！userId = " + userId + "，chat = " + chat);
                throw new FindFailedException("发送失败，无法操作其他用户之间的聊天！");
            }
            // 当前账号是chat发起者
            chatMessage.setFromAccountId(chat.getFromAccountId());
            chatMessage.setToAccountId(chat.getToAccountId());
            // chat接收者未读消息增加一条
            chat.setToUnreadCount(chat.getToUnreadCount() + 1);
        } else if (chat.getChatType().equals(Chat.CHAT_TYPE_MANAGER_TO_USER)) {
            if (!chat.getToAccountId().equals(userId)) {
                logger.warn("ChatMessage 查找失败，用户无法查找非自己账号的聊天！userId = " + userId + "，chat = " + chat);
                throw new FindFailedException("查找失败，无法查看其他用户之间的聊天！");
            }
            // 当前账号是chat接受者
            chatMessage.setFromAccountId(chat.getToAccountId());
            chatMessage.setToAccountId(chat.getFromAccountId());
            // chat发送者未读消息增加一条
            chat.setToUnreadCount(chat.getFromAccountId() + 1);
        } else if (!chat.getFromAccountId().equals(userId)) {
            if (!chat.getToAccountId().equals(userId)) {
                logger.warn("ChatMessage 查找失败，用户无法查找非自己账号的聊天！userId = " + userId + "，chat = " + chat);
                throw new FindFailedException("查找失败，无法查看其他用户之间的聊天！");
            }
            // 当前账号是chat接受者
            chatMessage.setFromAccountId(chat.getToAccountId());
            chatMessage.setToAccountId(chat.getFromAccountId());
            // chat发送者未读消息增加一条
            chat.setToUnreadCount(chat.getFromAccountId() + 1);
        } else {
            // 当前账号是chat发起者
            chatMessage.setFromAccountId(chat.getFromAccountId());
            chatMessage.setToAccountId(chat.getToAccountId());
            // chat接收者未读消息增加一条
            chat.setToUnreadCount(chat.getToUnreadCount() + 1);
        }
        // 保存chatMessage
        chatMessage.setSentTime(new Date());
        chatMessage.setIsDelete(0);
        try {
            chatMessageMapper.add(chatMessage);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("ChatMessage 插入失败，数据库发生未知异常！chatMessage = " + chatMessage);
            throw new AddFailedException("发送失败，数据库发生未知异常！");
        }
        // 更新chat
        chat.setModifiedTime(new Date());
        chat.setLastChatMessageId(chatMessage.getId());
        try {
            chatMapper.update(chat);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Chat 更新失败，数据库发生未知异常！chat = " + chat);
            throw new AddFailedException("发送失败，数据库发生未知异常！");
        }
        return chatMessage;
    }

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
