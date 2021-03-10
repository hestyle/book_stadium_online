package cn.edu.hestyle.bookstadium.controller;

import cn.edu.hestyle.bookstadium.entity.Banner;
import cn.edu.hestyle.bookstadium.service.IBannerService;
import cn.edu.hestyle.bookstadium.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/10 5:13 下午
 */
@RestController
@RequestMapping("/banner")
public class BannerController extends BaseController {

    @Autowired
    private IBannerService bannerService;

    @PostMapping("/findAll.do")
    public ResponseResult<List<Banner>> handleFindAll(HttpSession session) {
        List<Banner> bannerList = bannerService.findAll();
        return new ResponseResult<List<Banner>>(SUCCESS, "查找成功", bannerList);
    }
}
