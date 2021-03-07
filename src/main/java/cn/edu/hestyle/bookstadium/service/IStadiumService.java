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
     * @param stadium               体育场馆
     * @throws AddFailedException   添加失败异常
     */
    void add(String stadiumManagerUsername, Stadium stadium) throws AddFailedException;

    /**
     * 通过stadiumId查找stadium
     * @param stadiumId             stadiumId
     * @return                      stadium
     * @throws FindFailedException  查找失败异常
     */
    Stadium findById(Integer stadiumId) throws FindFailedException;

    /**
     * 修改stadium
     * @param stadiumManagerUsername    stadiumManager用户名
     * @param modifyDataMap             key value
     * @throws ModifyFailedException    修改异常
     */
    void stadiumManagerModify(String stadiumManagerUsername, HashMap<String, Object> modifyDataMap) throws ModifyFailedException;

    /**
     * 删除stadium（只能删除自己的）
     * @param stadiumManagerUsername    stadiumManager用户名
     * @param stadiumIds                id list
     * @throws DeleteFailedException    删除异常
     */
    void stadiumManagerDeleteByIds(String stadiumManagerUsername, List<Integer> stadiumIds) throws DeleteFailedException;

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
