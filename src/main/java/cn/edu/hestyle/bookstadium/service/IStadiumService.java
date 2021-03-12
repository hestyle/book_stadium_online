package cn.edu.hestyle.bookstadium.service;

import cn.edu.hestyle.bookstadium.entity.Stadium;
import cn.edu.hestyle.bookstadium.service.exception.AddFailedException;
import cn.edu.hestyle.bookstadium.service.exception.DeleteFailedException;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;
import cn.edu.hestyle.bookstadium.service.exception.ModifyFailedException;

import java.util.HashMap;
import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/4 9:59 上午
 */
public interface IStadiumService {
    /**
     * 添加stadium
     * @param stadiumManagerId      stadiumManagerId
     * @param stadium               体育场馆
     * @throws AddFailedException   添加失败异常
     */
    void add(Integer stadiumManagerId, Stadium stadium) throws AddFailedException;

    /**
     * 通过stadiumId查找stadium
     * @param stadiumId             stadiumId
     * @return                      stadium
     * @throws FindFailedException  查找失败异常
     */
    Stadium findById(Integer stadiumId) throws FindFailedException;

    /**
     * 修改stadium
     * @param stadiumManagerId          stadiumManagerId
     * @param stadium                   stadium
     * @throws ModifyFailedException    修改异常
     */
    void stadiumManagerModify(Integer stadiumManagerId, Stadium stadium) throws ModifyFailedException;

    /**
     * 删除stadium（只能删除自己的）
     * @param stadiumManagerId          stadiumManagerId
     * @param stadiumIds                id list
     * @throws DeleteFailedException    删除异常
     */
    void stadiumManagerDeleteByIds(Integer stadiumManagerId, List<Integer> stadiumIds) throws DeleteFailedException;

    /**
     * Stadium分页查询
     * @param stadiumManagerId      stadiumManagerId
     * @param pageIndex             页下标
     * @param pageSize              一页大小
     * @return                      Stadium list
     * @throws FindFailedException  查询失败异常
     */
    List<Stadium> stadiumManagerFindByPage(Integer stadiumManagerId, Integer pageIndex, Integer pageSize) throws FindFailedException;

    /**
     * 获取某stadiumManager的stadium个数
     * @param stadiumManagerId          stadiumManagerId
     * @return                          stadium个数
     */
    Integer stadiumManagerGetCount(Integer stadiumManagerId) throws FindFailedException;
}
