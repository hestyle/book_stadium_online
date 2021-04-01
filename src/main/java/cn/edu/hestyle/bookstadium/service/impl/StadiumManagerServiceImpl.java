package cn.edu.hestyle.bookstadium.service.impl;

import cn.edu.hestyle.bookstadium.entity.StadiumManager;
import cn.edu.hestyle.bookstadium.mapper.StadiumManagerMapper;
import cn.edu.hestyle.bookstadium.service.IStadiumManagerService;
import cn.edu.hestyle.bookstadium.service.exception.*;
import cn.edu.hestyle.bookstadium.util.EncryptUtil;
import cn.edu.hestyle.bookstadium.util.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        String encryptedPassword = EncryptUtil.encryptPassword(password, stadiumManager.getSaltValue());
        if (password != null && encryptedPassword.equals(stadiumManager.getPassword())) {
            // 生成token
            stadiumManager.setToken(TokenUtil.getToken(stadiumManager));
            // 更新数据库中的token
            stadiumManager.setModifiedUser(stadiumManager.getUsername());
            stadiumManager.setModifiedTime(new Date());
            try {
                stadiumManagerMapper.update(stadiumManager);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("Token 数据库更新失败，发生未知错误！stadiumManager = " + stadiumManager);
                throw new LoginFailedException("登录失败，数据库发生未知异常！");
            }
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

    @Override
    public void register(StadiumManager stadiumManager) throws RegisterFailedException {
        if (stadiumManager == null) {
            logger.info("StadiumManager 账号注册失败，未输入账号信息！data = " + stadiumManager);
            throw new RegisterFailedException("账号注册失败，请输入账号信息！");
        }
        // 检查用户名
        if (stadiumManager.getUsername() == null || stadiumManager.getUsername().length() == 0) {
            logger.info("StadiumManager 账号注册失败，未输入用户名！data = " + stadiumManager);
            throw new RegisterFailedException("账号注册失败，未输入用户名！");
        }
        if (stadiumManager.getUsername().length() > 20) {
            logger.info("StadiumManager 账号注册失败，用户名长度超过20个字符！data = " + stadiumManager);
            throw new RegisterFailedException("账号注册失败，用户名长度超过20个字符！");
        }
        // 检查用户名是否已被注册
        StadiumManager account = null;
        try {
            account = stadiumManagerMapper.findByUsername(stadiumManager.getUsername());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("StadiumManager 账号注册失败, 数据库发生未知异常！data = " + stadiumManager);
            throw new RegisterFailedException("账号注册失败，数据库发生未知异常！");
        }
        if (account != null) {
            logger.error("StadiumManager 账号注册失败,  用户名" + stadiumManager.getUsername() + "已被注册！data = " + stadiumManager);
            throw new RegisterFailedException("账号注册失败，用户名" + stadiumManager.getUsername() + "已被注册！");
        }
        // 检查密码
        if (stadiumManager.getPassword() == null || stadiumManager.getPassword().length() == 0) {
            logger.info("StadiumManager 账号注册失败，未输入密码！data = " + stadiumManager);
            throw new RegisterFailedException("账号注册失败，未输入密码！");
        }
        if (stadiumManager.getPassword().length() > 20) {
            logger.info("StadiumManager 账号注册失败，密码长度超过20个字符！data = " + stadiumManager);
            throw new RegisterFailedException("账号注册失败，密码长度超过20个字符！");
        }
        // 检查性别
        if (stadiumManager.getGender() == null || stadiumManager.getGender().length() == 0) {
            logger.info("StadiumManager 账号注册失败，未选择性别！data = " + stadiumManager);
            throw new RegisterFailedException("账号注册失败，未选择性别！");
        }
        if (!stadiumManager.getGender().equals("男") && !stadiumManager.getGender().equals("女")) {
            logger.info("StadiumManager 账号注册失败，性别非法！data = " + stadiumManager);
            throw new RegisterFailedException("账号注册失败，性别非法，只能设置为 男 或 女！");
        }
        // 检查电话号码
        if (stadiumManager.getPhoneNumber() == null || stadiumManager.getPhoneNumber().length() == 0) {
            logger.info("StadiumManager 账号注册失败，未输入电话号码！data = " + stadiumManager);
            throw new RegisterFailedException("账号注册失败，请输入电话号码！");
        }
        Pattern pattern = Pattern.compile("^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0,5-9]))\\d{8}$");
        Matcher matcher = pattern.matcher(stadiumManager.getPhoneNumber());
        if (!matcher.matches()) {
            logger.info("StadiumManager 账号注册失败，电话号码非法！data = " + stadiumManager);
            throw new RegisterFailedException("账号注册失败，输入的电话号码非法！");
        }
        // 补充StadiumManager信息
        String saltValue = UUID.randomUUID().toString().toUpperCase();
        stadiumManager.setSaltValue(saltValue);
        stadiumManager.setPassword(EncryptUtil.encryptPassword(stadiumManager.getPassword(), saltValue));
        stadiumManager.setCreditScore(60);
        stadiumManager.setCreatedUser(stadiumManager.getUsername());
        stadiumManager.setCreatedTime(new Date());
        try {
            stadiumManagerMapper.add(stadiumManager);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("StadiumManager 账号注册失败, 数据库发生未知异常！data = " + stadiumManager);
            throw new RegisterFailedException("账号注册失败，数据库发生未知异常！");
        }
        logger.info("StadiumManager 账号注册成功！data = " + stadiumManager);
    }

    @Override
    public StadiumManager findByUsername(String username) throws AccountNotFoundException {
        // 检查用户名字符串
        if (username == null || username.length() == 0) {
            logger.info("StadiumManager username=" + username + " 账号查找失败，未输入用户名！");
            throw new AccountNotFoundException("账号查找失败，未输入用户名！");
        }
        StadiumManager stadiumManager = null;
        try {
            stadiumManager = stadiumManagerMapper.findByUsername(username);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("StadiumManager username=" + username + ",  账号查找失败，数据库发生未知异常！");
            throw new AccountNotFoundException("账号查找失败，数据库发生未知异常！");
        }
        // 判断username是否注册
        if (stadiumManager == null) {
            logger.info("StadiumManager username=" + username + ", 账号查找失败，用户名 " + username + " 未注册！");
            throw new AccountNotFoundException("账号查找失败，用户名 " + username + " 未注册！");
        } else {
            // 将password、saltValue剔除
            stadiumManager.setPassword(null);
            stadiumManager.setSaltValue(null);
            logger.info("StadiumManager username=" + username + ", 查找账号信息成功！");
            return stadiumManager;
        }
    }

    @Override
    public StadiumManager findById(Integer stadiumManagerId) throws FindFailedException {
        if (stadiumManagerId == null) {
            logger.info("StadiumManager id=" + stadiumManagerId + " 账号查找失败，未输入stadiumManagerId！");
            throw new FindFailedException("账号查找失败，未输入用户id！");
        }
        StadiumManager stadiumManager = null;
        try {
            stadiumManager = stadiumManagerMapper.findById(stadiumManagerId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("StadiumManager id=" + stadiumManagerId + ",  账号查找失败，数据库发生未知异常！");
            throw new FindFailedException("账号查找失败，数据库发生未知异常！");
        }
        // 判断username是否注册
        if (stadiumManager == null) {
            logger.info("StadiumManager id=" + stadiumManagerId + ", 账号查找失败，用户名 id=" + stadiumManagerId + " 未注册！");
            throw new FindFailedException("账号查找失败，用户名 id=" + stadiumManagerId + " 未注册！");
        } else {
            // 将password、saltValue剔除
            stadiumManager.setPassword(null);
            stadiumManager.setSaltValue(null);
            logger.info("StadiumManager id=" + stadiumManagerId + ", 查找账号信息成功！stadiumManager=" + stadiumManager);
            return stadiumManager;
        }
    }

    @Override
    public StadiumManager systemFindById(Integer id) throws FindFailedException {
        if (id == null) {
            logger.warn("StadiumManager 查询失败，StadiumManager ID！");
            throw new FindFailedException("查询失败，未指定需要查询的StadiumManager ID！");
        }
        StadiumManager stadiumManager = null;
        try {
            stadiumManager = stadiumManagerMapper.findById(id);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumManager 查询失败，数据库发生未知异常！id = " + id);
            throw new FindFailedException("查询失败，数据库发生未知异常！");
        }
        logger.warn("StadiumManager 查询成功！stadiumManager = " + stadiumManager);
        return stadiumManager;
    }

    @Override
    public void modifyInfo(Integer stadiumManagerId, HashMap<String, Object> modifyDataMap) throws ModifyFailedException {
        StadiumManager stadiumManager = null;
        try {
            stadiumManager = stadiumManagerMapper.findById(stadiumManagerId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("StadiumManager id=" + stadiumManagerId + ",  账号查找失败，数据库发生未知异常！");
            throw new ModifyFailedException("更新保存失败，数据库发生未知异常！");
        }
        if (modifyDataMap.containsKey("gender")) {
            String gender = (String)modifyDataMap.get("gender");
            if (!"男".equals(gender) && !"女".equals(gender) && !"保密".equals(gender)) {
                logger.error("StadiumManager 账号更新保存失败，性别非法！data = " + modifyDataMap);
                throw new ModifyFailedException("更新保存失败，性别非法！");
            } else {
                stadiumManager.setGender(gender);
            }
        }
        if (modifyDataMap.containsKey("address")) {
            String address = (String)modifyDataMap.get("address");
            if (address != null) {
                stadiumManager.setAddress(address);
            }
        }
        if (modifyDataMap.containsKey("phoneNumber")) {
            String phoneNumber = (String)modifyDataMap.get("phoneNumber");
            Pattern pattern = Pattern.compile("^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0,5-9]))\\d{8}$");
            Matcher matcher = pattern.matcher(phoneNumber);
            if (!matcher.matches()) {
                logger.info("StadiumManager 账号更改保存失败，电话号码非法！data = " + modifyDataMap);
                throw new ModifyFailedException("账号修改保存失败，输入的电话号码非法！");
            } else {
                stadiumManager.setPhoneNumber(phoneNumber);
            }
        }
        if (modifyDataMap.containsKey("avatarPath")) {
            String avatarPath = (String) modifyDataMap.get("avatarPath");
            try {
                checkAvatarPaths(avatarPath);
            } catch (Exception e) {
                e.printStackTrace();
                logger.info("StadiumManager 账号更改保存失败，图片路径非法！data = " + avatarPath);
                throw new ModifyFailedException("账号修改保存失败，图片路径不在本服务器上！");
            }
            stadiumManager.setAvatarPath(avatarPath);
        }
        stadiumManager.setModifiedUser(stadiumManager.getUsername());
        stadiumManager.setModifiedTime(new Date());
        try {
            stadiumManagerMapper.update(stadiumManager);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("StadiumManager,  账号更新保存失败，数据库发生未知异常！");
            throw new ModifyFailedException("更新保存失败，数据库发生未知异常！");
        }
        logger.info("StadiumManager 账号更改保存成功！data = " + modifyDataMap);
    }

    @Override
    public void changePassword(Integer stadiumManagerId, String beforePassword, String newPassword) throws ModifyFailedException {
        StadiumManager stadiumManager = null;
        try {
            stadiumManager = stadiumManagerMapper.findById(stadiumManagerId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("StadiumManager id=" + stadiumManagerId + ",  账号查找失败，数据库发生未知异常！");
            throw new ModifyFailedException("更新保存失败，数据库发生未知异常！");
        }
        // 检查原密码是否正确
        if (!stadiumManager.getPassword().equals(EncryptUtil.encryptPassword(beforePassword, stadiumManager.getSaltValue()))) {
            logger.info("StadiumManager 密码更新失败，原密码错误！beforePassword = " + beforePassword);
            throw new ModifyFailedException("密码更新失败，原密码错误！");
        }
        // 检查新密码是否合法
        if (newPassword == null || newPassword.length() == 0) {
            logger.info("StadiumManager 密码更新失败，未输入新密码！");
            throw new ModifyFailedException("密码更新失败，请输入新密码！");
        }
        if (newPassword.length() > 20) {
            logger.info("StadiumManager 密码更新失败，新密码超过20个字符！newPassword = " + newPassword);
            throw new ModifyFailedException("密码更新失败，新密码超过了20个字符！");
        }
        stadiumManager.setPassword(EncryptUtil.encryptPassword(newPassword, stadiumManager.getSaltValue()));
        stadiumManager.setModifiedUser(stadiumManager.getUsername());
        stadiumManager.setModifiedTime(new Date());
        try {
            stadiumManagerMapper.update(stadiumManager);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("StadiumManager, 密码更新保存失败，数据库发生未知异常！");
            throw new ModifyFailedException("密码更新保存失败，数据库发生未知异常！");
        }
        logger.info("StadiumManager 账号密码更改保存成功！data = " + stadiumManager);
    }

    @Override
    public List<StadiumManager> systemManagerFindByPage(Integer pageIndex, Integer pageSize) {
        // 检查页码是否合法
        if (pageIndex < 1) {
            throw new FindFailedException("查询失败，页码 " + pageIndex + " 非法，必须大于0！");
        }
        // 检查页大小是否合法
        if (pageSize < 1) {
            throw new FindFailedException("查询失败，页大小 " + pageSize + " 非法，必须大于0！");
        }
        List<StadiumManager> stadiumManagerList = null;
        try {
            stadiumManagerList = stadiumManagerMapper.findByPage((pageIndex - 1) * pageSize, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("User 查询失败，数据库发生未知异常！");
            throw new FindFailedException("操作失败，数据库发生未知异常！");
        }
        return stadiumManagerList;
    }

    @Override
    public Integer getCount() {
        Integer count = null;
        try {
            count = stadiumManagerMapper.getCount();
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("User 查询失败，数据库发生未知异常！");
            throw new FindFailedException("操作失败，数据库发生未知异常！");
        }
        return count;
    }

    /**
     * 检查imagePaths的合法性
     * @param avatarPath            avatarPath
     * @return                      是否合法
     * @throws Exception            image path异常
     */
    private boolean checkAvatarPaths(String avatarPath) throws Exception {
        if (avatarPath == null || avatarPath.length() == 0) {
            return true;
        }
        try {
            String pathNameTemp = ResourceUtils.getURL("classpath:").getPath() + "static/upload/image/stadiumManager/avatar";
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
        return false;
    }
}
