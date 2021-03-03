package cn.edu.hestyle.bookstadium.service;

import cn.edu.hestyle.bookstadium.entity.StadiumManager;
import cn.edu.hestyle.bookstadium.service.exception.AccountNotFoundException;
import cn.edu.hestyle.bookstadium.service.exception.LoginFailedException;

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
     * StadiumManager 通过username查找账号信息
     * @param username                  用户名
     * @return                          StadiumManager
     */
    StadiumManager findByUsername(String username) throws AccountNotFoundException;
}
