package cn.edu.hestyle.bookstadium.mapper;

import cn.edu.hestyle.bookstadium.entity.StadiumCategory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/4 2:52 下午
 */
@Mapper
public interface StadiumCategoryMapper {
    /**
     * 添加stadiumCategory
     * @param stadiumCategory   stadiumCategory
     */
    void add(StadiumCategory stadiumCategory);

    /**
     * 通过id查找StadiumCategory
     * @param id        StadiumCategory id
     * @return          StadiumCategory
     */
    StadiumCategory findById(Integer id);

    /**
     * 分页查询
     * @param beginIndex    起始下标
     * @param pageSize      查询数量
     * @return              StadiumCategory list
     */
    List<StadiumCategory> findByPage(Integer beginIndex, Integer pageSize);

    /**
     * 获取StadiumCategory个数
     * @return                          stadium个数
     */
    Integer getAllCount();
}
