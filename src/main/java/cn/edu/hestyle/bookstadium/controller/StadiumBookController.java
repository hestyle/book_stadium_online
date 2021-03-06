package cn.edu.hestyle.bookstadium.controller;

import cn.edu.hestyle.bookstadium.controller.exception.NotLoginException;
import cn.edu.hestyle.bookstadium.entity.StadiumBook;
import cn.edu.hestyle.bookstadium.service.IStadiumBookService;
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
 * @date 2021/3/6 2:43 下午
 */
@RestController
@RequestMapping("/stadiumBook")
public class StadiumBookController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(StadiumBookController.class);

    @Autowired
    private IStadiumBookService stadiumBookService;

    @PostMapping("/stadiumManagerFindAllByPage.do")
    public ResponseResult<List<StadiumBook>> handleStadiumManagerFindAllByPage(@RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex,
                                                                            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize, HttpSession session) {
        // 判断是否登录
        String username = (String) session.getAttribute("stadiumManagerUsername");
        if (null == username) {
            throw new NotLoginException("请求失败，请先进行登录！");
        }
        List<StadiumBook> stadiumBookList = stadiumBookService.stadiumManagerFindAllByPage(username, pageIndex, pageSize);
        Integer count = stadiumBookService.stadiumManagerGetAllCount(username);
        return new ResponseResult<List<StadiumBook>>(SUCCESS, count, stadiumBookList,"查询成功！");
    }
}
