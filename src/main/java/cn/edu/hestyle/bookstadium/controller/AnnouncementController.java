package cn.edu.hestyle.bookstadium.controller;

import cn.edu.hestyle.bookstadium.controller.exception.RequestException;
import cn.edu.hestyle.bookstadium.entity.Announcement;
import cn.edu.hestyle.bookstadium.entity.Banner;
import cn.edu.hestyle.bookstadium.entity.SystemManager;
import cn.edu.hestyle.bookstadium.jwt.JwtToken;
import cn.edu.hestyle.bookstadium.service.IAnnouncementService;
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

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/23 12:51 下午
 */
@RestController
@RequestMapping("/announcement")
public class AnnouncementController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AnnouncementController.class);

    @Autowired
    private IAnnouncementService announcementService;

    @PostMapping("/save.do")
    @JwtToken(required = true, authorizedRoles = {SystemManager.SYSTEM_MANAGER_ROLE})
    public ResponseResult<Void> handleSave(@RequestParam(name = "announcementData") String announcementData, HttpSession session) {
        // 从session中取出id
        Integer systemManagerId = (Integer) session.getAttribute("id");
        ObjectMapper objectMapper = new ObjectMapper();
        Announcement announcement = null;
        try {
            announcement = objectMapper.readValue(announcementData, Announcement.class);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Announcement 保存失败，数据格式错误！data = " + announcementData);
            throw new RequestException("Banner 添加失败，数据格式错误！");
        }
        announcementService.save(announcement);
        return new ResponseResult<Void>(SUCCESS, "保存成功！");
    }

    @PostMapping("/find.do")
    public ResponseResult<Announcement> handleFind(HttpSession session) {
        Announcement announcement = announcementService.find();
        return new ResponseResult<Announcement>(SUCCESS, "查找成功", announcement);
    }

}
