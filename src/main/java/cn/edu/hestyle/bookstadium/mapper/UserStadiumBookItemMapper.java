package cn.edu.hestyle.bookstadium.mapper;

import cn.edu.hestyle.bookstadium.entity.UserStadiumBookItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/15 3:13 下午
 */
@Mapper
public interface UserStadiumBookItemMapper {

    /**
     * 分页查询user的StadiumBookItem
     * @param userId            userId
     * @param beginIndex        beginIndex
     * @param pageSize          pageSize
     * @return                  List UserStadiumBookItemVO
     */
    List<UserStadiumBookItem> findByUserIdAndPage(Integer userId, Integer beginIndex, Integer pageSize);
}
