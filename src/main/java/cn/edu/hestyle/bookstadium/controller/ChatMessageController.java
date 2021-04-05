package cn.edu.hestyle.bookstadium.controller;

import cn.edu.hestyle.bookstadium.controller.exception.RequestException;
import cn.edu.hestyle.bookstadium.entity.ChatMessage;
import cn.edu.hestyle.bookstadium.entity.StadiumManager;
import cn.edu.hestyle.bookstadium.entity.User;
import cn.edu.hestyle.bookstadium.jwt.JwtToken;
import cn.edu.hestyle.bookstadium.service.IChatMessageService;
import cn.edu.hestyle.bookstadium.util.ResponseResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/24 9:22 下午
 */
@RestController
@RequestMapping("/chatMessage")
public class ChatMessageController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(ChatMessageController.class);
    @Autowired
    private IChatMessageService chatMessageService;

    @PostMapping("/userSend.do")
    @JwtToken(required = true, authorizedRoles = {User.USER_ROLE})
    public ResponseResult<ChatMessage> handleUserSend(@RequestParam(value = "chatMessageData") String chatMessageData, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("id");
        ObjectMapper objectMapper = new ObjectMapper();
        ChatMessage chatMessage = null;
        try {
            chatMessage = objectMapper.readValue(chatMessageData, ChatMessage.class);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("ChatMessage 发送失败，数据格式错误！chatMessageData = " + chatMessageData);
            throw new RequestException("消息发送失败，数据格式错误！");
        }
        chatMessage = chatMessageService.userSend(userId, chatMessage);
        return new ResponseResult<ChatMessage>(SUCCESS, "发送成功！", chatMessage);
    }

    @PostMapping("/stadiumManagerSend.do")
    @JwtToken(required = true, authorizedRoles = {StadiumManager.STADIUM_MANAGER_ROLE})
    public ResponseResult<ChatMessage> handleStadiumManagerSend(@RequestParam(value = "chatMessageData") String chatMessageData, HttpSession session) {
        Integer stadiumManagerId = (Integer) session.getAttribute("id");
        ObjectMapper objectMapper = new ObjectMapper();
        ChatMessage chatMessage = null;
        try {
            chatMessage = objectMapper.readValue(chatMessageData, ChatMessage.class);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("ChatMessage 发送失败，数据格式错误！chatMessageData = " + chatMessageData);
            throw new RequestException("消息发送失败，数据格式错误！");
        }
        chatMessage = chatMessageService.stadiumManagerSend(stadiumManagerId, chatMessage);
        return new ResponseResult<ChatMessage>(SUCCESS, "发送成功！", chatMessage);
    }

    @PostMapping("/userFindBeforePage.do")
    @JwtToken(required = true, authorizedRoles = {User.USER_ROLE})
    public ResponseResult<List<ChatMessage>> handleUserFindBeforePage(@RequestParam(value = "chatId") Integer chatId,
                                                                      @RequestParam(value = "chatMessageId", defaultValue = "") Integer chatMessageId,
                                                                      @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                                      HttpSession session) {
        Integer userId = (Integer) session.getAttribute("id");
        List<ChatMessage> chatMessageList = chatMessageService.userFindBeforePage(userId, chatId, chatMessageId, pageSize);
        return new ResponseResult<List<ChatMessage>>(SUCCESS, "查询成功！", chatMessageList);
    }

    @PostMapping("/userFindAfterPage.do")
    @JwtToken(required = true, authorizedRoles = {User.USER_ROLE})
    public ResponseResult<List<ChatMessage>> handleUserFindAfterPage(@RequestParam(value = "chatId") Integer chatId,
                                                                     @RequestParam(value = "chatMessageId", defaultValue = "") Integer chatMessageId,
                                                                     @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                                     HttpSession session) {
        Integer userId = (Integer) session.getAttribute("id");
        List<ChatMessage> chatMessageList = chatMessageService.userFindAfterPage(userId, chatId, chatMessageId, pageSize);
        return new ResponseResult<List<ChatMessage>>(SUCCESS, "查询成功！", chatMessageList);
    }

    @PostMapping("/stadiumManagerFindBeforePage.do")
    @JwtToken(required = true, authorizedRoles = {StadiumManager.STADIUM_MANAGER_ROLE})
    public ResponseResult<List<ChatMessage>> handleStadiumManagerFindBeforePage(@RequestParam(value = "chatId") Integer chatId,
                                                                               @RequestParam(value = "chatMessageId", defaultValue = "") Integer chatMessageId,
                                                                               @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                                               HttpSession session) {
        Integer stadiumManagerId = (Integer) session.getAttribute("id");
        List<ChatMessage> chatMessageList = chatMessageService.stadiumManagerFindBeforePage(stadiumManagerId, chatId, chatMessageId, pageSize);
        return new ResponseResult<List<ChatMessage>>(SUCCESS, "查询成功！", chatMessageList);
    }

    @PostMapping("/stadiumManagerFindAfterPage.do")
    @JwtToken(required = true, authorizedRoles = {StadiumManager.STADIUM_MANAGER_ROLE})
    public ResponseResult<List<ChatMessage>> handleStadiumManagerFindAfterPage(@RequestParam(value = "chatId") Integer chatId,
                                                                               @RequestParam(value = "chatMessageId", defaultValue = "") Integer chatMessageId,
                                                                               @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                                               HttpSession session) {
        Integer stadiumManagerId = (Integer) session.getAttribute("id");
        List<ChatMessage> chatMessageList = chatMessageService.stadiumManagerFindAfterPage(stadiumManagerId, chatId, chatMessageId, pageSize);
        return new ResponseResult<List<ChatMessage>>(SUCCESS, "查询成功！", chatMessageList);
    }
}
