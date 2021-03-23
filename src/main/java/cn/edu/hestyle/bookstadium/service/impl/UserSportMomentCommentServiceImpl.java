package cn.edu.hestyle.bookstadium.service.impl;

import cn.edu.hestyle.bookstadium.entity.*;
import cn.edu.hestyle.bookstadium.mapper.*;
import cn.edu.hestyle.bookstadium.service.IUserSportMomentCommentService;
import cn.edu.hestyle.bookstadium.service.exception.AddFailedException;
import cn.edu.hestyle.bookstadium.service.exception.DeleteFailedException;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
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
    /** 评论内容最大长度 */
    private static final Integer USER_SPORT_MOMENT_COMMENT_CONTENT_MAX_LENGTH = 200;

    /** 评论动态通知title、content */
    private static final String SPORT_MOMENT_COMMENT_TITLE = "运动动态评论";
    private static final String SPORT_MOMENT_COMMENT_CONTENT = "用户【%s】评论了你的运动动态【%s...】";
    /** 回复动态评论通知title、content */
    private static final String SPORT_MOMENT_COMMENT_REPLY_TITLE = "运动动态评论回复";
    private static final String SPORT_MOMENT_COMMENT_REPLY_CONTENT = "用户【%s】回复了你的运动动态评论【%s...】";
    /** 点赞动态评论通知title、content */
    private static final String SPORT_MOMENT_COMMENT_LIKE_TITLE = "运动动态评论点赞";
    private static final String SPORT_MOMENT_COMMENT_LIKE_CONTENT = "用户【%s】点赞了你的运动动态评论【%s...】";

    private static final Logger logger = LoggerFactory.getLogger(UserSportMomentCommentServiceImpl.class);

    @Resource
    private UserMapper userMapper;
    @Resource
    private NoticeMapper noticeMapper;
    @Resource
    private SportMomentMapper sportMomentMapper;
    @Resource
    private SportMomentCommentMapper sportMomentCommentMapper;
    @Resource
    private SportMomentCommentLikeMapper sportMomentCommentLikeMapper;

    @Override
    @Transactional
    public void add(Integer userId, UserSportMomentComment userSportMomentComment) throws AddFailedException {
        // 检查各个字段合法性
        if (userSportMomentComment == null || userSportMomentComment.getSportMomentId() == null) {
            logger.warn("SportMomentComment 添加失败，未指定需要评论的SportMomentId！");
            throw new AddFailedException("评论失败，未指定需要评论的SportMomentId！");
        }
        SportMoment sportMoment = null;
        try {
            sportMoment = sportMomentMapper.findById(userSportMomentComment.getSportMomentId());
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SportMoment 查找失败，数据库发生未知错误！sportMomentId = " + userSportMomentComment.getSportMomentId());
            throw new AddFailedException("评论失败，数据库发生未知错误！");
        }
        if (sportMoment == null) {
            logger.warn("SportMomentComment 添加失败，不存在该SportMoment！sportMomentId = " + userSportMomentComment.getSportMomentId());
            throw new AddFailedException("评论失败，不存在该SportMoment！");
        }
        // 检查Content
        if (userSportMomentComment.getContent() == null || userSportMomentComment.getContent().length() == 0) {
            logger.warn("SportMomentComment 添加失败，未填写评论内容！userSportMomentComment = " + userSportMomentComment);
            throw new AddFailedException("评论失败，未填写评论内容！");
        }
        if (userSportMomentComment.getContent().length() > USER_SPORT_MOMENT_COMMENT_CONTENT_MAX_LENGTH) {
            logger.warn("SportMomentComment 添加失败，评论内容超过了 " + USER_SPORT_MOMENT_COMMENT_CONTENT_MAX_LENGTH  + " 个字符！userSportMomentComment = " + userSportMomentComment);
            throw new AddFailedException("评论失败，评论内容超过了 " + USER_SPORT_MOMENT_COMMENT_CONTENT_MAX_LENGTH  + " 个字符");
        }
        // 给sportMoment对应的user发送通知
        Notice notice = new Notice();
        notice.setToAccountType(Notice.TO_ACCOUNT_USER);
        // 追评需要检查原评论是否存在
        if (userSportMomentComment.getParentId() != null) {
            SportMomentComment sportMomentComment = null;
            try {
                sportMomentComment = sportMomentCommentMapper.findById(userSportMomentComment.getParentId());
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("SportMomentComment 查找失败，数据库发生未知错误！sportMomentCommentId = " + userSportMomentComment.getParentId());
                throw new AddFailedException("评论失败，数据库发生未知错误！");
            }
            if (sportMomentComment == null) {
                logger.warn("SportMomentComment 添加失败，回复的原评论不存在！sportMomentCommentId = " + userSportMomentComment.getParentId());
                throw new AddFailedException("回复失败，原评论不存在，或已删除、屏蔽！");
            }
            User user = null;
            try {
                user = userMapper.findById(sportMomentComment.getUserId());
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("User 查找失败，数据库发生未知错误！userId = " + userId);
                throw new AddFailedException("评论失败，数据库发生未知错误！");
            }
            notice.setAccountId(sportMoment.getUserId());
            notice.setTitle(SPORT_MOMENT_COMMENT_REPLY_TITLE);
            if (user != null) {
                String sportMomentContent = sportMomentComment.getContent();
                if (sportMomentContent.length() >= 20) {
                    notice.setContent(String.format(SPORT_MOMENT_COMMENT_REPLY_CONTENT, user.getUsername(), sportMomentContent.substring(0, 20)));
                } else {
                    notice.setContent(String.format(SPORT_MOMENT_COMMENT_REPLY_CONTENT, user.getUsername(), sportMomentContent));
                }
            }
        } else {
            User user = null;
            try {
                user = userMapper.findById(sportMoment.getUserId());
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("User 查找失败，数据库发生未知错误！userId = " + userId);
                throw new AddFailedException("评论失败，数据库发生未知错误！");
            }
            notice.setAccountId(sportMoment.getUserId());
            notice.setTitle(SPORT_MOMENT_COMMENT_TITLE);
            if (user != null) {
                String sportMomentContent = sportMoment.getContent();
                if (sportMomentContent.length() >= 20) {
                    notice.setContent(String.format(SPORT_MOMENT_COMMENT_CONTENT, user.getUsername(), sportMoment.getContent().substring(0, 20)));
                } else {
                    notice.setContent(String.format(SPORT_MOMENT_COMMENT_CONTENT, user.getUsername(), sportMoment.getContent()));
                }
            }
        }
        notice.setGeneratedTime(new Date());
        notice.setIsDelete(0);
        try {
            noticeMapper.add(notice);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Notice 添加失败，数据库发生未知错误！notice = " + notice);
            throw new AddFailedException("评论失败，数据库发生未知错误！");
        }
        logger.warn("Notice 添加成功！notice = " + notice);
        SportMomentComment additionSportMomentComment = new SportMomentComment();
        additionSportMomentComment.setUserId(userId);
        additionSportMomentComment.setSportMomentId(userSportMomentComment.getSportMomentId());
        additionSportMomentComment.setParentId(userSportMomentComment.getParentId());
        additionSportMomentComment.setContent(userSportMomentComment.getContent());
        additionSportMomentComment.setCommentedTime(new Date());
        additionSportMomentComment.setLikeCount(0);
        additionSportMomentComment.setIsDelete(0);
        logger.info("SportMomentComment 评论添加事务开启----");
        // 插入additionSportMomentComment
        try {
            sportMomentCommentMapper.add(additionSportMomentComment);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SportMomentComment 添加失败，数据库发生未知错误！事务回滚！additionSportMomentComment = " + additionSportMomentComment);
            throw new AddFailedException("评论失败，数据库发生未知错误！");
        }
        logger.warn("SportMomentComment 添加成功！additionSportMomentComment = " + additionSportMomentComment);
        // 修改SportMoment的评论数量
        sportMoment.setCommentCount(sportMoment.getCommentCount() + 1);
        sportMoment.setModifiedUser("System");
        sportMoment.setModifiedTime(new Date());
        try {
            sportMomentMapper.update(sportMoment);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SportMoment 修改失败，数据库发生未知错误！事务回滚！sportMoment = " + sportMoment);
            throw new AddFailedException("评论失败，数据库发生未知错误！");
        }
        logger.warn("SportMoment 修改成功！sportMoment = " + sportMoment);
        logger.warn("SportMomentComment 添加成功！事务提交");
    }

    @Override
    @Transactional
    public void like(Integer userId, Integer sportMomentCommentId) throws AddFailedException {
        if (sportMomentCommentId == null) {
            logger.warn("SportMomentComment 点赞失败，未指定需要点赞的SportMomentCommentId！");
            throw new AddFailedException("点赞失败，未指定需要点赞的SportMomentCommentId！");
        }
        // 判断sportMomentComment是否存在
        SportMomentComment sportMomentComment = null;
        try {
            sportMomentComment = sportMomentCommentMapper.findById(sportMomentCommentId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SportMomentComment 查找失败，数据库发生未知错误！sportMomentCommentId = " + sportMomentCommentId);
            throw new AddFailedException("点赞失败，数据库发生未知错误！");
        }
        if (sportMomentComment == null) {
            logger.warn("SportMomentComment 点赞失败，不存在该SportMomentComment！sportMomentCommentId = " + sportMomentCommentId);
            throw new AddFailedException("点赞失败，不存在该SportMomentComment！");
        }
        // 判断是否点过赞
        SportMomentCommentLike sportMomentCommentLike = null;
        try {
            sportMomentCommentLike = sportMomentCommentLikeMapper.findByUserIdAndSportMomentCommentId(userId, sportMomentCommentId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SportMomentCommentLike 查找失败，数据库发生未知错误！userId = " + userId + ", sportMomentCommentId = " + sportMomentCommentId);
            throw new AddFailedException("点赞失败，数据库发生未知错误！");
        }
        if (sportMomentCommentLike != null) {
            logger.warn("SportMomentCommentLike 点赞失败，重复点赞！sportMomentCommentLike = " + sportMomentCommentLike);
            throw new AddFailedException("点赞失败，您已经点过赞了！");
        }
        // 新增sportMomentCommentLike
        sportMomentCommentLike = new SportMomentCommentLike();
        sportMomentCommentLike.setUserId(userId);
        sportMomentCommentLike.setSportMomentCommentId(sportMomentCommentId);
        sportMomentCommentLike.setLikedTime(new Date());
        sportMomentCommentLike.setIsDelete(0);
        try {
            sportMomentCommentLikeMapper.add(sportMomentCommentLike);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SportMomentCommentLike 新增失败，数据库发生未知错误！事务回滚！sportMomentCommentLike = " + sportMomentCommentLike);
            throw new AddFailedException("点赞失败，数据库发生未知错误！");
        }
        logger.warn("SportMomentCommentLike 新增成功！sportMomentCommentLike = " + sportMomentCommentLike);
        // 修改评论的点赞数量
        sportMomentComment.setLikeCount(sportMomentComment.getLikeCount() + 1);
        try {
            sportMomentCommentMapper.update(sportMomentComment);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SportMomentComment 修改失败，数据库发生未知错误！事务回滚！sportMomentComment = " + sportMomentComment);
            throw new AddFailedException("点赞失败，数据库发生未知错误！");
        }
        logger.warn("SportMomentComment 修改成功！sportMomentComment = " + sportMomentComment);
        User user = null;
        try {
            user = userMapper.findById(userId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("User 查找失败，数据库发生未知错误！userId = " + userId);
            throw new AddFailedException("点赞失败，数据库发生未知错误！");
        }
        // 给sportMoment对应的user发送通知
        Notice notice = new Notice();
        notice.setToAccountType(Notice.TO_ACCOUNT_USER);
        notice.setAccountId(sportMomentComment.getUserId());
        notice.setTitle(SPORT_MOMENT_COMMENT_LIKE_TITLE);
        if (user != null) {
            String sportMomentContent = sportMomentComment.getContent();
            if (sportMomentContent.length() >= 20) {
                notice.setContent(String.format(SPORT_MOMENT_COMMENT_LIKE_CONTENT, user.getUsername(), sportMomentContent.substring(0, 20)));
            } else {
                notice.setContent(String.format(SPORT_MOMENT_COMMENT_LIKE_CONTENT, user.getUsername(), sportMomentContent));
            }
        }
        notice.setGeneratedTime(new Date());
        notice.setIsDelete(0);
        try {
            noticeMapper.add(notice);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Notice 添加失败，数据库发生未知错误！notice = " + notice);
            throw new AddFailedException("点赞失败，数据库发生未知错误！");
        }
        logger.warn("Notice 添加成功！notice = " + notice);
    }

    @Override
    @Transactional
    public void dislike(Integer userId, Integer sportMomentCommentId) throws DeleteFailedException {
        if (sportMomentCommentId == null) {
            logger.warn("SportMomentComment 点赞取消失败，未指定需要取消点赞的SportMomentCommentId！");
            throw new DeleteFailedException("点赞取消失败，未指定需要取消点赞的SportMomentCommentId！");
        }
        // 判断sportMomentComment是否存在
        SportMomentComment sportMomentComment = null;
        try {
            sportMomentComment = sportMomentCommentMapper.findById(sportMomentCommentId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SportMomentComment 查找失败，数据库发生未知错误！sportMomentCommentId = " + sportMomentCommentId);
            throw new DeleteFailedException("点赞取消失败，数据库发生未知错误！");
        }
        if (sportMomentComment == null) {
            logger.warn("SportMomentComment 点赞取消失败，不存在该SportMomentComment！sportMomentCommentId = " + sportMomentCommentId);
            throw new DeleteFailedException("点赞取消失败，不存在该SportMomentComment！");
        }
        // 判断是否点过赞
        SportMomentCommentLike sportMomentCommentLike = null;
        try {
            sportMomentCommentLike = sportMomentCommentLikeMapper.findByUserIdAndSportMomentCommentId(userId, sportMomentCommentId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SportMomentCommentLike 查找失败，数据库发生未知错误！userId = " + userId + ", sportMomentCommentId = " + sportMomentCommentId);
            throw new DeleteFailedException("点赞取消失败，数据库发生未知错误！");
        }
        if (sportMomentCommentLike == null) {
            logger.warn("SportMomentCommentLike 点赞取消失败，还未点赞！userId = " + userId + ", sportMomentCommentId = " + sportMomentCommentId);
            throw new DeleteFailedException("点赞取消失败，您还未点赞！");
        }
        // 删除sportMomentCommentLike
        try {
            sportMomentCommentLikeMapper.deleteById(sportMomentCommentLike.getId());
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SportMomentCommentLike 删除失败，数据库发生未知错误！事务回滚！sportMomentCommentLike = " + sportMomentCommentLike);
            throw new DeleteFailedException("点赞取消失败，数据库发生未知错误！");
        }
        logger.warn("SportMomentCommentLike 删除成功！sportMomentCommentLike = " + sportMomentCommentLike);
        // 修改评论的点赞数量
        sportMomentComment.setLikeCount(sportMomentComment.getLikeCount() - 1);
        try {
            sportMomentCommentMapper.update(sportMomentComment);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SportMomentComment 修改失败，数据库发生未知错误！事务回滚！sportMomentComment = " + sportMomentComment);
            throw new DeleteFailedException("点赞取消失败，数据库发生未知错误！");
        }
        logger.warn("SportMomentComment 修改成功！sportMomentComment = " + sportMomentComment);
    }

    @Override
    public boolean hasLiked(Integer userId, Integer sportMomentCommentId) throws FindFailedException {
        // 判断是否点过赞
        SportMomentCommentLike sportMomentCommentLike = null;
        try {
            sportMomentCommentLike = sportMomentCommentLikeMapper.findByUserIdAndSportMomentCommentId(userId, sportMomentCommentId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SportMomentCommentLike 查找失败，数据库发生未知错误！userId = " + userId + ", sportMomentCommentId = " + sportMomentCommentId);
            throw new AddFailedException("点赞失败，数据库发生未知错误！");
        }
        return sportMomentCommentLike != null;
    }


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
