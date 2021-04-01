package cn.edu.hestyle.bookstadium.service;

import cn.edu.hestyle.bookstadium.entity.StadiumComment;
import cn.edu.hestyle.bookstadium.service.exception.AddFailedException;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;
import cn.edu.hestyle.bookstadium.service.exception.ModifyFailedException;

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
     * 管理员回复场馆评论
     * @param stadiumManagerId          stadiumManagerId
     * @param stadiumComment            stadiumComment
     * @throws ModifyFailedException    修改失败异常
     */
    void managerReply(Integer stadiumManagerId, StadiumComment stadiumComment) throws ModifyFailedException;

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

    /**
     * 获取某Stadium的评论数量
     * @param stadiumId         stadiumId
     * @return                  Stadium的评论数量
     */
    Integer getCountByStadiumId(Integer stadiumId) throws FindFailedException;

    /**
     * systemManager通过stadiumCommentId删除
     * @param systemManagerId   systemManagerId
     * @param stadiumCommentId  stadiumCommentId
     * @param deleteReason      删除原因
     */
    void systemManagerDeleteById(Integer systemManagerId, Integer stadiumCommentId, String deleteReason);
}
