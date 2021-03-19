package cn.edu.hestyle.bookstadium.service;

import cn.edu.hestyle.bookstadium.entity.UserSportMomentComment;
import cn.edu.hestyle.bookstadium.service.exception.AddFailedException;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;

import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/18 4:18 下午
 */
public interface IUserSportMomentCommentService {

    /**
     * 点赞sportMomentComment
     * @param userId                userId
     * @param sportMomentCommentId  sportMomentCommentId
     * @throws AddFailedException   添加失败异常
     */
    void like(Integer userId, Integer sportMomentCommentId) throws AddFailedException;

    /**
     * 通过sportMomentId进行分页查找
     * @param sportMomentId         sportMomentId
     * @param pageIndex             pageIndex
     * @param pageSize              pageSize
     * @return                      List UserSportMomentComment
     * @throws FindFailedException  查找失败异常
     */
    List<UserSportMomentComment> findBySportMomentIdAndPage(Integer sportMomentId, Integer pageIndex, Integer pageSize) throws FindFailedException;
}
