package cn.edu.hestyle.bookstadium.controller;

import cn.edu.hestyle.bookstadium.entity.StadiumBookItem;
import cn.edu.hestyle.bookstadium.entity.User;
import cn.edu.hestyle.bookstadium.entity.UserStadiumBookItem;
import cn.edu.hestyle.bookstadium.jwt.JwtToken;
import cn.edu.hestyle.bookstadium.service.IUserStadiumBookItemService;
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
 * @date 2021/3/15 3:47 下午
 */
@RestController
@RequestMapping("/userStadiumBookItem")
public class UserStadiumBookItemController extends BaseController {

    @Autowired
    private IUserStadiumBookItemService userStadiumBookItemService;

    @PostMapping("/findByUserIdAndPage.do")
    @JwtToken(required = true, authorizedRoles = User.USER_ROLE)
    public ResponseResult<List<UserStadiumBookItem>> handleUserFindByUserIdAndPage(@RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex,
                                                                                   @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                                                   HttpSession session) {
        // 从session中取出id
        Integer userId = (Integer) session.getAttribute("id");
        List<UserStadiumBookItem> userStadiumBookItemList = userStadiumBookItemService.findByUserIdAndPage(userId, pageIndex, pageSize);
        return new ResponseResult<List<UserStadiumBookItem>>(SUCCESS, "查询成功！", userStadiumBookItemList);
    }

}
