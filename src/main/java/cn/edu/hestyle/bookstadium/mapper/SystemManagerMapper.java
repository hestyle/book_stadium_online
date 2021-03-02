package cn.edu.hestyle.bookstadium.mapper;

import cn.edu.hestyle.bookstadium.entity.SystemManager;
import org.apache.ibatis.annotations.Mapper;

/**
 * SystemManager 持久层接口
 * @author hestyle
 */
@Mapper
public interface SystemManagerMapper {
    /**
     * 通过用户名查询SystemManager
     * @param username  用户名
     * @return          SystemManager
     */
    SystemManager findByUsername(String username);
}
