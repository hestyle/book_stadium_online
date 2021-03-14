package cn.edu.hestyle.bookstadium.controller;

import cn.edu.hestyle.bookstadium.entity.StadiumBookItem;
import cn.edu.hestyle.bookstadium.entity.User;
import cn.edu.hestyle.bookstadium.jwt.JwtToken;
import cn.edu.hestyle.bookstadium.service.IStadiumBookItemService;
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
 * @date 2021/3/14 5:31 下午
 */
@RestController
@RequestMapping("/stadiumBookItem")
public class StadiumBookItemController extends BaseController {

    @Autowired
    private IStadiumBookItemService stadiumBookItemService;

    @PostMapping("/add.do")
    @JwtToken(required = true, authorizedRoles = User.USER_ROLE)
    public ResponseResult<Void> handleAdd(@RequestParam(name = "stadiumBookId") Integer stadiumBookId, HttpSession session) {
        // 从session中取出id
        Integer userId = (Integer) session.getAttribute("id");
        stadiumBookItemService.userAdd(userId, stadiumBookId);
        return new ResponseResult<Void>(SUCCESS, "预约成功！");
    }

    @PostMapping("/findByStadiumBookIdAndPage.do")
    public ResponseResult<List<StadiumBookItem>> handleUserFindByStadiumBookIdAndPage(@RequestParam(name = "stadiumBookId") Integer stadiumBookId,
                                                                                      @RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex,
                                                                                      @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                                                      HttpSession session) {
        List<StadiumBookItem> stadiumBookItemList = stadiumBookItemService.findByStadiumBookIdAndPage(stadiumBookId, pageIndex, pageSize);
        return new ResponseResult<List<StadiumBookItem>>(SUCCESS, "查询成功！", stadiumBookItemList);
    }
}
