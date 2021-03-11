package cn.edu.hestyle.bookstadium.service.impl;

import cn.edu.hestyle.bookstadium.entity.SystemManager;
import cn.edu.hestyle.bookstadium.mapper.SystemManagerMapper;
import cn.edu.hestyle.bookstadium.service.ISystemManagerService;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;
import cn.edu.hestyle.bookstadium.service.exception.LoginFailedException;
import cn.edu.hestyle.bookstadium.service.exception.ModifyFailedException;
import cn.edu.hestyle.bookstadium.util.EncryptUtil;
import cn.edu.hestyle.bookstadium.util.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.annotation.Resource;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        String encryptedPassword = EncryptUtil.encryptPassword(password, systemManager.getSaltValue());
        if (password != null && encryptedPassword.equals(systemManager.getPassword())) {
            // 生成token
            systemManager.setToken(TokenUtil.getToken(systemManager));
            // 更新数据库中的token
            try {
                systemManagerMapper.update(systemManager);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("Token 数据库更新失败，发生未知错误！systemManager = " + systemManager);
                throw new LoginFailedException("登录失败，数据库发生未知异常！");
            }
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

    @Override
    public SystemManager findById(Integer id) throws FindFailedException {
        if (id == null) {
            logger.warn("SystemManager 查询失败，未指定SystemManager ID！");
            throw new FindFailedException("查询失败，未指定需要查询的SystemManager ID！");
        }
        SystemManager systemManager = null;
        try {
            systemManager = systemManagerMapper.findById(id);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SystemManager 查询失败，数据库发生未知异常！id = " + id);
            throw new FindFailedException("查询失败，数据库发生未知异常！");
        }
        if (systemManager == null) {
            logger.warn("SystemManager 查询失败！不存在 id = " + id + " SystemManager账号！");
            throw new FindFailedException("查询失败，id = " + id + " SystemManager账号不存在！");
        }
        systemManager.setPassword(null);
        systemManager.setSaltValue(null);
        logger.warn("SystemManager 查询成功！systemManager = " + systemManager);
        return systemManager;
    }

    @Override
    public SystemManager systemFindById(Integer id) throws FindFailedException {
        if (id == null) {
            logger.warn("SystemManager 查询失败，未指定SystemManager ID！");
            throw new FindFailedException("查询失败，未指定需要查询的SystemManager ID！");
        }
        SystemManager systemManager = null;
        try {
            systemManager = systemManagerMapper.findById(id);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SystemManager 查询失败，数据库发生未知异常！id = " + id);
            throw new FindFailedException("查询失败，数据库发生未知异常！");
        }
        logger.warn("SystemManager 查询成功！systemManager = " + systemManager);
        return systemManager;
    }

    @Override
    public void modify(Integer systemManagerId, SystemManager systemManager) throws ModifyFailedException {
        SystemManager modifySystemManager = null;
        try {
            modifySystemManager = systemManagerMapper.findById(systemManagerId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SystemManager 查询失败，数据库发生未知异常！systemManager = " + systemManager);
            throw new ModifyFailedException("修改失败，数据库发生未知异常！");
        }
        // 检查修改的字段合法性
        if (systemManager.getAvatarPath() != null) {
            try {
                this.checkAvatarPath(systemManager.getAvatarPath());
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("SystemManager 修改失败，账号更改保存失败，图片路径非法！systemManager = " + systemManager);
                throw new ModifyFailedException("修改失败，图片路径非法！");
            }
            modifySystemManager.setAvatarPath(systemManager.getAvatarPath());
        }
        if (systemManager.getPhoneNumber() != null) {
            Pattern pattern = Pattern.compile("^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0,5-9]))\\d{8}$");
            Matcher matcher = pattern.matcher(systemManager.getPhoneNumber());
            if (!matcher.matches()) {
                logger.info("StadiumManager 账号更改保存失败，电话号码非法！systemManager = " + systemManager);
                throw new ModifyFailedException("账号修改保存失败，输入的电话号码非法！");
            }
            modifySystemManager.setPhoneNumber(systemManager.getPhoneNumber());
        }
        try {
            systemManagerMapper.update(modifySystemManager);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SystemManager 修改失败，数据库发生未知异常！modifySystemManager = " + modifySystemManager);
            throw new ModifyFailedException("修改失败，数据库发生未知异常！");
        }
        logger.warn("SystemManager 修改成功！modifySystemManager = " + modifySystemManager);
    }

    /**
     * 检查imagePaths的合法性
     * @param avatarPath            avatarPath
     * @return                      是否合法
     * @throws Exception            image path异常
     */
    private boolean checkAvatarPath(String avatarPath) throws Exception {
        if (avatarPath == null || avatarPath.length() == 0) {
            return true;
        }
        try {
            String pathNameTemp = ResourceUtils.getURL("classpath:").getPath() + "static/upload/image/systemManager/avatar";
            String pathNameTruth = pathNameTemp.replace("target", "src").replace("classes", "main/resources");
            String filePath = pathNameTruth + avatarPath.substring(avatarPath.lastIndexOf('/'));
            File file = new File(filePath);
            if (!file.exists()) {
                throw new Exception(avatarPath + "文件不存在！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(avatarPath + "文件不存在！");
        }
        logger.info("StadiumManager avatarPath = " + avatarPath + " 通过检查！");
        return true;
    }
}
