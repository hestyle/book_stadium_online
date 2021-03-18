package cn.edu.hestyle.bookstadium.mapper;

import cn.edu.hestyle.bookstadium.entity.SportMomentComment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/18 4:07 下午
 */
@Mapper
public interface SportMomentCommentMapper {
    /**
     * 通过sportMomentCommentId查找
     * @param sportMomentCommentId      sportMomentCommentId
     * @return                          SportMomentComment
     */
    SportMomentComment findById(Integer sportMomentCommentId);

    /**
     * 通过sportMomentId进行分页查找
     * @param sportMomentId     sportMomentId
     * @param beginIndex        beginIndex
     * @param pageSize          pageSize
     * @return                  List SportMomentComment
     */
    List<SportMomentComment> findBySportMomentIdAndPage(Integer sportMomentId, Integer beginIndex, Integer pageSize);
}
