package cn.edu.hestyle.bookstadium.service.impl;

import cn.edu.hestyle.bookstadium.entity.Stadium;
import cn.edu.hestyle.bookstadium.entity.StadiumCategory;
import cn.edu.hestyle.bookstadium.entity.StadiumManager;
import cn.edu.hestyle.bookstadium.mapper.StadiumCategoryMapper;
import cn.edu.hestyle.bookstadium.mapper.StadiumManagerMapper;
import cn.edu.hestyle.bookstadium.mapper.StadiumMapper;
import cn.edu.hestyle.bookstadium.service.IStadiumService;
import cn.edu.hestyle.bookstadium.service.exception.AddFailedException;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/4 10:02 上午
 */
@Service
public class StadiumServiceImpl implements IStadiumService {

    private static final Logger logger = LoggerFactory.getLogger(StadiumServiceImpl.class);

    @Resource
    private StadiumMapper stadiumMapper;
    @Resource
    private StadiumCategoryMapper stadiumCategoryMapper;
    @Resource
    private StadiumManagerMapper stadiumManagerMapper;

    @Override
    public void add(String stadiumManagerUsername, Stadium stadium) throws AddFailedException {
        StadiumManager stadiumManager = null;
        try {
            stadiumManager = stadiumManagerMapper.findByUsername(stadiumManagerUsername);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumManager 查询失败，数据库发生未知异常！username = " + stadiumManagerUsername);
            throw new AddFailedException("添加失败，数据库发生未知异常！");
        }
        if (stadiumManager == null) {
            logger.warn("Stadium 添加失败，username = " + stadiumManagerUsername + "用户未注册！");
            throw new AddFailedException("添加失败，username = " + stadiumManagerUsername + "用户未注册！");
        }
        stadium.setManagerId(stadiumManager.getId());
        // 检查categoryIds
        try {
            checkCategoryIds(stadium.getCategoryIds());
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Stadium 添加失败，category id列表格式错误！data = " + stadium);
            throw new AddFailedException("添加失败，场馆所属分类id格式错误！");
        }
        // 检查imagePaths
        try {
            checkImagePaths(stadium.getImagePaths());
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Stadium 添加失败，image path列表格式错误！data = " + stadium);
            throw new AddFailedException("添加失败，场馆图片格式错误！");
        }
        stadium.setCreatedUser(stadiumManagerUsername);
        stadium.setCreatedTime(new Date());
        try {
            stadiumMapper.add(stadium);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Stadium 添加失败，数据库发生未知异常！data = " + stadium);
            throw new AddFailedException("添加失败，数据库发生未知异常！");
        }
        logger.warn("Stadium 添加成功！data = " + stadium);
    }

    @Override
    public List<Stadium> stadiumManagerFindByPage(String stadiumManagerUsername, Integer pageIndex, Integer pageSize) throws FindFailedException {
        StadiumManager stadiumManager = null;
        try {
            stadiumManager = stadiumManagerMapper.findByUsername(stadiumManagerUsername);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumManager 查询失败，数据库发生未知异常！username = " + stadiumManagerUsername);
            throw new FindFailedException("查询失败，数据库发生未知异常！");
        }
        if (stadiumManager == null) {
            logger.warn("Stadium 查询失败，username = " + stadiumManagerUsername + "用户未注册！");
            throw new FindFailedException("查询失败，username = " + stadiumManagerUsername + "用户未注册！");
        }
        // 检查页码是否合法
        if (pageIndex < 1) {
            throw new FindFailedException("查询失败，页码 " + pageIndex + " 非法，必须大于0！");
        }
        // 检查页大小是否合法
        if (pageSize < 1) {
            throw new FindFailedException("查询失败，页大小 " + pageSize + " 非法，必须大于0！");
        }
        List<Stadium> stadiumList = null;
        try {
            stadiumList = stadiumMapper.stadiumManagerFindByPage(stadiumManager.getId(),(pageIndex - 1) * pageSize, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Stadium 查询失败，数据库发生未知异常！ stadiumManagerId= " + stadiumManager.getId());
            throw new FindFailedException("查询失败，数据库发生未知异常！");
        }
        logger.warn("Stadium 查询成功， stadiumManagerId= " + stadiumManager.getId() + "，data = " + stadiumList);
        return stadiumList;
    }

    @Override
    public Integer stadiumManagerGetCount(String stadiumManagerUsername) throws FindFailedException {
        StadiumManager stadiumManager = null;
        try {
            stadiumManager = stadiumManagerMapper.findByUsername(stadiumManagerUsername);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumManager 查询失败，数据库发生未知异常！username = " + stadiumManagerUsername);
            throw new FindFailedException("查询失败，数据库发生未知异常！");
        }
        if (stadiumManager == null) {
            logger.warn("Stadium 查询失败，username = " + stadiumManagerUsername + "用户未注册！");
            throw new FindFailedException("查询失败，username = " + stadiumManagerUsername + "用户未注册！");
        }
        Integer count = 0;
        try {
            count = stadiumMapper.stadiumManagerGetCount(stadiumManager.getId());
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Stadium 查询失败，数据库发生未知异常！ stadiumManagerId= " + stadiumManager.getId());
            throw new FindFailedException("查询失败，数据库发生未知异常！");
        }
        logger.warn("Stadium 查询成功， stadiumManagerId= " + stadiumManager.getId() + "，count = " + count);
        return count;
    }

    /**
     * 检查categoryIds的合法性
     * @param categoryIds   以逗号间隔的category id
     * @return              是否合法
     */
    private boolean checkCategoryIds(String categoryIds) throws Exception {
        if (categoryIds == null || categoryIds.length() == 0) {
            return true;
        }
        String[] ids = categoryIds.split(",");
        for (String idString : ids) {
            if (idString == null || idString.length() == 0) {
                throw new Exception("category id列表格式错误！");
            }
            Integer id = null;
            try {
                // 各个id必须是正确的数字
                id = Integer.parseInt(idString);
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("操作项id列表格式错误！");
            }
            // 检查id是否存在
            StadiumCategory stadiumCategory = null;
            try {
                // 是否有效的operation_id
                stadiumCategory = stadiumCategoryMapper.findById(id);
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("StadiumCategory 查询失败，数据库发生未知异常！stadiumCategoryId= " + id);
                throw new FindFailedException("查询失败，数据库发生未知异常！");
            }
            if (stadiumCategory == null) {
                logger.warn("StadiumCategory 查询失败，stadiumCategoryId= " + id + "不存在！");
                throw new FindFailedException("查询失败，stadiumCategoryId= " + id + "不存在！");
            }
        }
        logger.info("StadiumCategory stadiumCategoryIds = " + categoryIds + " 通过检查！");
        return true;
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
                String pathNameTemp = ResourceUtils.getURL("classpath:").getPath() + "static/upload/image/stadium";
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
        logger.info("Stadium imagePaths = " + imagePathsString + " 通过检查！");
        return false;
    }
}
