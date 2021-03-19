package cn.edu.hestyle.bookstadium.controller;

import cn.edu.hestyle.bookstadium.entity.User;
import cn.edu.hestyle.bookstadium.entity.UserSportMomentComment;
import cn.edu.hestyle.bookstadium.jwt.JwtToken;
import cn.edu.hestyle.bookstadium.service.IUserSportMomentCommentService;
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
 * @date 2021/3/18 4:53 下午
 */
@RestController
@RequestMapping("/userSportMomentComment")
public class UserSportMomentCommentController extends BaseController {

    @Autowired
    private IUserSportMomentCommentService userSportMomentCommentService;

    @PostMapping("/like.do")
    @JwtToken(required = true, authorizedRoles = {User.USER_ROLE})
    public ResponseResult<Void> handleLike(@RequestParam(value = "sportMomentCommentId") Integer sportMomentCommentId, HttpSession session) {
        // 从session中取出id
        Integer userId = (Integer) session.getAttribute("id");
        userSportMomentCommentService.like(userId, sportMomentCommentId);
        return new ResponseResult<Void>(SUCCESS, "点赞成功！");
    }

    @PostMapping("/hasLiked.do")
    @JwtToken(required = true, authorizedRoles = {User.USER_ROLE})
    public ResponseResult<Boolean> handleHasLike(@RequestParam(value = "sportMomentCommentId") Integer sportMomentCommentId, HttpSession session) {
        // 从session中取出id
        Integer userId = (Integer) session.getAttribute("id");
        Boolean flag = userSportMomentCommentService.hasLiked(userId, sportMomentCommentId);
        return new ResponseResult<Boolean>(SUCCESS, "查找成功！", flag);
    }

    @PostMapping("/dislike.do")
    @JwtToken(required = true, authorizedRoles = {User.USER_ROLE})
    public ResponseResult<Void> handleDislike(@RequestParam(value = "sportMomentCommentId") Integer sportMomentCommentId, HttpSession session) {
        // 从session中取出id
        Integer userId = (Integer) session.getAttribute("id");
        userSportMomentCommentService.dislike(userId, sportMomentCommentId);
        return new ResponseResult<Void>(SUCCESS, "点赞取消成功！");
    }

    @PostMapping("/findBySportMomentIdAndPage.do")
    @JwtToken(required = true, authorizedRoles = {User.USER_ROLE})
    public ResponseResult<List<UserSportMomentComment>> handleFindByPage(@RequestParam(value = "sportMomentId") Integer sportMomentId,
                                                                         @RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex,
                                                                         @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                                         HttpSession session) {
        List<UserSportMomentComment> userSportMomentCommentList = userSportMomentCommentService.findBySportMomentIdAndPage(sportMomentId, pageIndex, pageSize);
        return new ResponseResult<List<UserSportMomentComment>>(SUCCESS, "查询成功！", userSportMomentCommentList);
    }

}
