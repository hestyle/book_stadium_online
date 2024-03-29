package cn.edu.hestyle.bookstadium.service.impl;

import cn.edu.hestyle.bookstadium.entity.SportKnowledge;
import cn.edu.hestyle.bookstadium.entity.SystemManager;
import cn.edu.hestyle.bookstadium.mapper.SportKnowledgeMapper;
import cn.edu.hestyle.bookstadium.mapper.SystemManagerMapper;
import cn.edu.hestyle.bookstadium.service.ISportKnowledgeService;
import cn.edu.hestyle.bookstadium.service.exception.AddFailedException;
import cn.edu.hestyle.bookstadium.service.exception.DeleteFailedException;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author admin
 * @projectName bookstadium
 * @date 2021/3/30 2:12 下午
 */
@Service
public class SportKnowledgeServiceImpl implements ISportKnowledgeService {
    /** title最长字符数 */
    private static final Integer SPORT_KNOWLEDGE_TITLE_MAX_LENGTH = 20;
    /** content最长字符数 */
    private static final Integer SPORT_KNOWLEDGE_CONTENT_MAX_LENGTH = 500;

    private static final Logger logger = LoggerFactory.getLogger(SportKnowledgeServiceImpl.class);

    @Resource
    private SystemManagerMapper systemManagerMapper;
    @Resource
    private SportKnowledgeMapper sportKnowledgeMapper;

    @Override
    public void add(Integer systemManagerId, SportKnowledge sportKnowledge) {
        SystemManager systemManager = null;
        try {
            systemManager = systemManagerMapper.findById(systemManagerId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SystemManager 查询失败，数据库发生未知异常！");
            throw new AddFailedException("添加失败，数据库发生未知异常!");
        }
        if (sportKnowledge == null) {
            logger.warn("SportKnowledge 增加失败，未传入SportKnowledge参数！");
            throw new AddFailedException("操作失败，未传入SportKnowledge参数！");
        }
        String title = sportKnowledge.getTitle();
        if (title == null || title.length() == 0) {
            logger.warn("SportKnowledge 增加失败，运动常识title为空！sportKnowledge = " + sportKnowledge);
            throw new AddFailedException("操作失败，运动常识title不能为空！");
        }
        if (title.length() > SPORT_KNOWLEDGE_TITLE_MAX_LENGTH) {
            logger.warn("SportKnowledge 增加失败，运动常识title过长！sportKnowledge = " + sportKnowledge);
            throw new AddFailedException("操作失败，运动常识title超过了" + SPORT_KNOWLEDGE_TITLE_MAX_LENGTH + "个字符！");
        }
        String content = sportKnowledge.getContent();
        if (content == null || content.length() == 0) {
            logger.warn("SportKnowledge 增加失败，运动常识content为空！sportKnowledge = " + sportKnowledge);
            throw new AddFailedException("操作失败，运动常识content不能为空！");
        }
        if (content.length() > SPORT_KNOWLEDGE_CONTENT_MAX_LENGTH) {
            logger.warn("SportKnowledge 增加失败，运动常识content过长！sportKnowledge = " + sportKnowledge);
            throw new AddFailedException("操作失败，运动常识content超过了" + SPORT_KNOWLEDGE_CONTENT_MAX_LENGTH + "个字符！");
        }
        sportKnowledge.setId(null);
        sportKnowledge.setCreatedUser(systemManager.getUsername());
        sportKnowledge.setCreatedTime(new Date());
        sportKnowledge.setIsDelete(0);
        try {
            sportKnowledgeMapper.add(sportKnowledge);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SportKnowledge 增加失败，数据库发生未知异常！SportKnowledge = " + sportKnowledge);
            throw new AddFailedException("操作失败，数据库发生未知异常!");
        }
        logger.warn("SportKnowledge 增加成功！SportKnowledge = " + sportKnowledge);
    }

    @Override
    public void modify(Integer systemManagerId, SportKnowledge sportKnowledge) {
        SystemManager systemManager = null;
        try {
            systemManager = systemManagerMapper.findById(systemManagerId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SystemManager 查询失败，数据库发生未知异常！");
            throw new AddFailedException("添加失败，数据库发生未知异常!");
        }
        if (sportKnowledge == null || sportKnowledge.getId() == null) {
            logger.warn("SportKnowledge 增加失败，未传入SportKnowledge参数！");
            throw new AddFailedException("操作失败，未传入SportKnowledge参数！");
        }
        SportKnowledge sportKnowledgeModify = null;
        try {
            sportKnowledgeModify = sportKnowledgeMapper.findById(sportKnowledge.getId());
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SportKnowledge 查询失败，数据库发生未知异常！sportKnowledgeId = " + sportKnowledge.getId());
            throw new AddFailedException("操作失败，数据库发生未知异常!");
        }
        String title = sportKnowledge.getTitle();
        if (title == null || title.length() == 0) {
            logger.warn("SportKnowledge 修改失败，运动常识title为空！sportKnowledge = " + sportKnowledge);
            throw new AddFailedException("操作失败，运动常识title不能为空！");
        } else if (title.length() > SPORT_KNOWLEDGE_TITLE_MAX_LENGTH) {
            logger.warn("SportKnowledge 修改失败，运动常识title过长！sportKnowledge = " + sportKnowledge);
            throw new AddFailedException("操作失败，运动常识title超过了" + SPORT_KNOWLEDGE_TITLE_MAX_LENGTH + "个字符！");
        } else {
            sportKnowledgeModify.setTitle(title);
        }
        String content = sportKnowledge.getContent();
        if (content == null || content.length() == 0) {
            logger.warn("SportKnowledge 修改失败，运动常识content为空！sportKnowledge = " + sportKnowledge);
            throw new AddFailedException("操作失败，运动常识content不能为空！");
        } else if (content.length() > SPORT_KNOWLEDGE_CONTENT_MAX_LENGTH) {
            logger.warn("SportKnowledge 修改失败，运动常识content过长！sportKnowledge = " + sportKnowledge);
            throw new AddFailedException("操作失败，运动常识content超过了" + SPORT_KNOWLEDGE_CONTENT_MAX_LENGTH + "个字符！");
        } else {
            sportKnowledgeModify.setContent(content);
        }
        sportKnowledgeModify.setModifiedUser(systemManager.getUsername());
        sportKnowledgeModify.setModifiedTime(new Date());
        try {
            sportKnowledgeMapper.update(sportKnowledgeModify);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SportKnowledge 修改失败，数据库发生未知异常！sportKnowledgeModify = " + sportKnowledgeModify);
            throw new AddFailedException("操作失败，数据库发生未知异常!");
        }
        logger.warn("SportKnowledge 修改成功！sportKnowledgeModify = " + sportKnowledgeModify);
    }

    @Override
    @Transactional
    public void deleteByIdList(Integer systemManagerId, List<Integer> sportKnowledgeIdList) throws DeleteFailedException {
        SystemManager systemManager = null;
        try {
            systemManager = systemManagerMapper.findById(systemManagerId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SystemManager 查询失败，数据库发生未知异常！");
            throw new AddFailedException("添加失败，数据库发生未知异常!");
        }
        if (sportKnowledgeIdList == null || sportKnowledgeIdList.size() == 0) {
            logger.warn("SportKnowledge 删除失败，未传入有效的sportKnowledgeIdList参数！");
            throw new AddFailedException("操作失败，未传入有效的sportKnowledgeIdList参数!");
        }
        logger.warn("SportKnowledge批量删除事务开始-----");
        for (Integer sportKnowledgeId : sportKnowledgeIdList) {
            SportKnowledge sportKnowledge = null;
            try {
                sportKnowledge = sportKnowledgeMapper.findById(sportKnowledgeId);
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("SportKnowledge 查询失败，数据库发生未知异常！SportKnowledge批量删除事务回滚-----");
                throw new AddFailedException("操作失败，数据库发生未知异常!");
            }
            if (sportKnowledge == null) {
                logger.warn("SportKnowledge 查询失败，sportKnowledgeId = " + sportKnowledgeId + " 不存在！ SportKnowledge批量删除事务回滚-----");
                throw new AddFailedException("操作失败，数据库发生未知异常!");
            }
            sportKnowledge.setModifiedUser(systemManager.getUsername());
            sportKnowledge.setModifiedTime(new Date());
            sportKnowledge.setIsDelete(1);
            try {
                sportKnowledgeMapper.update(sportKnowledge);
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("SportKnowledge 更新失败，数据库发生未知异常！sportKnowledge = " + sportKnowledge + "SportKnowledge批量删除事务回滚-----");
                throw new AddFailedException("操作失败，数据库发生未知异常!");
            }
            logger.warn("SportKnowledge 更新成功！sportKnowledge = " + sportKnowledge);
        }
        logger.warn("SportKnowledge批量删除事务提交-----");
    }

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

    @Override
    public Integer getCount() {
        Integer count = 0;
        try {
            count = sportKnowledgeMapper.getCount();
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SportKnowledge 查找失败，数据库发生未知错误！");
            throw new FindFailedException("查找失败，数据库发生未知错误！");
        }
        return count;
    }
}
