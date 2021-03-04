package cn.edu.hestyle.bookstadium.controller;

import cn.edu.hestyle.bookstadium.entity.StadiumCategory;
import cn.edu.hestyle.bookstadium.service.IStadiumCategoryService;
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
 * @date 2021/3/4 3:30 下午
 */
@RestController
@RequestMapping("/stadiumCategory")
public class StadiumCategoryController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(StadiumCategoryController.class);

    @Autowired
    private IStadiumCategoryService stadiumCategoryService;

    @PostMapping("/findByIds.do")
    public ResponseResult<List<StadiumCategory>> handleFindByIds(@RequestParam("stadiumCategoryIds") String stadiumCategoryIds, HttpSession session) {
        List<StadiumCategory> stadiumCategoryList = stadiumCategoryService.findByIds(stadiumCategoryIds);
        return new ResponseResult<List<StadiumCategory>>(SUCCESS, "查找成功", stadiumCategoryList);
    }

    @PostMapping("/findByPage.do")
    public ResponseResult<List<StadiumCategory>> handleFindByPage(@RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex,
                                                                        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize, HttpSession session) {
        List<StadiumCategory> stadiumCategoryList = stadiumCategoryService.findByPage(pageIndex, pageSize);
        return new ResponseResult<List<StadiumCategory>>(SUCCESS, "查找成功", stadiumCategoryList);
    }
}
