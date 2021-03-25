package cn.edu.hestyle.bookstadium.service;

import cn.edu.hestyle.bookstadium.entity.ChatVO;

import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/24 11:16 上午
 */
public interface IChatService {
    /**
     * user分页查找
     * @param userId        userId
     * @param pageIndex     pageIndex
     * @param pageSize      pageSize
     * @return              List ChatVO
     */
    List<ChatVO> userFindByPage(Integer userId, Integer pageIndex, Integer pageSize);
}
