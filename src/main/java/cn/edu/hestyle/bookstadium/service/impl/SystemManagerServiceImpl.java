package cn.edu.hestyle.bookstadium.service.impl;

import cn.edu.hestyle.bookstadium.entity.SystemManager;
import cn.edu.hestyle.bookstadium.mapper.SystemManagerMapper;
import cn.edu.hestyle.bookstadium.service.ISystemManagerService;
import cn.edu.hestyle.bookstadium.service.exception.LoginFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;

/**
 * SystemManager service层接口实现类
 * @author hestyle
 */
@Service
public class SystemManagerServiceImpl implements ISystemManagerService {

    private static final Logger logger = LoggerFactory.getLogger(SystemManagerServiceImpl.class);

    @Resource
    private SystemManagerMapper systemManagerMapper;

    @Override
    public SystemManager login(String username, String password) throws LoginFailedException {
        // 检查用户名字符串
        if (username == null || username.length() == 0) {
            logger.info("SystemManager username=" + username + ", password=" + password + " 登录失败，未输入用户名！");
            throw new LoginFailedException("登录失败，未输入用户名！");
        }
        SystemManager systemManager = null;
        try {
            systemManager = systemManagerMapper.findByUsername(username);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("SystemManager username=" + username + ", password=" + password + " 登录失败，数据库发生未知异常！");
            throw new LoginFailedException("登录失败，数据库发生未知异常！");
        }
        // 判断username是否注册
        if (systemManager == null) {
            logger.info("SystemManager username=" + username + ", password=" + password + " 登录失败，用户名 " + username + " 未注册！");
            throw new LoginFailedException("登录失败，用户名 " + username + " 未注册！");
        }
        // 判断密码是否匹配
        String encryptedPassword = SystemManagerServiceImpl.encryptPassword(password, systemManager.getSaltValue());
        if (password != null && encryptedPassword.equals(systemManager.getPassword())) {
            // 将password、saltValue剔除
            systemManager.setPassword(null);
            systemManager.setSaltValue(null);
            logger.info("SystemManager username=" + username + ", password=" + password + " 登录成功！");
            return systemManager;
        } else {
            logger.info("SystemManager username=" + username + ", password=" + password + " 登录失败，密码错误！");
            throw new LoginFailedException("登录失败，密码错误！");
        }
    }

    /**
     * 对原始密码和盐值执行MD5加密
     * @param srcPassword 原始密码
     * @param saltValue 盐值
     * @return 加密后的密码
     */
    private static String encryptPassword(String srcPassword, String saltValue) {
        String src = saltValue + srcPassword + saltValue;
        for (int i = 0; i < 10 ; i++) {
            src = DigestUtils.md5DigestAsHex(src.getBytes()).toUpperCase();
        }
        return src;
    }
}
