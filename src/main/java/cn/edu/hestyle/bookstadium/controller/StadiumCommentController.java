package cn.edu.hestyle.bookstadium.controller;

import cn.edu.hestyle.bookstadium.entity.StadiumComment;
import cn.edu.hestyle.bookstadium.service.IStadiumCommentService;
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
 * @date 2021/3/13 9:03 下午
 */
@RestController
@RequestMapping("/stadiumComment")
public class StadiumCommentController extends BaseController {

    @Autowired
    private IStadiumCommentService stadiumCommentService;

    @PostMapping("/findByStadiumIdAndPage.do")
    public ResponseResult<List<StadiumComment>> handleFindByStadiumIdAndPage(@RequestParam(name = "stadiumId") Integer stadiumId,
                                                                             @RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex,
                                                                             @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize, HttpSession session) {
        List<StadiumComment> stadiumCommentList = stadiumCommentService.findByStadiumIdAndPage(stadiumId, pageIndex, pageSize);
        return new ResponseResult<List<StadiumComment>>(SUCCESS, "查询成功！", stadiumCommentList);
    }

}
