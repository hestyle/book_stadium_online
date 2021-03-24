package cn.edu.hestyle.bookstadium.mapper;

import cn.edu.hestyle.bookstadium.entity.Chat;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/24 10:56 上午
 */
@Mapper
public interface ChatMapper {
    /**
     * user分页查找Chat
     * @param userId            userId
     * @param beginIndex        beginIndex
     * @param pageSize          pageSize
     * @return                  List Chat
     */
    List<Chat> userFindByPage(Integer userId, Integer beginIndex, Integer pageSize);
}
