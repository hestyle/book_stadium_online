package cn.edu.hestyle.bookstadium.service;

import cn.edu.hestyle.bookstadium.entity.StadiumBook;
import cn.edu.hestyle.bookstadium.service.exception.AddFailedException;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;
import cn.edu.hestyle.bookstadium.service.exception.ModifyFailedException;

import java.util.HashMap;
import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/6 11:10 上午
 */
public interface IStadiumBookService {
    /**
     * stadiumManager 添加stadiumBook
     * @param stadiumManagerUsername    stadiumManager用户名
     * @param stadiumBook               stadiumBook
     * @throws AddFailedException       添加失败异常
     */
    void stadiumManagerAdd(String stadiumManagerUsername, StadiumBook stadiumBook) throws AddFailedException;

    /**
     * stadiumManager 修改stadiumBook
     * @param stadiumManagerUsername    stadiumManager用户名
     * @param stadiumBook               stadiumBook
     * @throws ModifyFailedException    修改失败异常
     */
    void stadiumManagerModify(String stadiumManagerUsername, StadiumBook stadiumBook) throws ModifyFailedException;

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
