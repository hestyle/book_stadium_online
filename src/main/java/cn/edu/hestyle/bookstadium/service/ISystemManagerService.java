package cn.edu.hestyle.bookstadium.service;

import cn.edu.hestyle.bookstadium.entity.SystemManager;
import cn.edu.hestyle.bookstadium.service.exception.LoginFailedException;

/**
 * SystemManager service层接口
 * @author hestyle
 */
public interface ISystemManagerService {
    /**
     * 通过username、password登录
     * @param username  username
     * @param password  password
     * @return          manager账号
     */
    SystemManager login(String username, String password) throws LoginFailedException;
}
