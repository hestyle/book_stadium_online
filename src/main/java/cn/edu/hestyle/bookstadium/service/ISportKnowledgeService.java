package cn.edu.hestyle.bookstadium.service;

import cn.edu.hestyle.bookstadium.entity.SportKnowledge;

import java.util.List;

/**
 * @author admin
 * @projectName bookstadium
 * @date 2021/3/30 2:10 下午
 */
public interface ISportKnowledgeService {

    /**
     * 通过sportKnowledgeId查找
     * @param sportKnowledgeId  sportKnowledgeId
     * @return                  SportKnowledge
     */
    SportKnowledge findById(Integer sportKnowledgeId);

    /**
     * 分页查询SportKnowledge
     * @param pageIndex     pageIndex
     * @param pageSize      pageSize
     * @return              List SportKnowledge
     */
    List<SportKnowledge> findByPage(Integer pageIndex, Integer pageSize);

    /**
     * 获取sportKnowledge的数量
     * @return              sportKnowledge的数量
     */
    Integer getCount();
}
