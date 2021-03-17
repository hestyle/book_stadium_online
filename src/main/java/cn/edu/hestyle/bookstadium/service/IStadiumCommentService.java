package cn.edu.hestyle.bookstadium.service;

import cn.edu.hestyle.bookstadium.entity.StadiumComment;
import cn.edu.hestyle.bookstadium.service.exception.AddFailedException;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;

import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/13 8:55 下午
 */
public interface IStadiumCommentService {
    /**
     * user Comment
     * @param userId                userId
     * @param stadiumComment        stadiumComment
     * @throws AddFailedException   增加失败异常
     */
    void userComment(Integer userId, Integer stadiumBookItemId, StadiumComment stadiumComment) throws AddFailedException;

    /**
     * 通过stadiumCommentId查找
     * @param stadiumCommentId      stadiumCommentId
     * @return                      StadiumComment
     * @throws FindFailedException  查询失败异常
     */
    StadiumComment findByStadiumCommentId(Integer stadiumCommentId) throws FindFailedException;

    /**
     * 分页查询某Stadium的评论
     * @param stadiumId             stadiumId
     * @param pageIndex             pageIndex
     * @param pageSize              pageSize
     * @return                      List StadiumComment
     * @throws FindFailedException  查询失败异常
     */
    List<StadiumComment> findByStadiumIdAndPage(Integer stadiumId, Integer pageIndex, Integer pageSize) throws FindFailedException;
}
