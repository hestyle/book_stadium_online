package cn.edu.hestyle.bookstadium.service.impl;

import cn.edu.hestyle.bookstadium.entity.SportMoment;
import cn.edu.hestyle.bookstadium.entity.User;
import cn.edu.hestyle.bookstadium.entity.UserSportMoment;
import cn.edu.hestyle.bookstadium.mapper.SportMomentMapper;
import cn.edu.hestyle.bookstadium.mapper.UserMapper;
import cn.edu.hestyle.bookstadium.service.IUserSportMomentService;
import cn.edu.hestyle.bookstadium.service.exception.AddFailedException;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
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

    @Resource
    private UserMapper userMapper;
    @Resource
    private SportMomentMapper sportMomentMapper;

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
