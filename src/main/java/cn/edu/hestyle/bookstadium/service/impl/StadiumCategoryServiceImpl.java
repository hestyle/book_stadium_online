package cn.edu.hestyle.bookstadium.service.impl;

import cn.edu.hestyle.bookstadium.entity.StadiumCategory;
import cn.edu.hestyle.bookstadium.entity.SystemManager;
import cn.edu.hestyle.bookstadium.mapper.StadiumCategoryMapper;
import cn.edu.hestyle.bookstadium.mapper.SystemManagerMapper;
import cn.edu.hestyle.bookstadium.service.IStadiumCategoryService;
import cn.edu.hestyle.bookstadium.service.exception.AddFailedException;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;
import cn.edu.hestyle.bookstadium.service.exception.ModifyFailedException;
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
 * @date 2021/3/4 3:04 下午
 */
@Service
public class StadiumCategoryServiceImpl implements IStadiumCategoryService {
    /** 场馆分类title最大长度 */
    private static final Integer STADIUM_CATEGORY_TITLE_MAX_LENGTH = 6;
    /** 场馆分类description最大长度 */
    private static final Integer STADIUM_CATEGORY_DESCRIPTION_MAX_LENGTH = 200;

    private static final Logger logger = LoggerFactory.getLogger(StadiumCategoryServiceImpl.class);

    @Resource
    private StadiumCategoryMapper stadiumCategoryMapper;
    @Resource
    private SystemManagerMapper systemManagerMapper;

    @Override
    public void add(Integer systemManagerId, StadiumCategory stadiumCategory) throws AddFailedException {
        SystemManager systemManager = null;
        try {
            systemManager = systemManagerMapper.findById(systemManagerId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SystemManager 查询失败，数据库发生未知异常！");
            throw new AddFailedException("添加失败，数据库发生未知异常!");
        }
        // 检查各个字段
        if (stadiumCategory.getTitle() == null || stadiumCategory.getTitle().length() == 0) {
            logger.warn("StadiumCategory 添加失败，未设置场馆分类title！StadiumCategory = " + stadiumCategory);
            throw new AddFailedException("添加失败，未设置场馆分类title!");
        }
        if (stadiumCategory.getTitle().length() > STADIUM_CATEGORY_TITLE_MAX_LENGTH) {
            logger.warn("StadiumCategory 添加失败，场馆分类title过长，超过" + STADIUM_CATEGORY_TITLE_MAX_LENGTH + "个字符！StadiumCategory = " + stadiumCategory);
            throw new AddFailedException("添加失败，场馆分类title过长，超过了" + STADIUM_CATEGORY_TITLE_MAX_LENGTH + "个字符!");
        }
        if (stadiumCategory.getDescription() == null || stadiumCategory.getDescription().length() == 0) {
            logger.warn("StadiumCategory 添加失败，未场馆分类 description ！stadiumCategory = " + stadiumCategory);
            throw new AddFailedException("添加失败，未设置场馆分类描述!");
        }
        if (stadiumCategory.getDescription().length() > STADIUM_CATEGORY_DESCRIPTION_MAX_LENGTH) {
            logger.warn("StadiumCategory 添加失败，场馆分类 description过长，超过" + STADIUM_CATEGORY_DESCRIPTION_MAX_LENGTH + "个字符！stadiumCategory = " + stadiumCategory);
            throw new AddFailedException("添加失败，场馆分类描述过长，超过" + STADIUM_CATEGORY_DESCRIPTION_MAX_LENGTH + "个字符!");
        }
        boolean isOk = false;
        try {
            isOk = this.checkStadiumCategoryImagePath(stadiumCategory.getImagePath());
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumCategory 添加失败，场馆分类图片路径非法，资源不在服务器上！stadiumCategory = " + stadiumCategory);
            throw new AddFailedException("添加失败，场馆分类图片路径非法，资源不在服务器上!");
        }
        if (!isOk) {
            logger.warn("StadiumCategory 添加失败，未填写场馆分类图片路径！stadiumCategory = " + stadiumCategory);
            throw new AddFailedException("添加失败，未上传场馆分类图片资源!");
        }
        stadiumCategory.setIsDelete(0);
        stadiumCategory.setCreatedUser(systemManager.getUsername());
        stadiumCategory.setCreatedTime(new Date());
        try {
            stadiumCategoryMapper.add(stadiumCategory);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumCategory 添加失败，数据库发生未知异常！");
            throw new AddFailedException("添加失败，数据库发生未知异常!");
        }
        logger.warn("StadiumCategory 添加成功！stadiumCategory = " + stadiumCategory);
    }

    @Override
    public void modify(Integer systemManagerId, StadiumCategory stadiumCategory) throws ModifyFailedException {
        SystemManager systemManager = null;
        try {
            systemManager = systemManagerMapper.findById(systemManagerId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SystemManager 查询失败，数据库发生未知异常！");
            throw new ModifyFailedException("修改失败，数据库发生未知异常!");
        }
        if (stadiumCategory == null || stadiumCategory.getId() == null) {
            logger.warn("StadiumCategory 修改失败，数据库发生未知异常！");
            throw new ModifyFailedException("修改失败，数据库发生未知异常!");
        }
        StadiumCategory modifyStadiumCategory = null;
        try {
            modifyStadiumCategory = stadiumCategoryMapper.findById(stadiumCategory.getId());
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumCategory 查询失败，数据库发生未知异常！");
            throw new ModifyFailedException("修改失败，数据库发生未知异常!");
        }
        // 检查各个字段
        if (stadiumCategory.getTitle() != null) {
            if (stadiumCategory.getTitle().length() == 0) {
                logger.warn("StadiumCategory 修改失败，场馆分类title不能设置为空！stadiumCategory = " + stadiumCategory);
                throw new ModifyFailedException("修改失败，场馆分类title不能设置为空!");
            }
            if (stadiumCategory.getTitle().length() > STADIUM_CATEGORY_TITLE_MAX_LENGTH) {
                logger.warn("StadiumCategory 修改失败，场馆分类title过长，超过" + STADIUM_CATEGORY_TITLE_MAX_LENGTH + "个字符！stadiumCategory = " + stadiumCategory);
                throw new ModifyFailedException("修改失败，场馆分类title过长，超过了" + STADIUM_CATEGORY_TITLE_MAX_LENGTH + "个字符!");
            }
            modifyStadiumCategory.setTitle(stadiumCategory.getTitle());
        }
        if (stadiumCategory.getDescription() != null) {
            if (stadiumCategory.getDescription().length() == 0) {
                logger.warn("StadiumCategory 修改失败，场馆分类description不能设置为空！StadiumCategory = " + stadiumCategory);
                throw new ModifyFailedException("修改失败，场馆分类description不能设置为空!");
            }
            if (stadiumCategory.getDescription().length() > STADIUM_CATEGORY_DESCRIPTION_MAX_LENGTH) {
                logger.warn("StadiumCategory 修改失败，场馆分类描述过长，超过" + STADIUM_CATEGORY_DESCRIPTION_MAX_LENGTH + "个字符！StadiumCategory = " + stadiumCategory);
                throw new ModifyFailedException("修改失败，场馆分类描述过长，超过了" + STADIUM_CATEGORY_DESCRIPTION_MAX_LENGTH + "个字符!");
            }
            modifyStadiumCategory.setDescription(stadiumCategory.getDescription());
        }
        if (stadiumCategory.getImagePath() != null) {
            boolean isOk = false;
            try {
                isOk = this.checkStadiumCategoryImagePath(stadiumCategory.getImagePath());
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("StadiumCategory 修改失败，场馆分类图片路径非法，资源不在服务器上！stadiumCategory = " + stadiumCategory);
                throw new ModifyFailedException("修改失败，场馆分类图片路径非法，资源不在服务器上!");
            }
            if (!isOk) {
                logger.warn("StadiumCategory 修改失败，未填写场馆分类图片路径！stadiumCategory = " + stadiumCategory);
                throw new ModifyFailedException("修改失败，场馆分类图片不能为空!");
            }
            modifyStadiumCategory.setImagePath(stadiumCategory.getImagePath());
        }
        stadiumCategory.setModifiedUser(systemManager.getUsername());
        stadiumCategory.setModifiedTime(new Date());
        try {
            stadiumCategoryMapper.update(modifyStadiumCategory);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumCategory 修改失败，数据库发生未知异常！");
            throw new ModifyFailedException("修改失败，数据库发生未知异常!");
        }
        logger.warn("StadiumCategory 修改成功！modifyStadiumCategory = " + modifyStadiumCategory);
    }

    @Override
    public StadiumCategory findById(Integer id) throws FindFailedException {
        if (id == null) {
            logger.info("StadiumCategory 查找失败，id=" + id);
            throw new FindFailedException("查找失败，未输入分类id！");
        }
        StadiumCategory stadiumCategory = null;
        try {
            stadiumCategory = stadiumCategoryMapper.findById(id);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumCategory 查找失败，数据库发生未知异常！id=" + id);
            throw new FindFailedException("查找失败，数据库发生未知异常！");
        }
        if (stadiumCategory == null) {
            logger.info("StadiumCategory 查找失败，id=" + id + ", 未找到对应的StadiumCategory！");
            throw new FindFailedException("查找失败，不存在id = " + id + " 的体育场馆分类！");
        }
        logger.info("StadiumCategory 查找成功，data = " + stadiumCategory);
        return stadiumCategory;
    }

    @Override
    public List<StadiumCategory> findByIds(String stadiumCategoryIds) throws FindFailedException {
        if (stadiumCategoryIds == null || stadiumCategoryIds.length() == 0) {
            logger.info("StadiumCategory 查找失败，ids=" + stadiumCategoryIds + ", 为空！");
            throw new FindFailedException("场馆分类查找失败，未输入体育场馆分类id！");
        }
        List<StadiumCategory> stadiumCategoryList = new ArrayList<StadiumCategory>();
        String[] ids = stadiumCategoryIds.split(",");
        for (String idString : ids) {
            if (idString == null || idString.length() == 0) {
                logger.info("StadiumCategory 查找失败，ids=" + stadiumCategoryIds + ", 格式错误！");
                throw new FindFailedException("查找失败，场馆分类列表格式错误！");
            }
            Integer id = null;
            try {
                // 各个id必须是正确的数字，且是有效的operation_id
                id = Integer.parseInt(idString);
            } catch (Exception e) {
                e.printStackTrace();
                logger.info("StadiumCategory 查找失败，ids=" + stadiumCategoryIds + ", 格式错误！");
                throw new FindFailedException("查找失败，场馆分类列表格式错误！");
            }
            // 检查id是否存在
            try {
                StadiumCategory stadiumCategory = stadiumCategoryMapper.findById(id);
                if (stadiumCategory != null) {
                    stadiumCategoryList.add(stadiumCategory);
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("StadiumCategory 查找失败，数据库发生未知错误！ids=" + stadiumCategoryIds);
                throw new FindFailedException("查找失败，数据库发生未知错误！");
            }
        }
        logger.info("StadiumCategory 查找成功，ids=" + stadiumCategoryIds + ", data = " + stadiumCategoryList);
        return stadiumCategoryList;
    }

    @Override
    public List<StadiumCategory> findByPage(Integer pageIndex, Integer pageSize) throws FindFailedException {
        // 检查页码是否合法
        if (pageIndex < 1) {
            logger.info("StadiumCategory查询失败，页码 " + pageIndex + " 非法，必须大于0！");
            throw new FindFailedException("查询失败，页码 " + pageIndex + " 非法，必须大于0！");
        }
        // 检查页大小是否合法
        if (pageSize < 1) {
            logger.info("StadiumCategory查询失败，页大小 " + pageSize + " 非法，必须大于0！");
            throw new FindFailedException("查询失败，页大小 " + pageSize + " 非法，必须大于0！");
        }
        List<StadiumCategory> stadiumCategoryList = null;
        try {
            stadiumCategoryList = stadiumCategoryMapper.findByPage((pageIndex - 1) * pageSize, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("StadiumCategory查询失败，数据库发生未知异常！");
            throw new FindFailedException("查询失败，数据库发生未知异常！");
        }
        logger.info("StadiumCategory查询成功，pageIndex = " + pageIndex + ", pageSize = " + pageSize + ", data = " + stadiumCategoryList);
        return stadiumCategoryList;
    }

    @Override
    public Integer getAllCount() throws FindFailedException {
        Integer count = 0;
        try {
            count = stadiumCategoryMapper.getAllCount();
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("StadiumCategory查询失败，数据库发生未知异常！");
            throw new FindFailedException("查询失败，数据库发生未知异常！");
        }
        return count;
    }

    /**
     * 检查BannerImagePath的合法性
     * @param imagePath             imagePath
     * @return                      是否合法
     * @throws Exception            image path异常
     */
    private boolean checkStadiumCategoryImagePath(String imagePath) throws Exception {
        if (imagePath == null || imagePath.length() == 0) {
            return false;
        }
        try {
            String pathNameTemp = ResourceUtils.getURL("classpath:").getPath() + "static/upload/image/stadiumCategory";
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
        logger.info("StadiumCategory imagePath = " + imagePath + " 通过检查！");
        return true;
    }
}
