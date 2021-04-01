package cn.edu.hestyle.bookstadium.service.impl;

import cn.edu.hestyle.bookstadium.entity.StadiumManager;
import cn.edu.hestyle.bookstadium.entity.SystemManager;
import cn.edu.hestyle.bookstadium.mapper.StadiumManagerMapper;
import cn.edu.hestyle.bookstadium.mapper.SystemManagerMapper;
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
    /** 密码的最小长度 */
    private static final Integer STADIUM_MANAGER_PASSWORD_MIN_LENGTH = 5;
    /** 密码的最大长度 */
    private static final Integer STADIUM_MANAGER_PASSWORD_MAX_LENGTH = 20;
    /** 性别：男 */
    private static final String STADIUM_MANAGER_GENDER_MAN = "男";
    /** 性别：女 */
    private static final String STADIUM_MANAGER_GENDER_WOMAN = "女";

    private static final Logger logger = LoggerFactory.getLogger(StadiumManagerServiceImpl.class);

    @Resource
    private SystemManagerMapper systemManagerMapper;
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
    public List<StadiumManager> systemManagerFindByPage(Integer pageIndex, Integer pageSize, String usernameKey) {
        // 检查页码是否合法
        if (pageIndex < 1) {
            throw new FindFailedException("查询失败，页码 " + pageIndex + " 非法，必须大于0！");
        }
        // 检查页大小是否合法
        if (pageSize < 1) {
            throw new FindFailedException("查询失败，页大小 " + pageSize + " 非法，必须大于0！");
        }
        // 去除usernameKey中的特殊字符
        if (usernameKey != null && usernameKey.length() != 0) {
            usernameKey = usernameKey.replaceAll("%", "").replaceAll("'", "").replaceAll("\\?", "");
        }
        List<StadiumManager> stadiumManagerList = null;
        try {
            stadiumManagerList = stadiumManagerMapper.findByPage((pageIndex - 1) * pageSize, pageSize, usernameKey);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("User 查询失败，数据库发生未知异常！");
            throw new FindFailedException("操作失败，数据库发生未知异常！");
        }
        return stadiumManagerList;
    }

    @Override
    public Integer getCount(String usernameKey) {
        // 去除usernameKey中的特殊字符
        if (usernameKey != null && usernameKey.length() != 0) {
            usernameKey = usernameKey.replaceAll("%", "").replaceAll("'", "").replaceAll("\\?", "");
        }
        Integer count = null;
        try {
            count = stadiumManagerMapper.getCount(usernameKey);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("User 查询失败，数据库发生未知异常！");
            throw new FindFailedException("操作失败，数据库发生未知异常！");
        }
        return count;
    }

    @Override
    public void systemManagerModify(Integer systemManagerId, StadiumManager stadiumManager) {
        SystemManager systemManager = null;
        try {
            systemManager = systemManagerMapper.findById(systemManagerId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SystemManager 查询失败，数据库发生未知异常！");
            throw new FindFailedException("操作失败，数据库发生未知异常！");
        }
        if (stadiumManager == null || stadiumManager.getId() == null) {
            logger.warn("StadiumManager 修改失败，未指定需要修改的User！");
            throw new ModifyFailedException("操作失败，未指定需要修改的StadiumManager！");
        }
        StadiumManager stadiumManagerModify = null;
        try {
            stadiumManagerModify = stadiumManagerMapper.findById(stadiumManager.getId());
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumManager 查询失败，数据库发生未知异常！");
            throw new FindFailedException("操作失败，数据库发生未知异常！");
        }
        if (stadiumManagerModify == null) {
            logger.warn("StadiumManager 修改失败，修改的StadiumManager不存在！stadiumManager = " + stadiumManager);
            throw new ModifyFailedException("操作失败，该用户不存在！");
        }
        String gender = stadiumManager.getGender();
        if (gender == null || (!STADIUM_MANAGER_GENDER_MAN.equals(gender) && !STADIUM_MANAGER_GENDER_WOMAN.equals(gender))) {
            logger.info("StadiumManager 修改失败，性别非法！stadiumManager = " + stadiumManager);
            throw new ModifyFailedException("修改失败，性别非法！");
        }
        stadiumManagerModify.setGender(gender);
        stadiumManagerModify.setAddress(stadiumManager.getAddress());
        String phoneNumber = stadiumManager.getPhoneNumber();
        if (phoneNumber != null) {
            Pattern pattern = Pattern.compile("^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0,5-9]))\\d{8}$");
            Matcher matcher = pattern.matcher(phoneNumber);
            if (!matcher.matches()) {
                logger.info("StadiumManager 修改失败，电话号码非法！stadiumManager = " + stadiumManager);
                throw new ModifyFailedException("修改失败，输入的电话号码非法！");
            }
        }
        stadiumManagerModify.setPhoneNumber(stadiumManager.getPhoneNumber());
        stadiumManagerModify.setModifiedUser(systemManager.getUsername());
        stadiumManagerModify.setModifiedTime(new Date());
        try {
            stadiumManagerMapper.update(stadiumManagerModify);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumManager 修改失败，数据库发生未知异常！stadiumManagerModify = " + stadiumManagerModify);
            throw new ModifyFailedException("操作失败，数据库发生未知异常！");
        }
        logger.warn("StadiumManager 修改成功！stadiumManagerModify = " + stadiumManagerModify);
    }

    @Override
    public void systemManagerResetPassword(Integer systemManagerId, Integer stadiumManagerId, String newPassword) {
        SystemManager systemManager = null;
        try {
            systemManager = systemManagerMapper.findById(systemManagerId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SystemManager 查询失败，数据库发生未知异常！");
            throw new FindFailedException("操作失败，数据库发生未知异常！");
        }
        if (stadiumManagerId == null) {
            logger.warn("SystemManager 密码重置失败，未传入stadiumManagerId参数！");
            throw new ModifyFailedException("操作失败，未指定需要重置密码的stadiumManager账号！");
        }
        StadiumManager stadiumManagerModify = null;
        try {
            stadiumManagerModify = stadiumManagerMapper.findById(stadiumManagerId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SystemManager 查询失败，数据库发生未知异常！");
            throw new FindFailedException("操作失败，数据库发生未知异常！");
        }
        if (stadiumManagerModify == null) {
            logger.warn("SystemManager 密码重置失败，该stadiumManager不存在！");
            throw new FindFailedException("操作失败，不存在这个stadiumManager账号！");
        }
        if (newPassword == null || newPassword.length() < STADIUM_MANAGER_PASSWORD_MIN_LENGTH || newPassword.length() > STADIUM_MANAGER_PASSWORD_MAX_LENGTH) {
            logger.warn("SystemManager 密码重置失败，新密码长度无效！不在[" + STADIUM_MANAGER_PASSWORD_MIN_LENGTH + ", " + STADIUM_MANAGER_PASSWORD_MAX_LENGTH + "]区间！newPassword = " + newPassword);
            throw new FindFailedException("操作失败，新密码长度无效！长度不在[" + STADIUM_MANAGER_PASSWORD_MIN_LENGTH + ", " + STADIUM_MANAGER_PASSWORD_MAX_LENGTH + "]区间！");
        }
        stadiumManagerModify.setPassword(EncryptUtil.encryptPassword(newPassword, stadiumManagerModify.getSaltValue()));
        stadiumManagerModify.setModifiedUser(systemManager.getUsername());
        stadiumManagerModify.setModifiedTime(new Date());
        try {
            stadiumManagerMapper.update(stadiumManagerModify);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SystemManager 更新失败，数据库发生未知异常！stadiumManagerModify = " + stadiumManagerModify);
            throw new FindFailedException("操作失败，数据库发生未知异常！");
        }
        logger.warn("SystemManager 更新成功！stadiumManagerModify = " + stadiumManagerModify);
    }

    @Override
    public void systemManagerAddToBlack(Integer systemManagerId, Integer stadiumManagerId) {
        SystemManager systemManager = null;
        try {
            systemManager = systemManagerMapper.findById(systemManagerId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SystemManager 查询失败，数据库发生未知异常！");
            throw new FindFailedException("操作失败，数据库发生未知异常！");
        }
        if (stadiumManagerId == null) {
            logger.warn("StadiumManager 拉黑失败，未传入stadiumManagerId参数！");
            throw new ModifyFailedException("操作失败，未指定需要拉黑的stadiumManager账号！");
        }
        StadiumManager stadiumManagerModify = null;
        try {
            stadiumManagerModify = stadiumManagerMapper.findById(stadiumManagerId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumManager 查询失败，数据库发生未知异常！");
            throw new FindFailedException("操作失败，数据库发生未知异常！");
        }
        if (stadiumManagerModify == null) {
            logger.warn("StadiumManager 拉黑失败，该stadiumManager不存在！");
            throw new FindFailedException("操作失败，不存在这个用户！");
        }
        if (stadiumManagerModify.getIsDelete() != null && stadiumManagerModify.getIsDelete().equals(2)) {
            logger.warn("StadiumManager 拉黑失败，该stadiumManager已处于黑名单状态！stadiumManagerModify = " + stadiumManagerModify);
            throw new FindFailedException("操作失败，该stadiumManager已处于黑名单状态！");
        }
        // 拉黑并清除token
        stadiumManagerModify.setIsDelete(2);
        stadiumManagerModify.setModifiedUser(systemManager.getUsername());
        stadiumManagerModify.setModifiedTime(new Date());
        stadiumManagerModify.setToken(null);
        try {
            stadiumManagerMapper.update(stadiumManagerModify);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumManager 更新失败，数据库发生未知异常！stadiumManagerModify = " + stadiumManagerModify);
            throw new FindFailedException("操作失败，数据库发生未知异常！");
        }
        logger.warn("StadiumManager 更新成功！stadiumManagerModify = " + stadiumManagerModify);
    }

    @Override
    public void systemManagerRemoveFromBlack(Integer systemManagerId, Integer stadiumManagerId) {
        SystemManager systemManager = null;
        try {
            systemManager = systemManagerMapper.findById(systemManagerId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SystemManager 查询失败，数据库发生未知异常！");
            throw new FindFailedException("操作失败，数据库发生未知异常！");
        }
        if (stadiumManagerId == null) {
            logger.warn("StadiumManager 解除拉黑失败，未传入stadiumManagerId参数！");
            throw new ModifyFailedException("操作失败，未指定需要解除拉黑的stadiumManager账号！");
        }
        StadiumManager stadiumManagerModify = null;
        try {
            stadiumManagerModify = stadiumManagerMapper.findById(stadiumManagerId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumManager 查询失败，数据库发生未知异常！");
            throw new FindFailedException("操作失败，数据库发生未知异常！");
        }
        if (stadiumManagerModify == null) {
            logger.warn("StadiumManager 解除拉黑失败，该stadiumManager不存在！");
            throw new FindFailedException("操作失败，不存在这个用户！");
        }
        if (stadiumManagerModify.getIsDelete() == null || !stadiumManagerModify.getIsDelete().equals(2)) {
            logger.warn("StadiumManager 解除拉黑失败，该stadiumManager并未处于黑名单状态！stadiumManagerModify = " + stadiumManagerModify);
            throw new FindFailedException("操作失败，该stadiumManager并未处于黑名单状态！");
        }
        // 解除拉黑并清除token
        stadiumManagerModify.setIsDelete(0);
        stadiumManagerModify.setModifiedUser(systemManager.getUsername());
        stadiumManagerModify.setModifiedTime(new Date());
        stadiumManagerModify.setToken(null);
        try {
            stadiumManagerMapper.update(stadiumManagerModify);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumManager 更新失败，数据库发生未知异常！stadiumManagerModify = " + stadiumManagerModify);
            throw new FindFailedException("操作失败，数据库发生未知异常！");
        }
        logger.warn("StadiumManager 更新成功！stadiumManagerModify = " + stadiumManagerModify);
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
