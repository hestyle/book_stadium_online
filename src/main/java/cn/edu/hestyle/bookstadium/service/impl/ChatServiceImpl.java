package cn.edu.hestyle.bookstadium.service.impl;

import cn.edu.hestyle.bookstadium.entity.*;
import cn.edu.hestyle.bookstadium.mapper.ChatMapper;
import cn.edu.hestyle.bookstadium.mapper.ChatMessageMapper;
import cn.edu.hestyle.bookstadium.mapper.StadiumManagerMapper;
import cn.edu.hestyle.bookstadium.mapper.UserMapper;
import cn.edu.hestyle.bookstadium.service.IChatService;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/24 11:18 上午
 */
@Service
public class ChatServiceImpl implements IChatService {
    private static final Logger logger = LoggerFactory.getLogger(ChatServiceImpl.class);

    @Resource
    private ChatMapper chatMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private ChatMessageMapper chatMessageMapper;
    @Resource
    private StadiumManagerMapper stadiumManagerMapper;

    @Override
    public ChatVO userGetChatWithUser(Integer userId, Integer otherUserId) {
        if (userId.equals(otherUserId)) {
            logger.warn("User 查找失败，账号无法与自身聊天！userId = " + userId + ", otherUserId = " + otherUserId);
            throw new FindFailedException("聊天创建失败，账号无法与自身聊天！");
        }
        User otherUser = null;
        try {
            otherUser = userMapper.findById(otherUserId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("User 查找失败，数据库发生未知错误！otherUserId = " + otherUserId);
            throw new FindFailedException("聊天创建失败，数据库发生未知错误！");
        }
        if (otherUser == null) {
            logger.warn("Chat 聊天创建失败，对方不存在！otherUserId = " + otherUserId);
            throw new FindFailedException("聊天创建失败，对方未注册！");
        }
        Chat chat = null;
        try {
            chat = chatMapper.findUserWithUserChat(userId, otherUserId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Chat 查找失败，数据库发生未知错误！userId = " + userId + ", otherUserId = " + otherUserId);
            throw new FindFailedException("聊天创建失败，数据库发生未知错误！");
        }
        if (chat == null) {
            // chat不存在，则创建一个
            chat = new Chat();
            chat.setChatType(Chat.CHAT_TYPE_USER_TO_USER);
            chat.setFromAccountId(userId);
            chat.setToAccountId(otherUserId);
            chat.setFromUnreadCount(0);
            chat.setToUnreadCount(0);
            chat.setCreatedTime(new Date());
            chat.setModifiedTime(new Date());
            chat.setIsDelete(0);
            try {
                chatMapper.add(chat);
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("Chat 插入失败，数据库发生未知错误！chat = " + chat);
                throw new FindFailedException("聊天创建失败，数据库发生未知错误！");
            }
        }
        User user = null;
        try {
            user = userMapper.findById(userId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("User 查找失败，数据库发生未知错误！userId = " + userId);
            throw new FindFailedException("聊天创建失败，数据库发生未知错误！");
        }
        ChatVO chatVO = chat.toChatVO();
        if (chat.getFromAccountId().equals(userId)) {
            // 填充chat发起方信息
            if (user != null) {
                chatVO.setFromAccountUsername(user.getUsername());
                chatVO.setFromAccountAvatarPath(user.getAvatarPath());
            }
            // 填充chat接收方信息
            chatVO.setToAccountUsername(otherUser.getUsername());
            chatVO.setToAccountAvatarPath(otherUser.getAvatarPath());
        } else if (chat.getToAccountId().equals(userId)) {
            // 填充chat接收方信息
            chatVO.setFromAccountUsername(otherUser.getUsername());
            chatVO.setFromAccountAvatarPath(otherUser.getAvatarPath());
            // 填充chat发起方信息
            if (user != null) {
                chatVO.setToAccountUsername(user.getUsername());
                chatVO.setToAccountAvatarPath(user.getAvatarPath());
            }
        }
        return chatVO;
    }

    @Override
    public ChatVO userGetChatWithStadiumManager(Integer userId, Integer stadiumManagerId) {
        StadiumManager stadiumManager = null;
        try {
            stadiumManager = stadiumManagerMapper.findById(stadiumManagerId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumManager 查找失败，数据库发生未知错误！stadiumManagerId = " + stadiumManagerId);
            throw new FindFailedException("聊天创建失败，数据库发生未知错误！");
        }
        if (stadiumManager == null) {
            logger.warn("Chat 聊天创建失败，对方不存在！stadiumManagerId = " + stadiumManagerId);
            throw new FindFailedException("聊天创建失败，对方未注册！");
        }
        Chat chat = null;
        try {
            chat = chatMapper.findUserWithStadiumManagerChat(userId, stadiumManagerId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Chat 查找失败，数据库发生未知错误！userId = " + userId + ", stadiumManagerId = " + stadiumManagerId);
            throw new FindFailedException("聊天创建失败，数据库发生未知错误！");
        }
        if (chat == null) {
            // chat不存在，则创建一个
            chat = new Chat();
            chat.setChatType(Chat.CHAT_TYPE_USER_TO_MANAGER);
            chat.setFromAccountId(userId);
            chat.setToAccountId(stadiumManagerId);
            chat.setFromUnreadCount(0);
            chat.setToUnreadCount(0);
            chat.setCreatedTime(new Date());
            chat.setModifiedTime(new Date());
            chat.setIsDelete(0);
            try {
                chatMapper.add(chat);
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("Chat 插入失败，数据库发生未知错误！chat = " + chat);
                throw new FindFailedException("聊天创建失败，数据库发生未知错误！");
            }
        }
        User user = null;
        try {
            user = userMapper.findById(userId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("User 查找失败，数据库发生未知错误！userId = " + userId);
            throw new FindFailedException("聊天创建失败，数据库发生未知错误！");
        }
        ChatVO chatVO = chat.toChatVO();
        if (chat.getChatType().equals(Chat.CHAT_TYPE_USER_TO_MANAGER) && chat.getFromAccountId().equals(userId)) {
            // 填充chat发起方信息
            if (user != null) {
                chatVO.setFromAccountUsername(user.getUsername());
                chatVO.setFromAccountAvatarPath(user.getAvatarPath());
            }
            // 填充chat接收方信息
            chatVO.setToAccountUsername(stadiumManager.getUsername());
            chatVO.setToAccountAvatarPath(stadiumManager.getAvatarPath());
        } else if (chat.getChatType().equals(Chat.CHAT_TYPE_MANAGER_TO_USER) && chat.getToAccountId().equals(userId)) {
            // 填充chat接收方信息
            chatVO.setFromAccountUsername(stadiumManager.getUsername());
            chatVO.setFromAccountAvatarPath(stadiumManager.getAvatarPath());
            // 填充chat发起方信息
            if (user != null) {
                chatVO.setToAccountUsername(user.getUsername());
                chatVO.setToAccountAvatarPath(user.getAvatarPath());
            }
        }
        return chatVO;
    }

    @Override
    public List<ChatVO> userFindByPage(Integer userId, Integer pageIndex, Integer pageSize) {
        // 检查页码是否合法
        if (pageIndex < 1) {
            throw new FindFailedException("查询失败，页码 " + pageIndex + " 非法，必须大于0！");
        }
        // 检查页大小是否合法
        if (pageSize < 1) {
            throw new FindFailedException("查询失败，页大小 " + pageSize + " 非法，必须大于0！");
        }
        List<Chat> chatList = null;
        try {
            chatList = chatMapper.userFindByPage(userId, (pageIndex - 1) * pageSize, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Chat 查找失败，数据库发生未知错误！userId = " + userId + "，pageIndex = " + pageIndex + "，pageSize = " + pageSize);
            throw new FindFailedException("查找失败，数据库发生未知错误！");
        }
        logger.warn("Chat 查找成功！userId = " + userId + "，chatList = " + chatList);
        List<ChatVO> chatVOList = chatListToChatVOList(chatList);
        logger.warn("ChatVO 查找成功！userId = " + userId + "，chatVOList = " + chatVOList);
        return chatVOList;
    }

    /**
     * chatList 转 chatVOList
     * @param chatList      chatList
     * @return              chatVOList
     */
    List<ChatVO> chatListToChatVOList(List<Chat> chatList) {
        List<ChatVO> chatVOList = new ArrayList<>();
        if (chatList != null && chatList.size() != 0) {
            for (Chat chat : chatList) {
                ChatVO chatVO = chat.toChatVO();
                // 获取发送者的username、avatarPath
                if (chat.getChatType().equals(Chat.CHAT_TYPE_USER_TO_USER) || chat.getChatType().equals(Chat.CHAT_TYPE_USER_TO_MANAGER)) {
                    User user = null;
                    try {
                        user = userMapper.findById(chat.getFromAccountId());
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.warn("User 查找失败，数据库发生未知错误！userId = " + chat.getFromAccountId());
                        throw new FindFailedException("查找失败，数据库发生未知错误！");
                    }
                    if (user != null) {
                        chatVO.setFromAccountUsername(user.getUsername());
                        chatVO.setFromAccountAvatarPath(user.getAvatarPath());
                    }
                } else {
                    StadiumManager stadiumManager = null;
                    try {
                        stadiumManager = stadiumManagerMapper.findById(chat.getFromAccountId());
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.warn("StadiumManager 查找失败，数据库发生未知错误！stadiumManagerId = " + chat.getFromAccountId());
                        throw new FindFailedException("查找失败，数据库发生未知错误！");
                    }
                    if (stadiumManager != null) {
                        chatVO.setFromAccountUsername(stadiumManager.getUsername());
                        chatVO.setFromAccountAvatarPath(stadiumManager.getAvatarPath());
                    }
                }
                // 获取接收者的username、avatarPath
                if (chat.getChatType().equals(Chat.CHAT_TYPE_USER_TO_USER) || chat.getChatType().equals(Chat.CHAT_TYPE_MANAGER_TO_USER)) {
                    User user = null;
                    try {
                        user = userMapper.findById(chat.getToAccountId());
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.warn("User 查找失败，数据库发生未知错误！userId = " + chat.getToAccountId());
                        throw new FindFailedException("查找失败，数据库发生未知错误！");
                    }
                    if (user != null) {
                        chatVO.setToAccountUsername(user.getUsername());
                        chatVO.setToAccountAvatarPath(user.getAvatarPath());
                    }
                } else {
                    StadiumManager stadiumManager = null;
                    try {
                        stadiumManager = stadiumManagerMapper.findById(chat.getToAccountId());
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.warn("StadiumManager 查找失败，数据库发生未知错误！stadiumManagerId = " + chat.getToAccountId());
                        throw new FindFailedException("查找失败，数据库发生未知错误！");
                    }
                    if (stadiumManager != null) {
                        chatVO.setToAccountUsername(stadiumManager.getUsername());
                        chatVO.setToAccountAvatarPath(stadiumManager.getAvatarPath());
                    }
                }
                // 填充最后一条消息
                if (chat.getLastChatMessageId() != null) {
                    ChatMessage chatMessage = null;
                    try {
                        chatMessage = chatMessageMapper.findById(chat.getLastChatMessageId());
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.warn("ChatMessage 查找失败，数据库发生未知错误！chatMessageId = " + chat.getLastChatMessageId());
                        throw new FindFailedException("查找失败，数据库发生未知错误！");
                    }
                    if (chatMessage != null && chatMessage.getIsDelete() != 0) {
                        chatMessage.setContent("已删除！");
                    }
                    chatVO.setLastChatMessage(chatMessage);
                }
                chatVOList.add(chatVO);
            }
        }
        return chatVOList;
    }
}
