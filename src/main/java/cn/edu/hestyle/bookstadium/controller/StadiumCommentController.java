package cn.edu.hestyle.bookstadium.controller;

import cn.edu.hestyle.bookstadium.controller.exception.RequestException;
import cn.edu.hestyle.bookstadium.entity.StadiumComment;
import cn.edu.hestyle.bookstadium.entity.StadiumManager;
import cn.edu.hestyle.bookstadium.entity.User;
import cn.edu.hestyle.bookstadium.jwt.JwtToken;
import cn.edu.hestyle.bookstadium.service.IStadiumCommentService;
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
 * @date 2021/3/13 9:03 下午
 */
@RestController
@RequestMapping("/stadiumComment")
public class StadiumCommentController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(StadiumCommentController.class);

    @Autowired
    private IStadiumCommentService stadiumCommentService;

    @PostMapping("/userComment.do")
    @JwtToken(required = true, authorizedRoles = {User.USER_ROLE})
    public ResponseResult<Void> handleUserComment(@RequestParam(name = "stadiumBookItemId") Integer stadiumBookItemId,
                                                  @RequestParam(name = "stadiumCommentData") String stadiumCommentData,
                                                  HttpSession session) {
        // 从session中取出id
        Integer userId = (Integer) session.getAttribute("id");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        StadiumComment stadiumComment = null;
        try {
            stadiumComment = objectMapper.readValue(stadiumCommentData, StadiumComment.class);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumComment 增加失败，数据格式错误！stadiumCommentData = " + stadiumCommentData);
            throw new RequestException("保存失败，数据格式错误！");
        }
        stadiumCommentService.userComment(userId, stadiumBookItemId, stadiumComment);
        return new ResponseResult<Void>(SUCCESS, "评论成功！");
    }

    @PostMapping("/managerReply.do")
    @JwtToken(required = true, authorizedRoles = {StadiumManager.STADIUM_MANAGER_ROLE})
    public ResponseResult<Void> handleUserComment(@RequestParam(name = "stadiumCommentData") String stadiumCommentData, HttpSession session) {
        // 从session中取出id
        Integer stadiumManagerId = (Integer) session.getAttribute("id");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        StadiumComment stadiumComment = null;
        try {
            stadiumComment = objectMapper.readValue(stadiumCommentData, StadiumComment.class);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumComment 回复失败，数据格式错误！stadiumCommentData = " + stadiumCommentData);
            throw new RequestException("回复失败，数据格式错误！");
        }
        stadiumCommentService.managerReply(stadiumManagerId, stadiumComment);
        return new ResponseResult<Void>(SUCCESS, "回复成功！");
    }

    @PostMapping("/findByStadiumCommentId.do")
    public ResponseResult<StadiumComment> handleFindByStadiumCommentId(@RequestParam(name = "stadiumCommentId") Integer stadiumCommentId, HttpSession session) {
        StadiumComment stadiumComment = stadiumCommentService.findByStadiumCommentId(stadiumCommentId);
        return new ResponseResult<StadiumComment>(SUCCESS, "查询成功！", stadiumComment);
    }

    @PostMapping("/findByStadiumIdAndPage.do")
    public ResponseResult<List<StadiumComment>> handleFindByStadiumIdAndPage(@RequestParam(name = "stadiumId") Integer stadiumId,
                                                                             @RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex,
                                                                             @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize, HttpSession session) {
        List<StadiumComment> stadiumCommentList = stadiumCommentService.findByStadiumIdAndPage(stadiumId, pageIndex, pageSize);
        return new ResponseResult<List<StadiumComment>>(SUCCESS, "查询成功！", stadiumCommentList);
    }

}
