package cn.edu.hestyle.bookstadium.service;

import cn.edu.hestyle.bookstadium.entity.StadiumManager;
import cn.edu.hestyle.bookstadium.service.exception.*;

import java.util.HashMap;

/**
 * StadiumManager 业务层接口
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/3 9:55 上午
 */
public interface IStadiumManagerService {
    /**
     * StadiumManager 通过username、password登录
     * @param username                  用户名
     * @param password                  密码
     * @return                          StadiumManager
     * @throws LoginFailedException     登录失败异常
     */
    StadiumManager login(String username, String password) throws LoginFailedException;

    /**
     * StadiumManager账号注册
     * @param stadiumManager            待注册的账号
     * @throws RegisterFailedException  注册失败异常
     */
    void register(StadiumManager stadiumManager) throws RegisterFailedException;

    /**
     * StadiumManager 通过username查找账号信息
     * @param username                  用户名
     * @return                          StadiumManager
     */
    StadiumManager findByUsername(String username) throws AccountNotFoundException;

    /**
     * 通过id查找(controller不能直接调用，会泄露盐值、token)
     * @param id    id
     * @return      Stadium
     */
    StadiumManager findById(Integer id) throws FindFailedException;

    /**
     * 更新StadiumManager账号信息
     * @param username                  用户名
     * @param modifyDataMap             key value
     * @throws ModifyFailedException    更新字段数字非法
     */
    void modifyInfo(String username, HashMap<String, Object> modifyDataMap) throws ModifyFailedException;

    /**
     * 修改StadiumManager账号密码
     * @param username                  用户名
     * @param beforePassword            原密码
     * @param newPassword               新密码
     * @throws ModifyFailedException    更新错误
     */
    void changePassword(String username, String beforePassword, String newPassword) throws ModifyFailedException;
}
