package cn.edu.hestyle.bookstadium.mapper;

import cn.edu.hestyle.bookstadium.entity.SportKnowledge;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author admin
 * @projectName bookstadium
 * @date 2021/3/30 1:57 下午
 */
@Mapper
public interface SportKnowledgeMapper {
    /**
     * 通过sportKnowledgeId查找
     * @param sportKnowledgeId  sportKnowledgeId
     * @return                  SportKnowledge
     */
    SportKnowledge findById(Integer sportKnowledgeId);

    /**
     * 分页查询SportKnowledge
     * @param beginIndex    beginIndex
     * @param pageSize      pageSize
     * @return              List SportKnowledge
     */
    List<SportKnowledge> findByPage(Integer beginIndex, Integer pageSize);
}
