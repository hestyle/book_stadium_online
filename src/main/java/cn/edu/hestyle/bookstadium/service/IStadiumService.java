package cn.edu.hestyle.bookstadium.service;

import cn.edu.hestyle.bookstadium.entity.Stadium;
import cn.edu.hestyle.bookstadium.service.exception.AddFailedException;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;
import cn.edu.hestyle.bookstadium.service.exception.ModifyFailedException;

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
     * 通过name进行模糊查询
     * @param nameKey               nameKey
     * @return                      List Stadium
     * @throws FindFailedException  查找失败异常
     */
    List<Stadium> findByNameKeyAndPage(String nameKey, Integer pageIndex, Integer pageSize) throws FindFailedException;

    /**
     * 获取stadium个数
     * @return                          stadium个数
     */
    Integer getCount(String nameKey) throws FindFailedException;

    /**
     * 分页查询未删除的Stadium
     * @param pageIndex             页下标
     * @param pageSize              一页大小
     * @return                      List Stadium
     * @throws FindFailedException  查询失败异常
     */
    List<Stadium> findByPage(Integer pageIndex, Integer pageSize) throws FindFailedException;

    /**
     * 分页查询stadiumCategoryId对应的Stadium
     * @param stadiumCategoryId     stadiumCategoryId
     * @param pageIndex             页下标
     * @param pageSize              一页大小
     * @return                      List Stadium
     * @throws FindFailedException  查询失败异常
     */
    List<Stadium> findByStadiumCategoryId(Integer stadiumCategoryId, Integer pageIndex, Integer pageSize) throws FindFailedException;

    /**
     * 修改stadium
     * @param stadiumManagerId          stadiumManagerId
     * @param stadium                   stadium
     * @throws ModifyFailedException    修改异常
     */
    void stadiumManagerModify(Integer stadiumManagerId, Stadium stadium) throws ModifyFailedException;

    /**
     * Stadium分页查询
     * @param stadiumManagerId      stadiumManagerId
     * @param pageIndex             页下标
     * @param pageSize              一页大小
     * @return                      Stadium list
     * @throws FindFailedException  查询失败异常
     */
    List<Stadium> stadiumManagerFindByPage(Integer stadiumManagerId, Integer pageIndex, Integer pageSize, String nameKey) throws FindFailedException;

    /**
     * 获取某stadiumManager的stadium个数
     * @param stadiumManagerId          stadiumManagerId
     * @return                          stadium个数
     */
    Integer stadiumManagerGetCount(Integer stadiumManagerId, String nameKey) throws FindFailedException;

    /**
     * systemManager通过stadiumId删除stadium
     * @param systemManagerId           systemManagerId
     * @param stadiumId                 stadiumId
     * @param deleteReason              删除原因
     */
    void systemManagerDeleteById(Integer systemManagerId, Integer stadiumId, String deleteReason);
}
