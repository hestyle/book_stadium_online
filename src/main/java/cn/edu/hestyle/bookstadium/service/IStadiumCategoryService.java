package cn.edu.hestyle.bookstadium.service;

import cn.edu.hestyle.bookstadium.entity.StadiumCategory;
import cn.edu.hestyle.bookstadium.service.exception.AddFailedException;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;

import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/4 3:02 下午
 */
public interface IStadiumCategoryService {

    /**
     * 添加StadiumCategory
     * @param systemManagerId       systemManagerId
     * @param stadiumCategory       stadiumCategory
     * @throws AddFailedException   添加失败异常
     */
    void add(Integer systemManagerId, StadiumCategory stadiumCategory) throws AddFailedException;

    /**
     * 通过id查找StadiumCategory
     * @param id        StadiumCategory id
     * @return          StadiumCategory
     */
    StadiumCategory findById(Integer id) throws FindFailedException;

    /**
     * 通过ids列表查找StadiumCategory
     * @param stadiumCategoryIds    以逗号间隔的id
     * @return                      StadiumCategory list
     */
    List<StadiumCategory> findByIds(String stadiumCategoryIds) throws FindFailedException;

    /**
     * 分页查询
     * @param pageIndex             页下标
     * @param pageSize              页大小
     * @return                      StadiumCategory List
     * @throws FindFailedException  查询失败异常
     */
    List<StadiumCategory> findByPage(Integer pageIndex, Integer pageSize) throws FindFailedException;
}
