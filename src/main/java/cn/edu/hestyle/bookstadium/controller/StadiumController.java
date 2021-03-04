package cn.edu.hestyle.bookstadium.controller;

import cn.edu.hestyle.bookstadium.controller.exception.NotLoginException;
import cn.edu.hestyle.bookstadium.controller.exception.RequestParamException;
import cn.edu.hestyle.bookstadium.entity.Stadium;
import cn.edu.hestyle.bookstadium.service.IStadiumService;
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
 * @date 2021/3/4 10:43 上午
 */
@RestController
@RequestMapping("/stadium")
public class StadiumController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(StadiumController.class);

    @Autowired
    private IStadiumService stadiumService;

    @PostMapping("/stadiumManagerAdd.do")
    public ResponseResult<Void> handleStadiumManagerAdd(@RequestParam(name = "stadiumData") String stadiumData, HttpSession session) {
        // 判断是否登录
        String username = (String) session.getAttribute("stadiumManagerUsername");
        if (null == username) {
            throw new NotLoginException("请求失败，请先进行登录！");
        }
        ObjectMapper objectMapper = new ObjectMapper();
        Stadium stadium = null;
        try {
            stadium = objectMapper.readValue(stadiumData, Stadium.class);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Stadium 添加失败，数据格式错误！data = " + stadiumData);
            throw new RequestParamException("Stadium 添加失败，数据格式错误！");
        }
        stadiumService.add(username, stadium);
        return new ResponseResult<Void>(SUCCESS, "体育场馆添加成功！");
    }

    @PostMapping("/stadiumManagerFindByPage.do")
    public ResponseResult<List<Stadium>> handleStadiumManagerFindByPage(@RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex,
                                                                        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize, HttpSession session) {
        // 判断是否登录
        String username = (String) session.getAttribute("stadiumManagerUsername");
        if (null == username) {
            throw new NotLoginException("请求失败，请先进行登录！");
        }
        List<Stadium> stadiumList = stadiumService.stadiumManagerFindByPage(username, pageIndex, pageSize);
        Integer count = stadiumService.stadiumManagerGetCount(username);
        return new ResponseResult<List<Stadium>>(SUCCESS, count, stadiumList, "查询成功！");
    }
}
