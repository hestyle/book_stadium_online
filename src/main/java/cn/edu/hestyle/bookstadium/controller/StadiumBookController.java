package cn.edu.hestyle.bookstadium.controller;

import cn.edu.hestyle.bookstadium.controller.exception.NotLoginException;
import cn.edu.hestyle.bookstadium.controller.exception.RequestParamException;
import cn.edu.hestyle.bookstadium.entity.StadiumBook;
import cn.edu.hestyle.bookstadium.service.IStadiumBookService;
import cn.edu.hestyle.bookstadium.util.ResponseResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
 * @date 2021/3/6 2:43 下午
 */
@RestController
@RequestMapping("/stadiumBook")
public class StadiumBookController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(StadiumBookController.class);

    @Autowired
    private IStadiumBookService stadiumBookService;

    @PostMapping("/stadiumManagerAdd.do")
    public ResponseResult<Void> handleStadiumManagerAdd(@RequestParam(name = "stadiumBookData") String stadiumBookData, HttpSession session) {
        // 判断是否登录
        String username = (String) session.getAttribute("stadiumManagerUsername");
        if (null == username) {
            throw new NotLoginException("请求失败，请先进行登录！");
        }
        ObjectMapper objectMapper = new ObjectMapper();
        StadiumBook stadiumBook = null;
        try {
            stadiumBook = objectMapper.readValue(stadiumBookData, StadiumBook.class);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumBook 添加失败，数据格式错误！stadiumBookData = " + stadiumBookData);
            throw new RequestParamException("StadiumBook 添加失败，数据格式错误！");
        }
        stadiumBookService.stadiumManagerAdd(username, stadiumBook);
        return new ResponseResult<>(SUCCESS, "添加成功！");
    }

    @PostMapping("/stadiumManagerModify.do")
    public ResponseResult<Void> handleStadiumManagerModify(@RequestParam(name = "stadiumBookModifyData") String stadiumBookModifyData, HttpSession session) {
        // 判断是否登录
        String username = (String) session.getAttribute("stadiumManagerUsername");
        if (null == username) {
            throw new NotLoginException("请求失败，请先进行登录！");
        }
        ObjectMapper objectMapper = new ObjectMapper();
        StadiumBook stadiumBook = null;
        try {
            stadiumBook = objectMapper.readValue(stadiumBookModifyData, StadiumBook.class);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumBook 添加失败，数据格式错误！stadiumBookModifyData = " + stadiumBookModifyData);
            throw new RequestParamException("StadiumBook 修改失败，数据格式错误！");
        }
        stadiumBookService.stadiumManagerModify(username, stadiumBook);
        return new ResponseResult<>(SUCCESS, "修改成功！");
    }

    @PostMapping("/stadiumManagerDeleteByIdList.do")
    public ResponseResult<Void> handleStadiumManagerDeleteByIdList(@RequestParam(name = "idListJsonString") String idListJsonString, HttpSession session) {
        // 判断是否登录
        String username = (String) session.getAttribute("stadiumManagerUsername");
        if (null == username) {
            throw new NotLoginException("请求失败，请先进行登录！");
        }
        List<Integer> idList = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            idList = objectMapper.readValue(idListJsonString, new TypeReference<List<Integer>>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new ResponseResult<>(FAILURE, "批量删除失败，场馆预约id list数据格式不正确！");
        }
        stadiumBookService.stadiumManagerDeleteByIdList(username, idList);
        return new ResponseResult<>(SUCCESS, "批量删除成功！");
    }

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
