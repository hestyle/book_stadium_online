package cn.edu.hestyle.bookstadium.service.impl;

import cn.edu.hestyle.bookstadium.entity.SportMoment;
import cn.edu.hestyle.bookstadium.entity.User;
import cn.edu.hestyle.bookstadium.entity.UserSportMoment;
import cn.edu.hestyle.bookstadium.mapper.SportMomentMapper;
import cn.edu.hestyle.bookstadium.mapper.UserMapper;
import cn.edu.hestyle.bookstadium.service.IUserSportMomentService;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/16 5:00 下午
 */
@Service
public class UserSportMomentServiceImpl implements IUserSportMomentService {
    private static final Logger logger = LoggerFactory.getLogger(UserSportMomentServiceImpl.class);

    @Resource
    private UserMapper userMapper;
    @Resource
    private SportMomentMapper sportMomentMapper;

    @Override
    public List<UserSportMoment> findByPage(Integer pageIndex, Integer pageSize) throws FindFailedException {
        // 检查页码是否合法
        if (pageIndex < 1) {
            throw new FindFailedException("查询失败，页码 " + pageIndex + " 非法，必须大于0！");
        }
        // 检查页大小是否合法
        if (pageSize < 1) {
            throw new FindFailedException("查询失败，页大小 " + pageSize + " 非法，必须大于0！");
        }
        List<SportMoment> sportMomentList = null;
        try {
            sportMomentList = sportMomentMapper.findByPage((pageIndex - 1) * pageSize, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SportMoment 查找失败，数据库发生未知异常！pageIndex = " + pageIndex + ", pageSize = " + pageSize);
            throw new FindFailedException("查询失败，数据库发生未知异常！");
        }
        List<UserSportMoment> userSportMomentList = new ArrayList<>();
        if (sportMomentList != null && sportMomentList.size() != 0) {
            for (SportMoment sportMoment : sportMomentList) {
                UserSportMoment userSportMoment = UserSportMomentServiceImpl.toUserSportMoment(sportMoment);
                Integer userId = userSportMoment.getUserId();
                if (userId != null) {
                    User user = null;
                    try {
                        user = userMapper.findById(userId);
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.warn("User 查找失败，数据库发生未知异常！userId = " + userId);
                        throw new FindFailedException("查询失败，数据库发生未知异常！");
                    }
                    if (user != null) {
                        userSportMoment.setUsername(user.getUsername());
                        userSportMoment.setUserAvatarPath(user.getAvatarPath());
                    }
                }
                userSportMomentList.add(userSportMoment);
            }
        }
        logger.warn("UserSportMoment 查找成功！userSportMomentList = " + userSportMomentList);
        return userSportMomentList;
    }

    /**
     * SportMoment to UserSportMoment
     * @param sportMoment       sportMoment
     * @return                  userSportMoment
     */
    public static UserSportMoment toUserSportMoment(SportMoment sportMoment) {
        UserSportMoment userSportMoment = new UserSportMoment();
        if (sportMoment != null) {
            userSportMoment.setSportMomentId(sportMoment.getId());
            userSportMoment.setUserId(sportMoment.getUserId());
            userSportMoment.setContent(sportMoment.getContent());
            userSportMoment.setImagePaths(sportMoment.getImagePaths());
            userSportMoment.setLikeCount(sportMoment.getLikeCount());
            userSportMoment.setCommentCount(sportMoment.getCommentCount());
            userSportMoment.setSentTime(sportMoment.getSentTime());
            userSportMoment.setIsDelete(sportMoment.getIsDelete());
            userSportMoment.setModifiedUser(sportMoment.getModifiedUser());
            userSportMoment.setModifiedTime(sportMoment.getModifiedTime());
        }
        return userSportMoment;
    }
}
