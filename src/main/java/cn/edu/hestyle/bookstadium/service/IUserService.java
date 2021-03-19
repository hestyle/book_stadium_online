package cn.edu.hestyle.bookstadium.service;

import cn.edu.hestyle.bookstadium.entity.User;
import cn.edu.hestyle.bookstadium.service.exception.*;

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
     * user注销登录
     * @param id                        user id
     * @throws LogoutFailedException    注销失败异常
     */
    void logout(Integer id) throws LogoutFailedException;

    /**
     * user 注册
     * @param user                      user
     * @throws RegisterFailedException  注册失败异常
     */
    void register(User user) throws RegisterFailedException;

    /**
     * 修改密码
     * @param userId                    userId
     * @param password                  password
     * @param newPassword               newPassword
     * @throws ModifyFailedException    修改失败异常
     */
    void modifyPassword(Integer userId, String password, String newPassword) throws ModifyFailedException;

    /**
     * 修改性别
     * @param userId                    userId
     * @param gender                    gender
     * @throws ModifyFailedException    修改失败异常
     */
    void modifyGender(Integer userId, String gender) throws ModifyFailedException;

    /**
     * 通过id查找（擦除盐值、密码字段）
     * @param id                    id
     * @return                      User
     * @throws FindFailedException  查找失败异常
     */
    User findById(Integer id) throws FindFailedException;

    /**
     * (系统内部)通过id查找(controller不能直接调用，会泄露盐值、token)
     * @param id    id
     * @return      Stadium
     */
    User systemFindById(Integer id) throws FindFailedException;
}
