package cn.edu.hestyle.bookstadium.service.impl;

import cn.edu.hestyle.bookstadium.entity.SportKnowledge;
import cn.edu.hestyle.bookstadium.mapper.SportKnowledgeMapper;
import cn.edu.hestyle.bookstadium.service.ISportKnowledgeService;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author admin
 * @projectName bookstadium
 * @date 2021/3/30 2:12 下午
 */
@Service
public class SportKnowledgeServiceImpl implements ISportKnowledgeService {
    private static final Logger logger = LoggerFactory.getLogger(SportKnowledgeServiceImpl.class);

    @Resource
    private SportKnowledgeMapper sportKnowledgeMapper;

    @Override
    public SportKnowledge findById(Integer sportKnowledgeId) {
        if (sportKnowledgeId == null) {
            logger.warn("SportKnowledge 查找失败，未传入sportKnowledgeId参数！");
            throw new FindFailedException("查询失败，未传入sportKnowledgeId参数！");
        }
        SportKnowledge sportKnowledge = null;
        try {
            sportKnowledge = sportKnowledgeMapper.findById(sportKnowledgeId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SportKnowledge 查找失败，数据库发生未知错误！sportKnowledgeId = " + sportKnowledgeId);
            throw new FindFailedException("查找失败，数据库发生未知错误！");
        }
        return sportKnowledge;
    }

    @Override
    public List<SportKnowledge> findByPage(Integer pageIndex, Integer pageSize) {
        // 检查页码是否合法
        if (pageIndex < 1) {
            throw new FindFailedException("查询失败，页码 " + pageIndex + " 非法，必须大于0！");
        }
        // 检查页大小是否合法
        if (pageSize < 1) {
            throw new FindFailedException("查询失败，页大小 " + pageSize + " 非法，必须大于0！");
        }
        List<SportKnowledge> sportKnowledgeList = null;
        try {
            sportKnowledgeList = sportKnowledgeMapper.findByPage((pageIndex - 1) * pageSize, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SportKnowledge 查找失败，数据库发生未知错误！pageIndex = " + pageIndex + "，pageSize = " + pageSize);
            throw new FindFailedException("查找失败，数据库发生未知错误！");
        }
        return sportKnowledgeList;
    }
}
