package cn.edu.hestyle.bookstadium.mapper;

import cn.edu.hestyle.bookstadium.entity.SportMomentLike;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/18 10:32 上午
 */
@Mapper
public interface SportMomentLikeMapper {
    /**
     * 添加sportMomentLike
     * @param sportMomentLike   sportMomentLike
     */
    void add(SportMomentLike sportMomentLike);

    /**
     * 通过sportMomentLikeId删除
     * @param sportMomentLikeId sportMomentLikeId
     */
    void delete(Integer sportMomentLikeId);

    /**
     * 通过userId、sportMomentId进行查找
     * @param userId            userId
     * @param sportMomentId     sportMomentId
     * @return                  SportMomentLike
     */
    SportMomentLike findByUserIdAndSportMomentId(Integer userId, Integer sportMomentId);
}
