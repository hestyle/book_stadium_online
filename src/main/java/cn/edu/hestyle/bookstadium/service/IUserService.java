package cn.edu.hestyle.bookstadium.service;

import cn.edu.hestyle.bookstadium.entity.User;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/9 12:45 下午
 */
public interface IUserService {
    /**
     * 通过id查找(controller不能直接调用，会泄露盐值、token)
     * @param id    id
     * @return      Stadium
     */
    User findById(Integer id) throws FindFailedException;
}
