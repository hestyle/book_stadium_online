package cn.edu.hestyle.bookstadium.service;

import cn.edu.hestyle.bookstadium.entity.UserSportMomentComment;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;

import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/18 4:18 下午
 */
public interface IUserSportMomentCommentService {

    /**
     * 通过sportMomentId进行分页查找
     * @param sportMomentId
     * @param pageIndex
     * @param pageSize
     * @return
     * @throws FindFailedException
     */
    List<UserSportMomentComment> findBySportMomentIdAndPage(Integer sportMomentId, Integer pageIndex, Integer pageSize) throws FindFailedException;
}
