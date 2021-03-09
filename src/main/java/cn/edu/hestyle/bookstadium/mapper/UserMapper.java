package cn.edu.hestyle.bookstadium.mapper;

import cn.edu.hestyle.bookstadium.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/9 12:41 下午
 */
@Mapper
public interface UserMapper {
    /**
     * 通过id查找
     * @param id    id
     * @return      Stadium
     */
    User findById(Integer id);
}
