package cn.edu.hestyle.bookstadium.mapper;

import cn.edu.hestyle.bookstadium.entity.SportMomentCommentLike;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/19 10:19 上午
 */
@Mapper
public interface SportMomentCommentLikeMapper {

    /**
     * 添加SportMomentCommentLike
     * @param sportMomentCommentLike    SportMomentCommentLike
     */
    void add(SportMomentCommentLike sportMomentCommentLike);

    /**
     * 通过sportMomentCommentLikeId删除
     * @param sportMomentCommentLikeId  sportMomentCommentLikeId
     */
    void deleteById(Integer sportMomentCommentLikeId);

    /**
     * 通过userId、sportMomentCommentId进行查找
     * @param userId                    userId
     * @param sportMomentCommentId      sportMomentCommentId
     */
    SportMomentCommentLike findByUserIdAndSportMomentCommentId(Integer userId, Integer sportMomentCommentId);
}
