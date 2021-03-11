package cn.edu.hestyle.bookstadium.service;

import cn.edu.hestyle.bookstadium.entity.StadiumBook;
import cn.edu.hestyle.bookstadium.service.exception.AddFailedException;
import cn.edu.hestyle.bookstadium.service.exception.DeleteFailedException;
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
     * @param stadiumManagerId          stadiumManagerId
     * @param stadiumBook               stadiumBook
     * @throws AddFailedException       添加失败异常
     */
    void stadiumManagerAdd(Integer stadiumManagerId, StadiumBook stadiumBook) throws AddFailedException;

    /**
     * stadiumManager 修改stadiumBook
     * @param stadiumManagerId          stadiumManagerId
     * @param stadiumBook               stadiumBook
     * @throws ModifyFailedException    修改失败异常
     */
    void stadiumManagerModify(Integer stadiumManagerId, StadiumBook stadiumBook) throws ModifyFailedException;

    /**
     * stadiumManager 批量删除 stadiumBook
     * @param stadiumManagerId          stadiumManagerId
     * @param stadiumBookIdList         stadiumBookId List
     * @throws DeleteFailedException    删除失败异常
     */
    void stadiumManagerDeleteByIdList(Integer stadiumManagerId, List<Integer> stadiumBookIdList) throws DeleteFailedException;

    /**
     * stadiumManager分页查询其所有Stadium的StadiumBook
     * @param stadiumManagerId          stadiumManagerId
     * @param pageIndex                 起始页
     * @param pageSize                  一页大小
     * @return                          Stadium list
     */
    List<StadiumBook> stadiumManagerFindAllByPage(Integer stadiumManagerId, Integer pageIndex, Integer pageSize) throws FindFailedException;

    /**
     * 获取某stadiumManager的所有stadium对应预约的个数
     * @param stadiumManagerId          stadiumManagerId
     * @return                          stadiumBook个数
     */
    Integer stadiumManagerGetAllCount(Integer stadiumManagerId) throws FindFailedException;
}
