package cn.edu.hestyle.bookstadium.controller;

import cn.edu.hestyle.bookstadium.controller.exception.RequestException;
import cn.edu.hestyle.bookstadium.entity.ChatVO;
import cn.edu.hestyle.bookstadium.entity.User;
import cn.edu.hestyle.bookstadium.jwt.JwtToken;
import cn.edu.hestyle.bookstadium.service.IChatService;
import cn.edu.hestyle.bookstadium.util.ResponseResult;
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
 * @date 2021/3/24 11:41 上午
 */
@RestController
@RequestMapping("/chat")
public class ChatController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    private IChatService chatService;

    @PostMapping("/userGetChatWithUser.do")
    @JwtToken(required = true, authorizedRoles = {User.USER_ROLE})
    public ResponseResult<ChatVO> handleUserGetChatWithUser(@RequestParam(value = "otherUserId") Integer otherUserId, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("id");
        if (otherUserId == null) {
            logger.warn("Chat 查找失败，未输入对方userId！");
            throw new RequestException("聊天创建失败，未指定对方userId！");
        }
        ChatVO chatVO = chatService.userGetChatWithUser(userId, otherUserId);
        return new ResponseResult<ChatVO>(SUCCESS, "查询成功！", chatVO);
    }

    @PostMapping("/userFindByPage.do")
    @JwtToken(required = true, authorizedRoles = {User.USER_ROLE})
    public ResponseResult<List<ChatVO>> handleUserFindByPage(@RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex,
                                                             @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                             HttpSession session) {
        Integer userId = (Integer) session.getAttribute("id");
        List<ChatVO> chatVOList = chatService.userFindByPage(userId, pageIndex, pageSize);
        return new ResponseResult<List<ChatVO>>(SUCCESS, "查询成功！", chatVOList);
    }

}
