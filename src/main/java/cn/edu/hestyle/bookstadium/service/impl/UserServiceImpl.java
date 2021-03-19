package cn.edu.hestyle.bookstadium.service.impl;

import cn.edu.hestyle.bookstadium.entity.User;
import cn.edu.hestyle.bookstadium.mapper.UserMapper;
import cn.edu.hestyle.bookstadium.service.IUserService;
import cn.edu.hestyle.bookstadium.service.exception.*;
import cn.edu.hestyle.bookstadium.util.EncryptUtil;
import cn.edu.hestyle.bookstadium.util.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/9 12:46 下午
 */
@Service
public class UserServiceImpl implements IUserService {
    /** 密码的最大长度 */
    private static final Integer USER_PASSWORD_MAX_LENGTH = 20;
    /** 性别：男 */
    private static final String USER_GENDER_MAN = "男";
    /** 性别：女 */
    private static final String USER_GENDER_WOMAN = "女";
    /** 性别：保密 */
    private static final String USER_GENDER_SECRECY = "保密";
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Resource
    private UserMapper userMapper;

    @Override
    public User login(String username, String password) throws LoginFailedException {
        // 检查用户名字符串
        if (username == null || username.length() == 0) {
            logger.info("User username=" + username + ", password=" + password + " 登录失败，未输入用户名！");
            throw new LoginFailedException("登录失败，未输入用户名！");
        }
        User user = null;
        try {
            user = userMapper.findByUsername(username);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("User username=" + username + ", password=" + password + " 登录失败，数据库发生未知异常！");
            throw new LoginFailedException("登录失败，数据库发生未知异常！");
        }
        // 判断username是否注册
        if (user == null) {
            logger.info("User username=" + username + ", password=" + password + " 登录失败，用户名 " + username + " 未注册！");
            throw new LoginFailedException("登录失败，用户名 " + username + " 未注册！");
        }
        // 判断密码是否匹配
        String encryptedPassword = EncryptUtil.encryptPassword(password, user.getSaltValue());
        if (password != null && encryptedPassword.equals(user.getPassword())) {
            // 生成token
            user.setToken(TokenUtil.getToken(user));
            // 更新数据库中的token
            user.setModifiedUser(user.getUsername());
            user.setModifiedTime(new Date());
            try {
                userMapper.update(user);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("Token 数据库更新失败，发生未知错误！user = " + user);
                throw new LoginFailedException("登录失败，数据库发生未知异常！");
            }
            // 将password、saltValue剔除
            user.setPassword(null);
            user.setSaltValue(null);
            logger.info("User username=" + username + ", password=" + password + " 登录成功！");
            return user;
        } else {
            logger.info("User username=" + username + ", password=" + password + " 登录失败，密码错误！");
            throw new LoginFailedException("登录失败，密码错误！");
        }
    }

    @Override
    public void logout(Integer id) throws LogoutFailedException {
        if (id == null) {
            logger.warn("User 注销失败，未指定用户ID");
            throw new LogoutFailedException("User 注销失败，未指定需要注销的用户ID");
        }
        User user = null;
        try {
            user = userMapper.findById(id);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("User 查询失败，数据库发生未知异常！userId = " + id);
            throw new LogoutFailedException("User 注销失败，数据库发生未知异常！");
        }
        user.setToken(null);
        user.setModifiedUser(user.getUsername());
        user.setModifiedTime(new Date());
        try {
            userMapper.update(user);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("User 更新失败，数据库发生未知异常！user = " + user);
            throw new LogoutFailedException("User 注销失败，数据库发生未知异常！");
        }
        logger.warn("User 注销成功！user = " + user);
    }

    @Override
    public void register(User user) throws RegisterFailedException {
        if (user == null) {
            logger.info("User 账号注册失败，未输入账号信息！data = " + user);
            throw new RegisterFailedException("账号注册失败，请输入账号信息！");
        }
        // 检查用户名
        if (user.getUsername() == null || user.getUsername().length() == 0) {
            logger.info("User 账号注册失败，未输入用户名！data = " + user);
            throw new RegisterFailedException("账号注册失败，未输入用户名！");
        }
        if (user.getUsername().length() > 20) {
            logger.info("User 账号注册失败，用户名长度超过20个字符！data = " + user);
            throw new RegisterFailedException("账号注册失败，用户名长度超过20个字符！");
        }
        // 检查用户名是否已被注册
        User account = null;
        try {
            account = userMapper.findByUsername(user.getUsername());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("User 账号注册失败, 数据库发生未知异常！data = " + user);
            throw new RegisterFailedException("账号注册失败，数据库发生未知异常！");
        }
        if (account != null) {
            logger.error("User 账号注册失败,  用户名" + user.getUsername() + "已被注册！data = " + user);
            throw new RegisterFailedException("账号注册失败，用户名" + user.getUsername() + "已被注册！");
        }
        // 检查密码
        if (user.getPassword() == null || user.getPassword().length() == 0) {
            logger.info("User 账号注册失败，未输入密码！data = " + user);
            throw new RegisterFailedException("账号注册失败，未输入密码！");
        }
        if (user.getPassword().length() > 20) {
            logger.info("User 账号注册失败，密码长度超过20个字符！data = " + user);
            throw new RegisterFailedException("账号注册失败，密码长度超过20个字符！");
        }
        // 检查性别
        if (user.getGender() == null || user.getGender().length() == 0) {
            logger.info("User 账号注册失败，未选择性别！data = " + user);
            throw new RegisterFailedException("账号注册失败，未选择性别！");
        }
        if (!user.getGender().equals("男") && !user.getGender().equals("女")) {
            logger.info("User 账号注册失败，性别非法！data = " + user);
            throw new RegisterFailedException("账号注册失败，性别非法，只能设置为 男 或 女！");
        }
        // 检查电话号码
        if (user.getPhoneNumber() == null || user.getPhoneNumber().length() == 0) {
            logger.info("User 账号注册失败，未输入电话号码！data = " + user);
            throw new RegisterFailedException("账号注册失败，请输入电话号码！");
        }
        Pattern pattern = Pattern.compile("^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0,5-9]))\\d{8}$");
        Matcher matcher = pattern.matcher(user.getPhoneNumber());
        if (!matcher.matches()) {
            logger.info("User 账号注册失败，电话号码非法！data = " + user);
            throw new RegisterFailedException("账号注册失败，输入的电话号码非法！");
        }
        // 补充User信息
        String saltValue = UUID.randomUUID().toString().toUpperCase();
        user.setSaltValue(saltValue);
        user.setPassword(EncryptUtil.encryptPassword(user.getPassword(), saltValue));
        user.setCreditScore(60);
        user.setCreatedUser(user.getUsername());
        user.setCreatedTime(new Date());
        try {
            userMapper.add(user);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("User 账号注册失败, 数据库发生未知异常！data = " + user);
            throw new RegisterFailedException("账号注册失败，数据库发生未知异常！");
        }
        logger.info("User 账号注册成功！data = " + user);
    }

    @Override
    public void modifyPassword(Integer userId, String password, String newPassword) throws ModifyFailedException {
        User user = null;
        try {
            user = userMapper.findById(userId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("User 查询失败，数据库发生未知异常！userId = " + userId);
            throw new ModifyFailedException("修改失败，数据库发生未知异常！");
        }
        if (user == null) {
            logger.warn("User 不存在该user！userId = " + userId);
            throw new ModifyFailedException("修改失败，账号未注册！");
        }
        if (password == null || password.length() == 0) {
            logger.warn("User 密码修改失败，原密码错误！user = " + user);
            throw new ModifyFailedException("修改失败，原密码错误！");
        }
        String passwordEncrypt = EncryptUtil.encryptPassword(password, user.getSaltValue());
        if (!user.getPassword().equals(passwordEncrypt)) {
            logger.warn("User 密码修改失败，原密码错误！user = " + user);
            throw new ModifyFailedException("修改失败，原密码错误！");
        }
        if (newPassword == null || newPassword.length() == 0) {
            logger.warn("User 密码修改失败，未设置新密码！user = " + user);
            throw new ModifyFailedException("修改失败，未设置新密码！");
        }
        if (newPassword.length() > USER_PASSWORD_MAX_LENGTH) {
            logger.warn("User 密码修改失败，新密码长度超过了 " + USER_PASSWORD_MAX_LENGTH + " 个字符！user = " + user);
            throw new ModifyFailedException("修改失败，新密码长度超过了 " + USER_PASSWORD_MAX_LENGTH + " 个字符");
        }
        user.setPassword(EncryptUtil.encryptPassword(newPassword, user.getSaltValue()));
        user.setModifiedUser(user.getUsername());
        user.setModifiedTime(new Date());
        try {
            userMapper.update(user);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("User 修改失败，数据库发生未知异常！user = " + user);
            throw new ModifyFailedException("修改失败，数据库发生未知异常！");
        }
        logger.warn("User 修改成功！user = " + user);
    }

    @Override
    public void modifyGender(Integer userId, String gender) throws ModifyFailedException {
        User user = null;
        try {
            user = userMapper.findById(userId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("User 查询失败，数据库发生未知异常！userId = " + userId);
            throw new ModifyFailedException("修改失败，数据库发生未知异常！");
        }
        if (user == null) {
            logger.warn("User 不存在该user！userId = " + userId);
            throw new ModifyFailedException("修改失败，账号未注册！");
        }
        if (USER_GENDER_MAN.equals(gender)) {
            user.setGender(USER_GENDER_MAN);
        } else if (USER_GENDER_WOMAN.equals(gender)) {
            user.setGender(USER_GENDER_WOMAN);
        } else if (USER_GENDER_SECRECY.equals(gender)) {
            user.setGender(USER_GENDER_SECRECY);
        } else {
            logger.warn("User 性别修改非法！gender = " + gender);
            throw new ModifyFailedException("修改失败，性别设置非法！");
        }
        user.setModifiedUser(user.getUsername());
        user.setModifiedTime(new Date());
        try {
            userMapper.update(user);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("User 修改失败，数据库发生未知异常！user = " + user);
            throw new ModifyFailedException("修改失败，数据库发生未知异常！");
        }
        logger.warn("User 性别修改成功！user = " + user);
    }

    @Override
    public User findById(Integer id) throws FindFailedException {
        User user = null;
        try {
            user = userMapper.findById(id);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("User 查询失败，数据库发生未知异常！userId = " + id);
            throw new FindFailedException("查询失败，数据库发生未知异常！");
        }
        user.setPassword(null);
        user.setSaltValue(null);
        logger.warn("User 查询成功！user = " + user);
        return user;
    }

    @Override
    public User systemFindById(Integer id) throws FindFailedException {
        if (id == null) {
            logger.warn("User 查询失败，未指定用户ID");
            throw new FindFailedException("查询失败，未指定需要查询的用户ID");
        }
        User user = null;
        try {
            user = userMapper.findById(id);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("User 查询失败，数据库发生未知异常！userId = " + id);
            throw new FindFailedException("查询失败，数据库发生未知异常！");
        }
        logger.warn("User 查询成功！user = " + user);
        return user;
    }
}
