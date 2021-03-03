package cn.edu.hestyle.bookstadium.service.impl;

import cn.edu.hestyle.bookstadium.entity.StadiumManager;
import cn.edu.hestyle.bookstadium.mapper.StadiumManagerMapper;
import cn.edu.hestyle.bookstadium.service.IStadiumManagerService;
import cn.edu.hestyle.bookstadium.service.exception.LoginFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;

/**
 * StadiumManagerService 接口实现类
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/3 9:57 上午
 */
@Service
public class StadiumManagerServiceImpl implements IStadiumManagerService {

    private static final Logger logger = LoggerFactory.getLogger(StadiumManagerServiceImpl.class);

    @Resource
    private StadiumManagerMapper stadiumManagerMapper;

    @Override
    public StadiumManager login(String username, String password) throws LoginFailedException {
        // 检查用户名字符串
        if (username == null || username.length() == 0) {
            logger.info("StadiumManager username=" + username + ", password=" + password + " 登录失败，未输入用户名！");
            throw new LoginFailedException("登录失败，未输入用户名！");
        }
        StadiumManager stadiumManager = null;
        try {
            stadiumManager = stadiumManagerMapper.findByUsername(username);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("StadiumManager username=" + username + ", password=" + password + " 登录失败，数据库发生未知异常！");
            throw new LoginFailedException("登录失败，数据库发生未知异常！");
        }
        // 判断username是否注册
        if (stadiumManager == null) {
            logger.info("StadiumManager username=" + username + ", password=" + password + " 登录失败，用户名 " + username + " 未注册！");
            throw new LoginFailedException("登录失败，用户名 " + username + " 未注册！");
        }
        // 判断密码是否匹配
        String encryptedPassword = StadiumManagerServiceImpl.encryptPassword(password, stadiumManager.getSaltValue());
        if (password != null && encryptedPassword.equals(stadiumManager.getPassword())) {
            // 将password、saltValue剔除
            stadiumManager.setPassword(null);
            stadiumManager.setSaltValue(null);
            logger.info("StadiumManager username=" + username + ", password=" + password + " 登录成功！");
            return stadiumManager;
        } else {
            logger.info("StadiumManager username=" + username + ", password=" + password + " 登录失败，密码错误！");
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
