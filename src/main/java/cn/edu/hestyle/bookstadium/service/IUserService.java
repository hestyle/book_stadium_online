package cn.edu.hestyle.bookstadium.service;

import cn.edu.hestyle.bookstadium.entity.User;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;
import cn.edu.hestyle.bookstadium.service.exception.LoginFailedException;
import cn.edu.hestyle.bookstadium.service.exception.RegisterFailedException;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/9 12:45 下午
 */
public interface IUserService {
    /**
     * user 登录
     * @param username                  用户名
     * @param password                  密码
     * @return                          user（剔除密码、盐值）
     * @throws LoginFailedException     登录失败异常
     */
    User login(String username, String password) throws LoginFailedException;

    /**
     * user 注册
     * @param user                      user
     * @throws RegisterFailedException  注册失败异常
     */
    void register(User user) throws RegisterFailedException;

    /**
     * (系统内部)通过id查找(controller不能直接调用，会泄露盐值、token)
     * @param id    id
     * @return      Stadium
     */
    User systemFindById(Integer id) throws FindFailedException;
}
