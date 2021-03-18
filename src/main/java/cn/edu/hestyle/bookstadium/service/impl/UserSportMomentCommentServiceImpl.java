package cn.edu.hestyle.bookstadium.service.impl;

import cn.edu.hestyle.bookstadium.entity.SportMoment;
import cn.edu.hestyle.bookstadium.entity.SportMomentComment;
import cn.edu.hestyle.bookstadium.entity.User;
import cn.edu.hestyle.bookstadium.entity.UserSportMomentComment;
import cn.edu.hestyle.bookstadium.mapper.SportMomentCommentMapper;
import cn.edu.hestyle.bookstadium.mapper.SportMomentMapper;
import cn.edu.hestyle.bookstadium.mapper.UserMapper;
import cn.edu.hestyle.bookstadium.service.IUserSportMomentCommentService;
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
 * @date 2021/3/18 4:22 下午
 */
@Service
public class UserSportMomentCommentServiceImpl implements IUserSportMomentCommentService {
    /** 评论被删除 */
    private static final Integer USER_SPORT_MOMENT_COMMENT_DELETE_STATE = 1;
    /** 评论被屏蔽 */
    private static final Integer USER_SPORT_MOMENT_COMMENT_BLANK_STATE = 2;

    private static final Logger logger = LoggerFactory.getLogger(UserSportMomentCommentServiceImpl.class);

    @Resource
    private UserMapper userMapper;
    @Resource
    private SportMomentMapper sportMomentMapper;
    @Resource
    private SportMomentCommentMapper sportMomentCommentMapper;

    @Override
    public List<UserSportMomentComment> findBySportMomentIdAndPage(Integer sportMomentId, Integer pageIndex, Integer pageSize) throws FindFailedException {
        if (sportMomentId == null) {
            logger.warn("UserSportMomentComment 查找失败，未指定sportMomentId！");
            throw new FindFailedException("查找失败，未指定运动动态id！");
        }
        SportMoment sportMoment = null;
        try {
            sportMoment = sportMomentMapper.findById(sportMomentId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SportMoment 查找失败，数据库发生未知错误！");
            throw new FindFailedException("查找失败，数据库发生未知错误！");
        }
        if (sportMoment == null) {
            logger.warn("UserSportMomentComment 查找失败，不存在该运动动态！sportMomentId = " + sportMomentId);
            throw new FindFailedException("查找失败，不存在该运动动态！");
        }
        // 检查页码是否合法
        if (pageIndex < 1) {
            throw new FindFailedException("查询失败，页码 " + pageIndex + " 非法，必须大于0！");
        }
        // 检查页大小是否合法
        if (pageSize < 1) {
            throw new FindFailedException("查询失败，页大小 " + pageSize + " 非法，必须大于0！");
        }
        List<SportMomentComment> sportMomentCommentList = null;
        try {
            sportMomentCommentList = sportMomentCommentMapper.findBySportMomentIdAndPage(sportMomentId, (pageIndex - 1) * pageSize, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SportMomentComment 查找失败，数据库发生未知错误！");
            throw new FindFailedException("查找失败，数据库发生未知错误！");
        }
        List<UserSportMomentComment> userSportMomentCommentList = toUserSportMomentCommentList(sportMomentCommentList);
        logger.info("UserSportMomentComment 查询成功！userSportMomentCommentList = " + userSportMomentCommentList);
        return userSportMomentCommentList;
    }

    /**
     * sportMomentCommentList to userSportMomentCommentList
     * @param sportMomentCommentList    sportMomentCommentList
     * @return                          userSportMomentCommentList
     */
    private List<UserSportMomentComment> toUserSportMomentCommentList(List<SportMomentComment> sportMomentCommentList) {
        List<UserSportMomentComment> userSportMomentCommentList = new ArrayList<>();
        if (sportMomentCommentList != null && sportMomentCommentList.size() != 0) {
            for (SportMomentComment sportMomentComment : sportMomentCommentList) {
                UserSportMomentComment userSportMomentComment = sportMomentComment.toUserSportMomentComment();
                // 填充user信息
                if (userSportMomentComment.getUserId() != null) {
                    User user = null;
                    try {
                        user = userMapper.findById(userSportMomentComment.getUserId());
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.warn("User 查找失败，数据库发生未知异常！userId = " + userSportMomentComment.getUserId());
                        throw new FindFailedException("查询失败，数据库发生未知异常！");
                    }
                    if (user != null) {
                        userSportMomentComment.setCommentUsername(user.getUsername());
                        userSportMomentComment.setCommentUserAvatarPath(user.getAvatarPath());
                    }
                }
                // 填充回复的原评论信息
                if (userSportMomentComment.getParentId() != null) {
                    SportMomentComment pSportMomentComment = null;
                    try {
                        pSportMomentComment = sportMomentCommentMapper.findById(userSportMomentComment.getParentId());
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.warn("User 查找失败，数据库发生未知异常！sportMomentCommentId = " + userSportMomentComment.getParentId());
                        throw new FindFailedException("查询失败，数据库发生未知异常！");
                    }
                    if (pSportMomentComment == null || pSportMomentComment.getIsDelete().equals(USER_SPORT_MOMENT_COMMENT_DELETE_STATE)) {
                        userSportMomentComment.setParentContent("已删除");
                    } else if (pSportMomentComment.getIsDelete().equals(USER_SPORT_MOMENT_COMMENT_BLANK_STATE)) {
                        userSportMomentComment.setParentContent("因违规，被系统屏蔽");
                    } else {
                        userSportMomentComment.setParentContent(pSportMomentComment.getContent());
                    }
                }
                userSportMomentCommentList.add(userSportMomentComment);
            }
        }
        return userSportMomentCommentList;
    }
}
