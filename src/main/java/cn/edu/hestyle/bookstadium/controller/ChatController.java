package cn.edu.hestyle.bookstadium.controller;

import cn.edu.hestyle.bookstadium.entity.ChatVO;
import cn.edu.hestyle.bookstadium.entity.User;
import cn.edu.hestyle.bookstadium.jwt.JwtToken;
import cn.edu.hestyle.bookstadium.service.IChatService;
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
 * @date 2021/3/24 11:41 上午
 */
@RestController
@RequestMapping("/chat")
public class ChatController extends BaseController {

    @Autowired
    private IChatService chatService;

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
