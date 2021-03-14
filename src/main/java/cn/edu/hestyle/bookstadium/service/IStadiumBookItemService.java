package cn.edu.hestyle.bookstadium.service;

import cn.edu.hestyle.bookstadium.entity.StadiumBookItem;
import cn.edu.hestyle.bookstadium.service.exception.AddFailedException;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;

import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/14 5:24 下午
 */
public interface IStadiumBookItemService {

    /**
     * user 添加预约
     * @param userId                    userId
     * @param stadiumBookId             stadiumBookId
     * @throws AddFailedException       添加失败异常
     */
    void userAdd(Integer userId, Integer stadiumBookId) throws AddFailedException;

    /**
     * 通过stadiumBookId进行分页查询
     * @param stadiumBookId             stadiumBookId
     * @param pageIndex                 pageIndex
     * @param pageSize                  pageSize
     * @return                          List StadiumBookItem
     * @throws FindFailedException      查询失败异常
     */
    List<StadiumBookItem> findByStadiumBookIdAndPage(Integer stadiumBookId, Integer pageIndex, Integer pageSize) throws FindFailedException;
}
