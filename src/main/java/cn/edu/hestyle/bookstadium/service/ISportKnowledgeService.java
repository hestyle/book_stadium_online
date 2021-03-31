package cn.edu.hestyle.bookstadium.service;

import cn.edu.hestyle.bookstadium.entity.SportKnowledge;
import cn.edu.hestyle.bookstadium.service.exception.DeleteFailedException;

import java.util.List;

/**
 * @author admin
 * @projectName bookstadium
 * @date 2021/3/30 2:10 下午
 */
public interface ISportKnowledgeService {
    /**
     * 添加sportKnowledge
     * @param sportKnowledge    sportKnowledge
     */
    void add(Integer systemManagerId, SportKnowledge sportKnowledge);

    /**
     * 修改sportKnowledge
     * @param systemManagerId   systemManagerId
     * @param sportKnowledge    sportKnowledge
     */
    void modify(Integer systemManagerId, SportKnowledge sportKnowledge);

    /**
     * 批量删除sportKnowledge
     * @param systemManagerId           systemManagerId
     * @param sportKnowledgeIdList      sportKnowledgeIdList
     * @throws DeleteFailedException    删除异常
     */
    void deleteByIdList(Integer systemManagerId, List<Integer> sportKnowledgeIdList) throws DeleteFailedException;

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
