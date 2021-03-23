package cn.edu.hestyle.bookstadium.controller;

import cn.edu.hestyle.bookstadium.entity.Notice;
import cn.edu.hestyle.bookstadium.entity.User;
import cn.edu.hestyle.bookstadium.jwt.JwtToken;
import cn.edu.hestyle.bookstadium.service.INoticeService;
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
 * @date 2021/3/23 5:02 下午
 */
@RestController
@RequestMapping("/notice")
public class NoticeController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(NoticeController.class);

    @Autowired
    private INoticeService noticeService;

    @PostMapping("/findUserNoticeByPage.do")
    @JwtToken(required = true, authorizedRoles = {User.USER_ROLE})
    public ResponseResult<List<Notice>> handleFindUserNoticeByPage(@RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex,
                                                                   @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                                   HttpSession session) {
        Integer userId = (Integer) session.getAttribute("id");
        List<Notice> noticeList = noticeService.findByIdAndPage(Notice.TO_ACCOUNT_USER, userId, pageIndex, pageSize);
        return new ResponseResult<List<Notice>>(SUCCESS, "查询成功！", noticeList);
    }
}
