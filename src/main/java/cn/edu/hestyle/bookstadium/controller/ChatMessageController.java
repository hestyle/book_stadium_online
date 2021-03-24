package cn.edu.hestyle.bookstadium.controller;

import cn.edu.hestyle.bookstadium.entity.ChatMessage;
import cn.edu.hestyle.bookstadium.entity.User;
import cn.edu.hestyle.bookstadium.jwt.JwtToken;
import cn.edu.hestyle.bookstadium.service.IChatMessageService;
import cn.edu.hestyle.bookstadium.util.ResponseResult;
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

    @Autowired
    private IChatMessageService chatMessageService;

    @PostMapping("/userFindByChatIdAndPage.do")
    @JwtToken(required = true, authorizedRoles = {User.USER_ROLE})
    public ResponseResult<List<ChatMessage>> handleUserFindByChatIdAndPage(@RequestParam(value = "chatId") Integer chatId,
                                                                           @RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex,
                                                                           @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                                           HttpSession session) {
        Integer userId = (Integer) session.getAttribute("id");
        List<ChatMessage> chatMessageList = chatMessageService.userFindByChatIdAndPage(userId, chatId, pageIndex, pageSize);
        return new ResponseResult<List<ChatMessage>>(SUCCESS, "查询成功！", chatMessageList);
    }
}
