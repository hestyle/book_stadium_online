package cn.edu.hestyle.bookstadium.service.impl;

import cn.edu.hestyle.bookstadium.entity.Stadium;
import cn.edu.hestyle.bookstadium.entity.StadiumBookItem;
import cn.edu.hestyle.bookstadium.entity.StadiumComment;
import cn.edu.hestyle.bookstadium.entity.User;
import cn.edu.hestyle.bookstadium.mapper.StadiumBookItemMapper;
import cn.edu.hestyle.bookstadium.mapper.StadiumCommentMapper;
import cn.edu.hestyle.bookstadium.mapper.StadiumMapper;
import cn.edu.hestyle.bookstadium.mapper.UserMapper;
import cn.edu.hestyle.bookstadium.service.IStadiumCommentService;
import cn.edu.hestyle.bookstadium.service.exception.AddFailedException;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/13 8:57 下午
 */
@Service
public class StadiumCommentServiceImpl implements IStadiumCommentService {
    /** 评论被删除 */
    private static final Integer STADIUM_COMMENT_DELETE = 1;
    /** 评论被屏蔽 */
    private static final Integer STADIUM_COMMENT_BLACK = 2;
    /** 评论的最大长度 */
    private static final Integer STADIUM_COMMENT_CONTENT_MAX_LENGTH = 200;
    /** 评论的最大星级 */
    private static final Integer STADIUM_COMMENT_MAX_STAR_COUNT = 5;
    private static final Logger logger = LoggerFactory.getLogger(StadiumCategoryServiceImpl.class);

    @Resource
    private UserMapper userMapper;
    @Resource
    private StadiumMapper stadiumMapper;
    @Resource
    private StadiumCommentMapper stadiumCommentMapper;
    @Resource
    private StadiumBookItemMapper stadiumBookItemMapper;

    @Override
    @Transactional
    public void userComment(Integer userId, Integer stadiumBookItemId, StadiumComment stadiumComment) throws AddFailedException {
        if (stadiumComment == null || stadiumComment.getStadiumId() == null) {
            logger.warn("StadiumComment 增加失败！未指定需要评论的StadiumId！ stadiumComment = " + stadiumComment);
            throw new AddFailedException("评论失败，未指定需要评论的体育场馆！");
        }
        if (stadiumBookItemId == null) {
            logger.warn("StadiumComment 增加失败！未指定需要评论的场馆预约场次！ stadiumId = " + stadiumComment.getStadiumId());
            throw new AddFailedException("评论失败，未指定需要评论的场馆预约场次！");
        }
        // 检查StadiumBookItem
        StadiumBookItem stadiumBookItem = null;
        try {
            stadiumBookItem = stadiumBookItemMapper.findById(stadiumBookItemId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumBookItem 查询失败！数据库发生未知错误！stadiumBookItemId = " + stadiumBookItemId);
            throw new AddFailedException("评论失败，数据库发生未知错误！");
        }
        if (stadiumBookItem == null) {
            logger.warn("StadiumComment 增加失败！不存在评论的预约场次！stadiumBookItemId = " + stadiumBookItemId);
            throw new AddFailedException("评论失败，不存在评论的预约场次！");
        }
        if (stadiumBookItem.getStadiumCommentId() != null) {
            logger.warn("StadiumComment 增加失败！已经评论过！stadiumBookItem = " + stadiumBookItem);
            throw new AddFailedException("评论失败，您已经评论过了！");
        }
        // 检查参数合法性
        Stadium stadium = null;
        try {
            stadium = stadiumMapper.findById(stadiumComment.getStadiumId());
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Stadium 查询失败！数据库发生未知错误！stadiumId = " + stadiumComment.getStadiumId());
            throw new AddFailedException("评论失败，数据库发生未知错误！");
        }
        if (stadium == null) {
            logger.warn("StadiumComment 增加失败！数据库不存在该场馆！ stadiumId = " + stadiumComment.getStadiumId());
            throw new AddFailedException("评论失败，不存在该体育场馆！");
        }
        stadiumComment.setUserId(userId);
        if (stadiumComment.getContent() == null || stadiumComment.getContent().length() == 0) {
            logger.warn("StadiumComment 增加失败！未输入评论内容！ stadiumComment = " + stadiumComment);
            throw new AddFailedException("评论失败，为填写评论内容！");
        }
        if (stadiumComment.getContent().length() > STADIUM_COMMENT_CONTENT_MAX_LENGTH) {
            logger.warn("StadiumComment 增加失败！评论内容超过 " + STADIUM_COMMENT_CONTENT_MAX_LENGTH +  "个字符！ stadiumComment = " + stadiumComment);
            throw new AddFailedException("评论失败，评论内容超过 " + STADIUM_COMMENT_CONTENT_MAX_LENGTH +  " 个字符！！");
        }
        if (stadiumComment.getStarCount() == null) {
            logger.warn("StadiumComment 增加失败！未填写评论星级！ stadiumComment = " + stadiumComment);
            throw new AddFailedException("评论失败，为填写评论星级！");
        }
        if (stadiumComment.getStarCount() < 0 || stadiumComment.getStarCount() > STADIUM_COMMENT_MAX_STAR_COUNT) {
            logger.warn("StadiumComment 增加失败！评论星级非法！ stadiumComment = " + stadiumComment);
            throw new AddFailedException("评论失败，评论星级非法，只能在[0, " + STADIUM_COMMENT_MAX_STAR_COUNT + "]区间内！");
        }
        // 插入stadiumComment
        stadiumComment.setCommentedTime(new Date());
        stadiumComment.setManagerReply(null);
        stadiumComment.setIsDelete(0);
        try {
            stadiumCommentMapper.add(stadiumComment);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumComment 增加失败！数据库发生未知错误！stadiumComment = " + stadiumComment);
            throw new AddFailedException("评论失败，数据库发生未知错误！");
        }
        // 然后更新StadiumBookItem
        stadiumBookItem.setStadiumCommentId(stadiumComment.getId());
        try {
            stadiumBookItemMapper.update(stadiumBookItem);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumBookItem 修改失败！数据库发生未知错误！stadiumBookItem = " + stadiumBookItem);
            throw new AddFailedException("评论失败，数据库发生未知错误！");
        }
        logger.warn("StadiumComment 增加成功！ stadiumComment = " + stadiumComment);
    }

    @Override
    public StadiumComment findByStadiumCommentId(Integer stadiumCommentId) throws FindFailedException {
        if (stadiumCommentId == null) {
            logger.warn("StadiumComment 查询失败！未指定stadiumCommentId！");
            throw new FindFailedException("查询失败，未指定体育评论id！");
        }
        StadiumComment stadiumComment = null;
        try {
            stadiumComment = stadiumCommentMapper.findByStadiumCommentId(stadiumCommentId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumComment 查询失败！数据库发生未知错误！stadiumCommentId = " + stadiumCommentId);
            throw new FindFailedException("查询失败，数据库发生未知错误！");
        }
        if (stadiumComment != null && stadiumComment.getIsDelete() != null) {
            if (stadiumComment.getIsDelete().equals(STADIUM_COMMENT_DELETE)) {
                stadiumComment.setContent("已删除！");
            } else if (stadiumComment.getIsDelete().equals(STADIUM_COMMENT_BLACK)) {
                stadiumComment.setContent("已被系统屏蔽！");
            }
        }
        logger.warn("StadiumComment 查询成功！stadiumComment = " + stadiumComment);
        return stadiumComment;
    }

    @Override
    public List<StadiumComment> findByStadiumIdAndPage(Integer stadiumId, Integer pageIndex, Integer pageSize) throws FindFailedException {
        if (stadiumId == null) {
            logger.warn("StadiumComment 查询失败！未指定stadiumId！");
            throw new FindFailedException("查询失败，未指定体育场馆id！");
        }
        // 检查页码是否合法
        if (pageIndex < 1) {
            logger.warn("StadiumComment 查询失败！页码 " + pageIndex + " 非法，必须大于0！");
            throw new FindFailedException("查询失败，页码 " + pageIndex + " 非法，必须大于0！");
        }
        // 检查页大小是否合法
        if (pageSize < 1) {
            logger.warn("StadiumComment 查询失败！页大小 " + pageSize + " 非法，必须大于0！");
            throw new FindFailedException("查询失败，页大小 " + pageSize + " 非法，必须大于0！");
        }
        List<StadiumComment> stadiumCommentList = null;
        try {
            stadiumCommentList = stadiumCommentMapper.findByStadiumIdAndPage(stadiumId, (pageIndex - 1) * pageSize, pageSize);
            if (stadiumCommentList != null) {
                // 查找每个评论的user
                for (StadiumComment stadiumComment : stadiumCommentList) {
                    User user = userMapper.findById(stadiumComment.getUserId());
                    user.setPassword(null);
                    user.setSaltValue(null);
                    user.setToken(null);
                    stadiumComment.setCommentUser(user);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumComment 查询失败！数据库发生未知错误！stadiumId = " + stadiumId + "，pageIndex = " + pageIndex + ", pageSize = " + pageSize);
            throw new FindFailedException("查询失败，数据库发生未知错误！");
        }
        logger.warn("StadiumComment 查询成功！stadiumCommentList = " + stadiumCommentList);
        return stadiumCommentList;
    }
}
