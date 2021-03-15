package cn.edu.hestyle.bookstadium.mapper;

import cn.edu.hestyle.bookstadium.entity.StadiumComment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/13 8:47 下午
 */
@Mapper
public interface StadiumCommentMapper {
    /**
     * 通过stadiumCommentId查找
     * @param stadiumCommentId  stadiumCommentId
     * @return                  StadiumComment
     */
    StadiumComment findByStadiumCommentId(Integer stadiumCommentId);

    /**
     * 分页查询某个stadium的评论
     * @param stadiumId         stadiumId
     * @param beginIndex        beginIndex
     * @param pageSize          pageSize
     * @return                  List StadiumComment
     */
    List<StadiumComment> findByStadiumIdAndPage(Integer stadiumId, Integer beginIndex, Integer pageSize);
}
