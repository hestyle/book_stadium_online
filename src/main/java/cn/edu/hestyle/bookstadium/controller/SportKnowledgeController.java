package cn.edu.hestyle.bookstadium.controller;

import cn.edu.hestyle.bookstadium.controller.exception.RequestException;
import cn.edu.hestyle.bookstadium.entity.SportKnowledge;
import cn.edu.hestyle.bookstadium.entity.SystemManager;
import cn.edu.hestyle.bookstadium.jwt.JwtToken;
import cn.edu.hestyle.bookstadium.service.ISportKnowledgeService;
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
 * @author admin
 * @projectName bookstadium
 * @date 2021/3/30 2:17 下午
 */
@RestController
@RequestMapping("/sportKnowledge")
public class SportKnowledgeController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(SportKnowledgeController.class);

    @Autowired
    private ISportKnowledgeService sportKnowledgeService;

    @PostMapping("/add.do")
    @JwtToken(required = true, authorizedRoles = {SystemManager.SYSTEM_MANAGER_ROLE})
    public ResponseResult<Void> handleAdd(@RequestParam(name = "sportKnowledgeData") String sportKnowledgeData, HttpSession session) {
        // 从session中取出id
        Integer systemManagerId = (Integer) session.getAttribute("id");
        ObjectMapper objectMapper = new ObjectMapper();
        SportKnowledge sportKnowledge = null;
        try {
            sportKnowledge = objectMapper.readValue(sportKnowledgeData, SportKnowledge.class);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SportKnowledge 添加失败，数据格式错误！data = " + sportKnowledgeData);
            throw new RequestException("SportKnowledge 添加失败，数据格式错误！");
        }
        sportKnowledgeService.add(systemManagerId, sportKnowledge);
        return new ResponseResult<Void>(SUCCESS, "添加成功！");
    }

    @PostMapping("/findById.do")
    public ResponseResult<SportKnowledge> handleFindById(@RequestParam(value = "sportKnowledgeId") Integer sportKnowledgeId, HttpSession session) {
        SportKnowledge sportKnowledge = sportKnowledgeService.findById(sportKnowledgeId);
        return new ResponseResult<SportKnowledge>(SUCCESS, "查询成功！", sportKnowledge);
    }

    @PostMapping("/findByPage.do")
    public ResponseResult<List<SportKnowledge>> handleFindByPage(@RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex,
                                                                 @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                                 HttpSession session) {
        List<SportKnowledge> sportKnowledgeList = sportKnowledgeService.findByPage(pageIndex, pageSize);
        Integer count = sportKnowledgeService.getCount();
        return new ResponseResult<List<SportKnowledge>>(SUCCESS, count, sportKnowledgeList, "查询成功！");
    }
}
