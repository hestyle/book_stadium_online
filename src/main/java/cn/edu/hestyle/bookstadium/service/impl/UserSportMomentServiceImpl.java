package cn.edu.hestyle.bookstadium.service.impl;

import cn.edu.hestyle.bookstadium.entity.*;
import cn.edu.hestyle.bookstadium.mapper.NoticeMapper;
import cn.edu.hestyle.bookstadium.mapper.SportMomentLikeMapper;
import cn.edu.hestyle.bookstadium.mapper.SportMomentMapper;
import cn.edu.hestyle.bookstadium.mapper.UserMapper;
import cn.edu.hestyle.bookstadium.service.IUserSportMomentService;
import cn.edu.hestyle.bookstadium.service.exception.AddFailedException;
import cn.edu.hestyle.bookstadium.service.exception.DeleteFailedException;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;
import cn.edu.hestyle.bookstadium.service.exception.ModifyFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/16 5:00 下午
 */
@Service
public class UserSportMomentServiceImpl implements IUserSportMomentService {
    /** 评论的最大长度 */
    private static final Integer SPORT_MOMENT_CONTENT_MAX_LENGTH = 200;
    /** 动态包含的最大图片数 */
    private static final Integer SPORT_MOMENT_IMAGE_PATH_MAX_LENGTH = 3;
    private static final Logger logger = LoggerFactory.getLogger(UserSportMomentServiceImpl.class);
    /** 点赞通知title、content */
    private static final String SPORT_MOMENT_LIKE_TITLE = "运动动态点赞";
    private static final String SPORT_MOMENT_LIKE_CONTENT = "用户【%s】点赞了你的运动动态【%s...】";

    @Resource
    private UserMapper userMapper;
    @Resource
    private NoticeMapper noticeMapper;
    @Resource
    private SportMomentMapper sportMomentMapper;
    @Resource
    private SportMomentLikeMapper sportMomentLikeMapper;

    @Override
    public void add(UserSportMoment userSportMoment) throws AddFailedException {
        if (userSportMoment == null || userSportMoment.getUserId() == null) {
            logger.warn("UserSportMoment 增加失败，为指明user id！userSportMoment = " + userSportMoment);
            throw new FindFailedException("保存失败，系统未识别您的账号！");
        }
        // 检查字段的合法性
        if (userSportMoment.getContent() == null || userSportMoment.getContent().length() == 0) {
            logger.warn("UserSportMoment 增加失败，未填写运动动态的内容！userSportMoment = " + userSportMoment);
            throw new FindFailedException("保存失败，未填写运动动态的内容！");
        }
        // 检查imagePaths
        try {
            checkImagePaths(userSportMoment.getImagePaths());
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("UserSportMoment 添加失败，image path列表格式错误！userSportMoment = " + userSportMoment);
            throw new AddFailedException("保存失败，动态包含的图片格式错误！");
        }
        SportMoment sportMoment = new SportMoment();
        sportMoment.setUserId(userSportMoment.getUserId());
        sportMoment.setContent(userSportMoment.getContent());
        sportMoment.setImagePaths(userSportMoment.getImagePaths());
        sportMoment.setLikeCount(0);
        sportMoment.setCommentCount(0);
        sportMoment.setSentTime(new Date());
        sportMoment.setIsDelete(0);
        try {
            sportMomentMapper.add(sportMoment);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("UserSportMoment 添加失败，数据库发生未知错误！sportMoment = " + sportMoment);
            throw new AddFailedException("保存失败，数据库发生未知错误！");
        }
        logger.warn("UserSportMoment 添加成功！sportMoment = " + sportMoment);
    }

    @Override
    @Transactional
    public void like(Integer userId, Integer sportMomentId) throws AddFailedException {
        // 检查sportMoment是否存在
        if (sportMomentId == null) {
            logger.warn("SportMoment 点赞失败，未指定需要点赞的sportMomentId！");
            throw new FindFailedException("点赞失败，未指定需要点赞的sportMomentId！");
        }
        SportMoment sportMoment = null;
        try {
            sportMoment = sportMomentMapper.findById(sportMomentId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SportMoment 查找失败，数据库发生未知错误！sportMomentId = " + sportMomentId);
            throw new AddFailedException("点赞失败，数据库发生未知错误！");
        }
        if (sportMoment == null) {
            logger.warn("SportMoment 点赞失败，不存在该运动动态！sportMomentId = " + sportMomentId);
            throw new FindFailedException("点赞失败，不存在该运动动态！");
        }
        // 检查是否点过赞
        SportMomentLike sportMomentLike = null;
        try {
            sportMomentLike = sportMomentLikeMapper.findByUserIdAndSportMomentId(userId, sportMomentId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SportMomentLike 查找失败，数据库发生未知错误！userId = " + userId + ", sportMomentId = " + sportMomentId);
            throw new AddFailedException("点赞失败，数据库发生未知错误！");
        }
        if (sportMomentLike != null) {
            logger.warn("SportMomentLike 点赞失败，重复点赞！sportMomentLike = " + sportMomentLike);
            throw new FindFailedException("点赞失败，您已经点赞过了！");
        }
        sportMomentLike = new SportMomentLike();
        sportMomentLike.setUserId(userId);
        sportMomentLike.setSportMomentId(sportMomentId);
        sportMomentLike.setLikedTime(new Date());
        sportMomentLike.setIsDelete(0);
        try {
            sportMomentLikeMapper.add(sportMomentLike);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SportMomentLike 添加失败，数据库发生未知错误！sportMomentLike = " + sportMomentLike);
            throw new AddFailedException("点赞失败，数据库发生未知错误！");
        }
        logger.warn("SportMomentLike 增加成功！sportMomentLike = " + sportMomentLike);
        // 然后修改sportMoment的点赞数量
        sportMoment.setLikeCount(sportMoment.getLikeCount() + 1);
        sportMoment.setModifiedUser("System");
        sportMoment.setModifiedTime(new Date());
        try {
            sportMomentMapper.update(sportMoment);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SportMoment 修改失败，数据库发生未知错误！sportMoment = " + sportMoment);
            throw new AddFailedException("点赞失败，数据库发生未知错误！");
        }
        logger.warn("SportMoment 修改成功！sportMoment = " + sportMoment);
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
        notice.setAccountId(sportMoment.getUserId());
        notice.setTitle(SPORT_MOMENT_LIKE_TITLE);
        if (user != null) {
            String sportMomentContent = sportMoment.getContent();
            if (sportMomentContent.length() >= 20) {
                notice.setContent(String.format(SPORT_MOMENT_LIKE_CONTENT, user.getUsername(), sportMoment.getContent().substring(0, 20)));
            } else {
                notice.setContent(String.format(SPORT_MOMENT_LIKE_CONTENT, user.getUsername(), sportMoment.getContent()));
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
    public void dislike(Integer userId, Integer sportMomentId) throws AddFailedException {
        // 检查sportMoment是否存在
        if (sportMomentId == null) {
            logger.warn("SportMoment 点赞取消失败，未指定需要点赞的sportMomentId！");
            throw new FindFailedException("点赞取消失败，未指定需要取消点赞的sportMomentId！");
        }
        SportMoment sportMoment = null;
        try {
            sportMoment = sportMomentMapper.findById(sportMomentId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SportMoment 查找失败，数据库发生未知错误！sportMomentId = " + sportMomentId);
            throw new AddFailedException("点赞取消失败，数据库发生未知错误！");
        }
        if (sportMoment == null) {
            logger.warn("SportMoment 点赞取消失败，不存在该运动动态！sportMomentId = " + sportMomentId);
            throw new FindFailedException("点赞取消失败，不存在该运动动态！");
        }
        // 检查是否点过赞
        SportMomentLike sportMomentLike = null;
        try {
            sportMomentLike = sportMomentLikeMapper.findByUserIdAndSportMomentId(userId, sportMomentId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SportMomentLike 查找失败，数据库发生未知错误！userId = " + userId + ", sportMomentId = " + sportMomentId);
            throw new AddFailedException("点赞取消失败，数据库发生未知错误！");
        }
        if (sportMomentLike == null) {
            logger.warn("SportMomentLike 点赞取消失败，未点赞！sportMomentLike = " + sportMomentLike);
            throw new FindFailedException("点赞取消失败，您还未点赞该动态！");
        }
        try {
            sportMomentLikeMapper.delete(sportMomentLike.getId());
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SportMomentLike 删除失败，数据库发生未知错误！sportMomentLike = " + sportMomentLike);
            throw new AddFailedException("点赞取消失败，数据库发生未知错误！");
        }
        logger.warn("SportMomentLike 删除成功！sportMomentLike = " + sportMomentLike);
        // 然后修改sportMoment的点赞数量
        sportMoment.setLikeCount(sportMoment.getLikeCount() - 1);
        sportMoment.setModifiedUser("System");
        sportMoment.setModifiedTime(new Date());
        try {
            sportMomentMapper.update(sportMoment);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SportMoment 修改失败，数据库发生未知错误！sportMoment = " + sportMoment);
            throw new AddFailedException("点赞取消失败，数据库发生未知错误！");
        }
        logger.warn("SportMoment 修改成功！sportMoment = " + sportMoment);
    }

    @Override
    public boolean hasLiked(Integer userId, Integer sportMomentId) throws FindFailedException {
        // 检查是否点过赞
        SportMomentLike sportMomentLike = null;
        try {
            sportMomentLike = sportMomentLikeMapper.findByUserIdAndSportMomentId(userId, sportMomentId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SportMomentLike 查找失败，数据库发生未知错误！userId = " + userId + ", sportMomentId = " + sportMomentId);
            throw new FindFailedException("查找失败，数据库发生未知错误！");
        }
        return sportMomentLike != null;
    }

    @Override
    public void modify(Integer userId, UserSportMoment userSportMoment) throws ModifyFailedException {
        if (userSportMoment == null || userSportMoment.getSportMomentId() == null) {
            logger.warn("SportMoment 修改失败，未指定需要修改的 sportMomentId！userSportMoment =" + userSportMoment);
            throw new ModifyFailedException("修改失败，未指定需要修改的动态！");
        }
        SportMoment sportMoment = null;
        try {
            sportMoment = sportMomentMapper.findById(userSportMoment.getSportMomentId());
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SportMoment 查找失败，数据库发生未知异常！sportMomentId = " + userSportMoment.getSportMomentId());
            throw new ModifyFailedException("修改失败，数据库发生未知异常！");
        }
        if (sportMoment == null) {
            logger.warn("SportMoment 修改失败，不存在该SportMoment！sportMomentId = " + userSportMoment.getSportMomentId());
            throw new ModifyFailedException("修改失败，不存在该运动动态！");
        }
        // 判断userId删除的sportMoment是否是自己创建的
        if (!sportMoment.getUserId().equals(userId)) {
            logger.warn("SportMoment 修改失败，用户 userId = " + userId + "无法修改其他账号的SportMoment！sportMoment = " + sportMoment);
            throw new ModifyFailedException("修改失败，您的账号无权限修改该运动动态！");
        }
        // 检查字段的合法性
        if (userSportMoment.getContent() == null || userSportMoment.getContent().length() == 0) {
            logger.warn("SportMoment 修改失败，未填写运动动态的内容！userSportMoment = " + userSportMoment);
            throw new ModifyFailedException("修改失败，未填写运动动态的内容！");
        }
        if (userSportMoment.getContent().length() > SPORT_MOMENT_CONTENT_MAX_LENGTH) {
            logger.warn("SportMoment 修改失败，运动动态的内容长度超过了" + SPORT_MOMENT_CONTENT_MAX_LENGTH + "个字符！userSportMoment = " + userSportMoment);
            throw new ModifyFailedException("修改失败，运动动态的内容长度超过了" + SPORT_MOMENT_CONTENT_MAX_LENGTH + "个字符！");
        }
        // 检查imagePaths
        try {
            checkImagePaths(userSportMoment.getImagePaths());
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SportMoment 修改失败，image path列表格式错误！userSportMoment = " + userSportMoment);
            throw new ModifyFailedException("修改失败，动态包含的图片格式错误！");
        }
        sportMoment.setContent(userSportMoment.getContent());
        sportMoment.setImagePaths(userSportMoment.getImagePaths());
        sportMoment.setModifiedUser("System");
        sportMoment.setModifiedTime(new Date());
        try {
            sportMomentMapper.update(sportMoment);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SportMoment 修改失败，数据库发生未知错误！userSportMoment = " + userSportMoment + ", sportMoment = " + sportMoment);
            throw new ModifyFailedException("修改失败，数据库发生未知错误！");
        }
        logger.warn("SportMoment 修改成功！userSportMoment = " + userSportMoment + ", sportMoment = " + sportMoment);
    }

    @Override
    public void deleteBySportMomentId(Integer userId, Integer sportMomentId) throws DeleteFailedException {
        if (sportMomentId == null) {
            logger.warn("SportMoment 删除失败，未指定sportMomentId！");
            throw new DeleteFailedException("删除失败，未指定sportMomentId！");
        }
        SportMoment sportMoment = null;
        try {
            sportMoment = sportMomentMapper.findById(sportMomentId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SportMoment 查找失败，数据库发生未知异常！sportMomentId = " + sportMomentId);
            throw new DeleteFailedException("删除失败，数据库发生未知异常！");
        }
        if (sportMoment == null) {
            logger.warn("SportMoment 删除失败，不存在该SportMoment！sportMomentId = " + sportMomentId);
            throw new DeleteFailedException("删除失败，不存在该运动动态！");
        }
        // 判断userId删除的sportMoment是否是自己创建的
        if (!sportMoment.getUserId().equals(userId)) {
            logger.warn("SportMoment 删除失败，用户 userId = " + userId + "无法删除其他账号的SportMoment！sportMoment = " + sportMoment);
            throw new DeleteFailedException("删除失败，您的账号无权限删除该运动动态！");
        }
        sportMoment.setIsDelete(1);
        sportMoment.setModifiedUser("System");
        sportMoment.setModifiedTime(new Date());
        try {
            sportMomentMapper.update(sportMoment);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SportMoment 删除失败，数据库发生未知异常！sportMoment = " + sportMoment);
            throw new DeleteFailedException("删除失败，数据库发生未知异常！");
        }
        logger.warn("SportMoment 删除成功！sportMoment = " + sportMoment);
    }

    @Override
    public UserSportMoment findById(Integer sportMomentId) throws FindFailedException {
        if (sportMomentId == null) {
            logger.warn("UserSportMoment 查找失败，未指定sportMomentId！");
            throw new FindFailedException("查找失败，未指定sportMomentId！");
        }
        SportMoment sportMoment = null;
        try {
            sportMoment = sportMomentMapper.findById(sportMomentId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SportMoment 查找失败，数据库发生未知异常！sportMomentId = " + sportMomentId);
            throw new FindFailedException("查询失败，数据库发生未知异常！");
        }
        if (sportMoment == null) {
            logger.warn("SportMoment 查找失败，不存在该SportMoment！sportMomentId = " + sportMomentId);
            throw new FindFailedException("查找失败，不存在该运动动态！");
        }
        UserSportMoment userSportMoment = sportMoment.toUserSportMoment();
        if (userSportMoment.getUserId() != null) {
            // 拼接username、avatarPath字段
            User user = null;
            try {
                user = userMapper.findById(userSportMoment.getUserId());
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("User 查找失败，数据库发生未知异常！userId = " + userSportMoment.getUserId());
                throw new FindFailedException("查询失败，数据库发生未知异常！");
            }
            if (user != null) {
                userSportMoment.setUsername(user.getUsername());
                userSportMoment.setUserAvatarPath(user.getAvatarPath());
            }
        }
        logger.info("UserSportMoment 查找成功！userSportMoment = " + userSportMoment);
        return userSportMoment;
    }

    @Override
    public List<UserSportMoment> findByUserIdAndPage(Integer userId, Integer pageIndex, Integer pageSize) throws FindFailedException {
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
            sportMomentList = sportMomentMapper.findByUserIdAndPage(userId, (pageIndex - 1) * pageSize, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SportMoment 查找失败，数据库发生未知异常！pageIndex = " + pageIndex + ", pageSize = " + pageSize);
            throw new FindFailedException("查询失败，数据库发生未知异常！");
        }
        List<UserSportMoment> userSportMomentList = toUserSportMomentList(sportMomentList);
        logger.warn("UserSportMoment 查找成功！userSportMomentList = " + userSportMomentList);
        return userSportMomentList;
    }

    @Override
    public List<UserSportMoment> findByContentKeyPage(String contentKey, Integer pageIndex, Integer pageSize) throws FindFailedException {
        if (contentKey == null || contentKey.length() == 0) {
            logger.warn("SportMoment 查找失败，contentKey为空！");
            throw new FindFailedException("查询失败，内容关键字为空！");
        }
        if (contentKey.length() > SPORT_MOMENT_CONTENT_MAX_LENGTH / 2) {
            logger.warn("SportMoment 查找失败，contentKey字符串过长，超过了 " + SPORT_MOMENT_CONTENT_MAX_LENGTH / 2 + " 个字符！");
            throw new FindFailedException("查询失败，内容关键字为空！");
        }
        // 检查页码是否合法
        if (pageIndex < 1) {
            throw new FindFailedException("查询失败，页码 " + pageIndex + " 非法，必须大于0！");
        }
        // 检查页大小是否合法
        if (pageSize < 1) {
            throw new FindFailedException("查询失败，页大小 " + pageSize + " 非法，必须大于0！");
        }
        // 去除特殊字符
        contentKey = contentKey.replaceAll("%", "").replaceAll("'", "").replaceAll("\\?", "");
        List<SportMoment> sportMomentList = null;
        try {
            sportMomentList = sportMomentMapper.findByContentKeyAndPage(contentKey, (pageIndex - 1) * pageSize, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SportMoment 查找失败，数据库发生未知异常！pageIndex = " + pageIndex + ", pageSize = " + pageSize);
            throw new FindFailedException("查询失败，数据库发生未知异常！");
        }
        List<UserSportMoment> userSportMomentList = toUserSportMomentList(sportMomentList);
        logger.warn("UserSportMoment 查找成功！userSportMomentList = " + userSportMomentList);
        return userSportMomentList;
    }

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
        List<UserSportMoment> userSportMomentList = toUserSportMomentList(sportMomentList);
        logger.warn("UserSportMoment 查找成功！userSportMomentList = " + userSportMomentList);
        return userSportMomentList;
    }

    /**
     * sportMomentList to userSportMomentList
     * @param sportMomentList       sportMomentList
     * @return                      List UserSportMoment
     */
    private List<UserSportMoment> toUserSportMomentList(List<SportMoment> sportMomentList) {
        List<UserSportMoment> userSportMomentList = new ArrayList<>();
        if (sportMomentList != null && sportMomentList.size() != 0) {
            for (SportMoment sportMoment : sportMomentList) {
                UserSportMoment userSportMoment = sportMoment.toUserSportMoment();
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
        return userSportMomentList;
    }

    /**
     * 检查imagePaths的合法性
     * @param imagePathsString      以逗号间隔的image path
     * @return                      是否合法
     * @throws Exception            image path异常
     */
    private boolean checkImagePaths(String imagePathsString) throws Exception {
        if (imagePathsString == null || imagePathsString.length() == 0) {
            return true;
        }
        String[] imagePaths = imagePathsString.split(",");
        for (String imagePath : imagePaths) {
            if (imagePath == null || imagePath.length() == 0) {
                throw new Exception(imagePath + "文件不存在！");
            }
            try {
                String pathNameTemp = ResourceUtils.getURL("classpath:").getPath() + "static/upload/image/sportMoment";
                String pathNameTruth = pathNameTemp.replace("target", "src").replace("classes", "main/resources");
                String filePath = pathNameTruth + imagePath.substring(imagePath.lastIndexOf('/'));
                File file = new File(filePath);
                if (!file.exists()) {
                    throw new Exception(imagePath + "文件不存在！");
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception(imagePath + "文件不存在！");
            }
        }
        if (imagePaths.length > SPORT_MOMENT_IMAGE_PATH_MAX_LENGTH) {
            throw new Exception("体育场馆最多上传" + SPORT_MOMENT_IMAGE_PATH_MAX_LENGTH + "张照片！");
        }
        logger.info("SportMoment imagePaths = " + imagePathsString + " 通过检查！");
        return false;
    }
}
