package cn.edu.hestyle.bookstadium.service;

import cn.edu.hestyle.bookstadium.entity.Stadium;
import cn.edu.hestyle.bookstadium.service.exception.AddFailedException;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;

import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/4 9:59 上午
 */
public interface IStadiumService {
    /**
     * 添加stadium
     * @param stadium               体育场馆
     * @throws AddFailedException   添加失败异常
     */
    void add(String stadiumManagerUsername, Stadium stadium) throws AddFailedException;

    /**
     * Stadium分页查询
     * @param stadiumManagerUsername 场馆管理员username
     * @param pageIndex             页下标
     * @param pageSize              一页大小
     * @return                      Stadium list
     * @throws FindFailedException  查询失败异常
     */
    List<Stadium> stadiumManagerFindByPage(String stadiumManagerUsername, Integer pageIndex, Integer pageSize) throws FindFailedException;

    /**
     * 获取某stadiumManager的stadium个数
     * @param stadiumManagerUsername    stadiumManager
     * @return                          stadium个数
     */
    Integer stadiumManagerGetCount(String stadiumManagerUsername) throws FindFailedException;
}
