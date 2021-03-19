package cn.edu.hestyle.bookstadium.service;

import cn.edu.hestyle.bookstadium.entity.UserSportMoment;
import cn.edu.hestyle.bookstadium.service.exception.AddFailedException;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;

import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/16 4:56 下午
 */
public interface IUserSportMomentService {
    /**
     * add userSportMoment
     * @param userSportMoment       userSportMoment
     * @throws AddFailedException   插入失败异常
     */
    void add(UserSportMoment userSportMoment) throws AddFailedException;

    /**
     * 点赞sportMoment
     * @param userId                userId
     * @param sportMomentId         sportMomentId
     * @throws AddFailedException   添加失败异常
     */
    void like(Integer userId, Integer sportMomentId) throws AddFailedException;

    /**
     * 取消点赞sportMoment
     * @param userId                userId
     * @param sportMomentId         sportMomentId
     * @throws AddFailedException   添加失败异常
     */
    void dislike(Integer userId, Integer sportMomentId) throws AddFailedException;

    /**
     * 判断是否user是否已点赞该sportMoment
     * @param userId                userId
     * @param sportMomentId         sportMomentId
     * @return                      true false
     * @throws FindFailedException  查询失败异常
     */
    boolean hasLiked(Integer userId, Integer sportMomentId) throws FindFailedException;

    /**
     * 通过sportMomentId进行查找
     * @param sportMomentId         sportMomentId
     * @return                      UserSportMoment
     * @throws FindFailedException  查询失败异常
     */
    UserSportMoment findById(Integer sportMomentId) throws FindFailedException;

    /**
     * 通过contentKey分页查询
     * @param pageIndex             pageIndex
     * @param pageSize              pageSize
     * @return                      List UserSportMoment
     * @throws FindFailedException  查询失败异常
     */
    List<UserSportMoment> findByContentKeyPage(String contentKey, Integer pageIndex, Integer pageSize) throws FindFailedException;

    /**
     * 分页查询UserSportMoment
     * @param pageIndex             pageIndex
     * @param pageSize              pageSize
     * @return                      List UserSportMoment
     * @throws FindFailedException  查询失败异常
     */
    List<UserSportMoment> findByPage(Integer pageIndex, Integer pageSize) throws FindFailedException;
}
