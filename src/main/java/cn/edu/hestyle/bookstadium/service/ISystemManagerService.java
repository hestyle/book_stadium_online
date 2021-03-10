package cn.edu.hestyle.bookstadium.service;

import cn.edu.hestyle.bookstadium.entity.SystemManager;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;
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

    /**
     * (系统内部)通过id查找(controller不能直接调用，会泄露盐值、token)
     * @param id    id
     * @return      Stadium
     */
    SystemManager systemFindById(Integer id) throws FindFailedException;
}
