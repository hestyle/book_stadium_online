package cn.edu.hestyle.bookstadium.controller;

import cn.edu.hestyle.bookstadium.entity.SportKnowledge;
import cn.edu.hestyle.bookstadium.service.ISportKnowledgeService;
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
        return new ResponseResult<List<SportKnowledge>>(SUCCESS, "查询成功！", sportKnowledgeList);
    }
}
