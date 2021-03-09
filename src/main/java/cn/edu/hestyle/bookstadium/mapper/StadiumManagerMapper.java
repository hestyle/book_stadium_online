package cn.edu.hestyle.bookstadium.mapper;

import cn.edu.hestyle.bookstadium.entity.StadiumManager;
import org.apache.ibatis.annotations.Mapper;

/**
 * StadiumManager 持久层接口
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/3 10:11 上午
 */
@Mapper
public interface StadiumManagerMapper {
    /**
     * 通过username 查找 StadiumManager
     * @param username  用户名
     * @return          StadiumManager
     */
    StadiumManager findByUsername(String username);

    /**
     * 通过id进行查找
     * @param id        StadiumManager id
     * @return          StadiumManager
     */
    StadiumManager findById(Integer id);

    /**
     * StadiumManager 账号添加
     * @param stadiumManager    待添加的账号
     */
    void add(StadiumManager stadiumManager);

    /**
     * StadiumManager 账号更新
     * @param stadiumManager    待更新的账号
     */
    void update(StadiumManager stadiumManager);
}
