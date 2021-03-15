package cn.edu.hestyle.bookstadium.service;

import cn.edu.hestyle.bookstadium.entity.UserStadiumBookItem;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;

import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/15 3:00 下午
 */
public interface IUserStadiumBookItemService {

    /**
     * 分页查询user的StadiumBookItem
     * @param userId                userId
     * @param pageIndex             页下标
     * @param pageSize              页大小
     * @return                      List UserStadiumBookItemVO
     * @throws FindFailedException  查找失败异常
     */
    List<UserStadiumBookItem> findByUserIdAndPage(Integer userId, Integer pageIndex, Integer pageSize) throws FindFailedException;

}
