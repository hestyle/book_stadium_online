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
     * 添加场馆预约
     * @param stadiumBook   stadiumBook
     */
    void add(StadiumBook stadiumBook);

    /**
     * 更新stadiumBook
     * @param stadiumBook   stadiumBook
     */
    void update(StadiumBook stadiumBook);

    /**
     * 通过id查找StadiumBook
     * @param id        StadiumBook id
     * @return          StadiumBook
     */
    StadiumBook findById(Integer id);

    /**
     * user通过stadiumId分页查询场馆预约（未删除，且now < startTime, 正在进行预约）
     * @param stadiumId             stadiumId
     * @param beginIndex            beginIndex
     * @param pageSize              pageSize
     * @return                      List StadiumBook
     */
    List<StadiumBook> userFindByStadiumIdAndPage(Integer stadiumId, Integer beginIndex, Integer pageSize);

    /**
     * 通过id删除StadiumBook
     * @param id        StadiumBook id
     */
    void deleteById(Integer id);

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
