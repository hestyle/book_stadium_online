package cn.edu.hestyle.bookstadium.mapper;

import cn.edu.hestyle.bookstadium.entity.StadiumBook;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/6 10:47 上午
 */
@Mapper
public interface StadiumBookMapper {
    /**
     * stadiumManager分页查询所有Stadium的StadiumBook
     * @param stadiumManagerId      stadiumManagerId
     * @param beginIndex            id升序排列
     * @param pageSize              一页大小
     * @return                      Stadium list
     */
    List<StadiumBook> stadiumManagerFindAllByPage(Integer stadiumManagerId, Integer beginIndex, Integer pageSize);

    /**
     * 获取某stadiumManager的所有stadium对应预约的个数
     * @param stadiumManagerId  stadiumManager
     * @return                  stadiumBook个数
     */
    Integer stadiumManagerGetAllCount(Integer stadiumManagerId);
}
