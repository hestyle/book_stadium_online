package cn.edu.hestyle.bookstadium.service.impl;

import cn.edu.hestyle.bookstadium.entity.StadiumComment;
import cn.edu.hestyle.bookstadium.entity.User;
import cn.edu.hestyle.bookstadium.mapper.StadiumCommentMapper;
import cn.edu.hestyle.bookstadium.mapper.UserMapper;
import cn.edu.hestyle.bookstadium.service.IStadiumCommentService;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
    private static final Logger logger = LoggerFactory.getLogger(StadiumCategoryServiceImpl.class);

    @Resource
    private UserMapper userMapper;

    @Resource
    private StadiumCommentMapper stadiumCommentMapper;

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
