package cn.edu.hestyle.bookstadium.service;

import cn.edu.hestyle.bookstadium.entity.StadiumBook;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;

import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/6 11:10 上午
 */
public interface IStadiumBookService {
    /**
     * stadiumManager分页查询其所有Stadium的StadiumBook
     * @param stadiumManagerUsername    stadiumManager用户名
     * @param pageIndex                 起始页
     * @param pageSize                  一页大小
     * @return                          Stadium list
     */
    List<StadiumBook> stadiumManagerFindAllByPage(String stadiumManagerUsername, Integer pageIndex, Integer pageSize) throws FindFailedException;

    /**
     * 获取某stadiumManager的所有stadium对应预约的个数
     * @param stadiumManagerUsername    stadiumManagerUsername
     * @return                          stadiumBook个数
     */
    Integer stadiumManagerGetAllCount(String stadiumManagerUsername) throws FindFailedException;
}
