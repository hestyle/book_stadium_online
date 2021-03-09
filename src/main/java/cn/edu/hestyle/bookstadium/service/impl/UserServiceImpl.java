package cn.edu.hestyle.bookstadium.service.impl;

import cn.edu.hestyle.bookstadium.entity.User;
import cn.edu.hestyle.bookstadium.mapper.UserMapper;
import cn.edu.hestyle.bookstadium.service.IUserService;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/9 12:46 下午
 */
@Service
public class UserServiceImpl implements IUserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Resource
    private UserMapper userMapper;

    @Override
    public User findById(Integer id) throws FindFailedException {
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
