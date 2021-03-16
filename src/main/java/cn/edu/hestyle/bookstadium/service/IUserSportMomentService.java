package cn.edu.hestyle.bookstadium.service;

import cn.edu.hestyle.bookstadium.entity.UserSportMoment;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;

import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/16 4:56 下午
 */
public interface IUserSportMomentService {

    /**
     * 分页查询UserSportMoment
     * @param pageIndex             pageIndex
     * @param pageSize              pageSize
     * @return                      List UserSportMoment
     * @throws FindFailedException  查询失败异常
     */
    List<UserSportMoment> findByPage(Integer pageIndex, Integer pageSize) throws FindFailedException;
}
