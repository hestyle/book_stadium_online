package cn.edu.hestyle.bookstadium.mapper;

import cn.edu.hestyle.bookstadium.entity.StadiumBookItem;
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
     * 通过stadiumBookId进行分页查询
     * @param stadiumBookId         stadiumBookId
     * @param beginIndex            beginIndex
     * @param pageSize              pageSize
     * @return                      List StadiumBookItem
     */
    List<StadiumBookItem> findByStadiumBookIdAndPage(Integer stadiumBookId, Integer beginIndex, Integer pageSize);
}
