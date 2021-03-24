package cn.edu.hestyle.bookstadium.mapper;

import cn.edu.hestyle.bookstadium.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/24 3:56 下午
 */
@Mapper
public interface ChatMessageMapper {
    /**
     * 通过id查找ChatMessage
     * @param id        id
     * @return          ChatMessage
     */
    ChatMessage findById(Integer id);
}
