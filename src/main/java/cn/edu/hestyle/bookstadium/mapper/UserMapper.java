package cn.edu.hestyle.bookstadium.mapper;

import cn.edu.hestyle.bookstadium.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/9 12:41 下午
 */
@Mapper
public interface UserMapper {

    /**
     * 添加user
     * @param user  user
     */
    void add(User user);

    /**
     * 更新user
     * @param user  user
     */
    void update(User user);

    /**
     * 通过id查找
     * @param id    id
     * @return      User
     */
    User findById(Integer id);

    /**
     * 通过username查找
     * @param username  username
     * @return          User
     */
    User findByUsername(String username);

    /**
     * 分页查询user(未删除的)
     * @param beginIndex    beginIndex
     * @param pageSize      pageSize
     * @return              List User
     */
    List<User> findByPage(Integer beginIndex, Integer pageSize);

    /**
     * 获取User数量
     * @return              user数量
     */
    Integer getCount();
}
