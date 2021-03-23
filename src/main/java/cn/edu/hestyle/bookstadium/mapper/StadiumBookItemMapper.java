package cn.edu.hestyle.bookstadium.mapper;

import cn.edu.hestyle.bookstadium.entity.StadiumBookItem;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/14 5:10 下午
 */
@Mapper
public interface StadiumBookItemMapper {

    /**
     * 添加stadiumBookItem
     * @param stadiumBookItem       stadiumBookItem
     */
    void add(StadiumBookItem stadiumBookItem);

    /**
     * 更新stadiumBookItem
     * @param stadiumBookItem       stadiumBookItem
     */
    void update(StadiumBookItem stadiumBookItem);

    /**
     * 通过stadiumBookItemId查找
     * @param stadiumBookItemId     stadiumBookItemId
     * @return                      StadiumBookItem
     */
    StadiumBookItem findById(Integer stadiumBookItemId);

    /**
     * 通过stadiumBookId、userId查找
     * @param stadiumBookId         stadiumBookId
     * @param userId                userId
     * @return                      StadiumBookItem
     */
    StadiumBookItem findByStadiumBookIdAndUserId(Integer stadiumBookId, Integer userId);

    /**
     * 通过stadiumBookId进行分页查询
     * @param stadiumBookId         stadiumBookId
     * @param beginIndex            beginIndex
     * @param pageSize              pageSize
     * @return                      List StadiumBookItem
     */
    List<StadiumBookItem> findByStadiumBookIdAndPage(Integer stadiumBookId, Integer beginIndex, Integer pageSize);

    /**
     * 获取stadiumBook的item数
     * @param stadiumBookId             stadiumBookId
     * @return                          count
     * @throws FindFailedException      查找失败异常
     */
    Integer getCountByStadiumBookId(Integer stadiumBookId);
}
