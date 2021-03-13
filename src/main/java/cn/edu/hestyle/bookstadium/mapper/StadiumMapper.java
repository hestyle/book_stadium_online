package cn.edu.hestyle.bookstadium.mapper;

import cn.edu.hestyle.bookstadium.entity.Stadium;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/4 9:50 上午
 */
@Mapper
public interface StadiumMapper {
    /**
     * 添加stadium
     * @param stadium       体育场馆
     */
    void add(Stadium stadium);

    /**
     * 更新stadium
     * @param stadium       stadium
     */
    void update(Stadium stadium);

    /**
     * 通过id查找
     * @param id    id
     * @return      Stadium
     */
    Stadium findById(Integer id);

    /**
     * 分页查询未删除的Stadium
     * @param beginIndex            id升序排列
     * @param pageSize              一页大小
     * @return                      List Stadium
     */
    List<Stadium> findByPage(Integer beginIndex, Integer pageSize);

    /**
     * 分页查询stadiumCategoryId对应的Stadium
     * @param stadiumCategoryId     stadiumCategoryId
     * @param beginIndex            id升序排列
     * @param pageSize              一页大小
     * @return                      List Stadium
     */
    List<Stadium> findByStadiumCategoryId(Integer stadiumCategoryId, Integer beginIndex, Integer pageSize);

    /**
     * 通过id删除
     * @param id    id
     */
    void deleteById(Integer id);

    /**
     * 分页查询某stadiumManager的Stadium
     * @param stadiumManagerId  stadiumManager
     * @param beginIndex        id升序排列
     * @param pageSize          一页大小
     * @return                  Stadium list
     */
    List<Stadium> stadiumManagerFindByPage(Integer stadiumManagerId, Integer beginIndex, Integer pageSize);

    /**
     * 获取某stadiumManager的stadium个数
     * @param stadiumManagerId  stadiumManager
     * @return                  stadium个数
     */
    Integer stadiumManagerGetCount(Integer stadiumManagerId);
}
