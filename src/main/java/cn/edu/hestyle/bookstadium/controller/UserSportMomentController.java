package cn.edu.hestyle.bookstadium.controller;

import cn.edu.hestyle.bookstadium.entity.UserSportMoment;
import cn.edu.hestyle.bookstadium.service.IUserSportMomentService;
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
 * @date 2021/3/16 5:18 下午
 */
@RestController
@RequestMapping("/userSportMoment")
public class UserSportMomentController extends BaseController {

    @Autowired
    private IUserSportMomentService userSportMomentService;

    @PostMapping("/findByPage.do")
    public ResponseResult<List<UserSportMoment>> handleFindByPage(@RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex,
                                                                      @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                                      HttpSession session) {
        List<UserSportMoment> userSportMomentList = userSportMomentService.findByPage(pageIndex, pageSize);
        return new ResponseResult<List<UserSportMoment>>(SUCCESS, "查询成功！", userSportMomentList);
    }
}
