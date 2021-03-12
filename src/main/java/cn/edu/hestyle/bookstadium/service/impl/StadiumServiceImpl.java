package cn.edu.hestyle.bookstadium.service.impl;

import cn.edu.hestyle.bookstadium.entity.Stadium;
import cn.edu.hestyle.bookstadium.entity.StadiumCategory;
import cn.edu.hestyle.bookstadium.entity.StadiumManager;
import cn.edu.hestyle.bookstadium.mapper.StadiumCategoryMapper;
import cn.edu.hestyle.bookstadium.mapper.StadiumManagerMapper;
import cn.edu.hestyle.bookstadium.mapper.StadiumMapper;
import cn.edu.hestyle.bookstadium.service.IStadiumService;
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
import java.util.Date;
import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/4 10:02 上午
 */
@Service
public class StadiumServiceImpl implements IStadiumService {
    /** 体育场馆name的最大长度 */
    private static final Integer STADIUM_NAME_MAX_LENGTH = 20;
    /** 体育场馆address的最大长度 */
    private static final Integer STADIUM_ADDRESS_MAX_LENGTH = 200;
    /** 体育场馆description的最大长度 */
    private static final Integer STADIUM_DESCRIPTION_MAX_LENGTH = 200;

    private static final Logger logger = LoggerFactory.getLogger(StadiumServiceImpl.class);

    @Resource
    private StadiumMapper stadiumMapper;
    @Resource
    private StadiumCategoryMapper stadiumCategoryMapper;
    @Resource
    private StadiumManagerMapper stadiumManagerMapper;

    @Override
    public void add(Integer stadiumManagerId, Stadium stadium) throws AddFailedException {
        StadiumManager stadiumManager = null;
        try {
            stadiumManager = stadiumManagerMapper.findById(stadiumManagerId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumManager 查询失败，数据库发生未知异常！stadiumManagerId = " + stadiumManagerId);
            throw new AddFailedException("添加失败，数据库发生未知异常！");
        }
        stadium.setStadiumManagerId(stadiumManager.getId());
        // 检查name
        if (stadium.getName() == null || stadium.getName().length() == 0) {
            logger.warn("Stadium 添加失败，场馆name不能设置为空！data = " + stadium);
            throw new AddFailedException("添加失败，体育场馆的名称不能设置为空！");
        }
        if (stadium.getName().length() > STADIUM_NAME_MAX_LENGTH) {
            logger.warn("Stadium 添加失败，场馆name长度超过" + STADIUM_NAME_MAX_LENGTH + "个字符！data = " + stadium);
            throw new AddFailedException("添加失败，体育场馆的名称不能超过" + STADIUM_NAME_MAX_LENGTH + "个字符！");
        }
        // 检查address
        if (stadium.getAddress() == null || stadium.getAddress().length() == 0) {
            logger.warn("Stadium 添加失败，场馆address不能设置为空！data = " + stadium);
            throw new AddFailedException("添加失败，体育场馆的地址不能设置为空！");
        }
        if (stadium.getAddress().length() > STADIUM_ADDRESS_MAX_LENGTH) {
            logger.warn("Stadium 添加失败，场馆address长度超过" + STADIUM_ADDRESS_MAX_LENGTH + "个字符！data = " + stadium);
            throw new AddFailedException("添加失败，体育场馆的地址不能超过" + STADIUM_ADDRESS_MAX_LENGTH + "个字符！");
        }
        // 检查description
        if (stadium.getDescription() == null || stadium.getDescription().length() == 0) {
            logger.warn("Stadium 添加失败，场馆description不能设置为空！data = " + stadium);
            throw new AddFailedException("添加失败，体育场馆的描述不能设置为空！");
        }
        if (stadium.getDescription().length() > STADIUM_DESCRIPTION_MAX_LENGTH) {
            logger.warn("Stadium 修改失败，场馆description长度超过" + STADIUM_DESCRIPTION_MAX_LENGTH + "个字符！data = " + stadium);
            throw new ModifyFailedException("修改失败，体育场馆的描述不能超过" +  + STADIUM_DESCRIPTION_MAX_LENGTH + "个字符！");
        }
        // 检查categoryId
        if (stadium.getStadiumCategoryId() == null) {
            logger.warn("Stadium 添加失败，未设置场馆所属分类！data = " + stadium);
            throw new AddFailedException("添加失败，未设置场馆所属分类！");
        }
        StadiumCategory stadiumCategory = null;
        try {
            stadiumCategory = stadiumCategoryMapper.findById(stadium.getStadiumCategoryId());
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumCategory 查询失败，数据库发生未知错误！data = " + stadium);
            throw new AddFailedException("添加失败，数据库发生未知错误！");
        }
        if (stadiumCategory == null) {
            logger.warn("Stadium 添加失败，不存在 id = " + stadium.getStadiumCategoryId() + "的体育场馆！data = " + stadium);
            throw new AddFailedException("添加失败，不存在 id = " + stadium.getStadiumCategoryId() + "的体育场馆！");
        }
        // 检查imagePaths
        try {
            checkImagePaths(stadium.getImagePaths());
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Stadium 添加失败，image path列表格式错误！data = " + stadium);
            throw new AddFailedException("添加失败，场馆图片格式错误！");
        }
        stadium.setCreatedUser(stadiumManager.getUsername());
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
    public Stadium findById(Integer stadiumId) throws FindFailedException {
        if (stadiumId == null) {
            logger.warn("Stadium 查询失败，未指定需要查询的场馆id！");
            throw new FindFailedException("查询失败，未指定需要查询的场馆id！");
        }
        Stadium stadium = null;
        try {
            stadium = stadiumMapper.findById(stadiumId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Stadium 查询失败，数据库发生未知异常！stadiumId = " + stadiumId);
            throw new FindFailedException("查询失败，数据库发生未知异常！");
        }
        logger.warn("Stadium 查询成功！stadium = " + stadium);
        return stadium;
    }

    @Override
    public void stadiumManagerModify(Integer stadiumManagerId, Stadium stadium) throws ModifyFailedException {
        StadiumManager stadiumManager = null;
        try {
            stadiumManager = stadiumManagerMapper.findById(stadiumManagerId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumManager 查询失败，数据库发生未知异常！stadiumManagerId = " + stadiumManagerId);
            throw new ModifyFailedException("添加失败，数据库发生未知异常！");
        }
        if (stadium == null || stadium.getId() == null) {
            logger.warn("Stadium 修改失败，未指定需要修改的体育场馆！data = " + stadium);
            throw new ModifyFailedException("修改失败，未指定需要修改的体育场馆！");
        }
        // stadium是否存在
        Stadium modifyStadium = null;
        try {
            modifyStadium = stadiumMapper.findById(stadium.getId());
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Stadium 查询失败，数据库发生未知异常！data = " + stadium);
            throw new ModifyFailedException("修改失败，数据库发生未知异常！");
        }
        if (modifyStadium == null) {
            logger.warn("Stadium 修改失败，不存在 id = " + stadium.getId() + "的体育场馆！");
            throw new ModifyFailedException("修改失败，不存在 id = " + stadium.getId() + "的体育场馆！");
        }
        // 检查该stadiumManager是否有权限修改stadium
        if (!stadiumManager.getId().equals(modifyStadium.getStadiumManagerId())) {
            logger.warn("Stadium 修改失败，stadiumManagerId = " + stadiumManager.getId() + "没有体育场馆 data = " + stadium + " 修改权限！");
            throw new ModifyFailedException("修改失败，" + stadiumManager.getUsername() + " 无权限修改 id = " + stadium.getId() + "的体育场馆！");
        }
        // name
        if (stadium.getName() != null) {
            if (stadium.getName().length() == 0) {
                logger.warn("Stadium 修改失败，场馆name不能设置为空！data = " + stadium);
                throw new ModifyFailedException("修改失败，体育场馆的名称不能设置为空！");
            }
            if (stadium.getName().length() > STADIUM_NAME_MAX_LENGTH) {
                logger.warn("Stadium 修改失败，场馆name长度超过" + STADIUM_NAME_MAX_LENGTH + "个字符！data = " + stadium);
                throw new ModifyFailedException("修改失败，体育场馆的名称不能超过" + STADIUM_NAME_MAX_LENGTH + "个字符！");
            }
            modifyStadium.setName(stadium.getName());
        }
        // address
        if (stadium.getAddress() != null) {
            if (stadium.getAddress().length() == 0) {
                logger.warn("Stadium 修改失败，场馆address不能设置为空！data = " + stadium);
                throw new ModifyFailedException("修改失败，体育场馆的地址不能设置为空！");
            }
            if (stadium.getAddress().length() > STADIUM_ADDRESS_MAX_LENGTH) {
                logger.warn("Stadium 修改失败，场馆address长度超过" + STADIUM_ADDRESS_MAX_LENGTH + "个字符！data = " + stadium);
                throw new ModifyFailedException("修改失败，体育场馆的地址不能超过" + STADIUM_ADDRESS_MAX_LENGTH + "个字符！");
            }
            modifyStadium.setAddress(stadium.getAddress());
        }
        // description
        if (stadium.getDescription() != null) {
            if (stadium.getDescription() == null || stadium.getDescription().length() == 0) {
                logger.warn("Stadium 修改失败，场馆description不能设置为空！data = " + stadium);
                throw new ModifyFailedException("修改失败，体育场馆的描述不能设置为空！");
            }
            if (stadium.getDescription().length() > STADIUM_DESCRIPTION_MAX_LENGTH) {
                logger.warn("Stadium 修改失败，场馆description长度超过" + STADIUM_DESCRIPTION_MAX_LENGTH + "个字符！data = " + stadium);
                throw new ModifyFailedException("修改失败，体育场馆的描述不能超过" +  + STADIUM_DESCRIPTION_MAX_LENGTH + "个字符！");
            }
            modifyStadium.setDescription(stadium.getDescription());
        }
        // categoryIds
        if (stadium.getStadiumCategoryId() != null) {
            StadiumCategory stadiumCategory = null;
            try {
                stadiumCategory = stadiumCategoryMapper.findById(stadium.getStadiumCategoryId());
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("StadiumCategory 查询失败，数据库发生未知错误！data = " + stadium);
                throw new ModifyFailedException("修改失败，数据库发生未知错误！");
            }
            if (stadiumCategory == null) {
                logger.warn("Stadium 添加失败，不存在 id = " + stadium.getStadiumCategoryId() + "的体育场馆！data = " + stadium);
                throw new ModifyFailedException("修改失败，不存在 id = " + stadium.getStadiumCategoryId() + "的体育场馆！");
            }
            modifyStadium.setStadiumCategoryId(stadium.getStadiumCategoryId());
        }
        // imagePaths
        if (stadium.getImagePaths() != null) {
            try {
                checkImagePaths(stadium.getImagePaths());
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("Stadium 添加失败，image path列表格式错误！data = " + stadium);
                throw new ModifyFailedException("修改失败，场馆图片格式错误！");
            }
            modifyStadium.setImagePaths(stadium.getImagePaths());
        }
        modifyStadium.setModifiedUser(stadiumManager.getUsername());
        modifyStadium.setModifiedTime(new Date());
        try {
            stadiumMapper.update(modifyStadium);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Stadium 修改失败，数据库发生未知异常！modifyStadium = " + modifyStadium);
            throw new ModifyFailedException("修改失败，数据库发生未知异常！");
        }
        logger.warn("Stadium 修改成功！modifyStadium = " + modifyStadium);
    }

    @Override
    @Transactional
    public void stadiumManagerDeleteByIds(Integer stadiumManagerId, List<Integer> stadiumIds) throws DeleteFailedException {
        StadiumManager stadiumManager = null;
        try {
            stadiumManager = stadiumManagerMapper.findById(stadiumManagerId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumManager 查询失败，数据库发生未知异常！stadiumManagerId = " + stadiumManagerId);
            throw new DeleteFailedException("删除失败，数据库发生未知异常！");
        }
        if (stadiumIds == null || stadiumIds.size() == 0) {
            logger.warn("Stadium 删除失败，data = " + stadiumIds + " 未指定需要删除的Stadium id！");
            throw new DeleteFailedException("删除失败，未指定需要删除的场馆！");
        }
        logger.warn("Stadium 批量删除事务开启==========");
        for (Integer stadiumId : stadiumIds) {
            Stadium stadium = null;
            try {
                stadium = stadiumMapper.findById(stadiumId);
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("Stadium 查询失败，数据库发生未知异常！stadiumId = " + stadiumId);
                logger.warn("Stadium 批量删除事务回滚==========");
                throw new DeleteFailedException("删除失败，数据库发生未知异常！");
            }
            if (stadium == null) {
                logger.warn("Stadium 删除失败，不存在体育场馆 stadiumId = " + stadiumId);
                logger.warn("Stadium 批量删除事务回滚==========");
                throw new DeleteFailedException("删除失败，不存在id = " + stadiumId + " 的体育场馆！");
            }
            if (!stadium.getStadiumManagerId().equals(stadiumManager.getId())) {
                logger.warn("Stadium 删除失败，无删除权限 stadium = " + stadium + "\n\tStadiumManager = " + stadiumManager);
                logger.warn("Stadium 批量删除事务回滚==========");
                throw new DeleteFailedException("删除失败，不存在id = " + stadiumId + " 的体育场馆！");
            }
            stadium.setIsDelete(1);
            stadium.setModifiedUser(stadiumManager.getUsername());
            stadium.setModifiedTime(new Date());
            try {
                stadiumMapper.update(stadium);
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("Stadium 删除失败，数据库发生未知异常！stadium = " + stadium);
                logger.warn("Stadium 批量删除事务回滚==========");
                throw new DeleteFailedException("删除失败，数据库发生未知异常！");
            }
            logger.info("Stadium 完成体育场馆更新！ stadium = " + stadium);
        }
        logger.info("Stadium 成功删除体育场馆！stadiumIds = " + stadiumIds);
        logger.warn("Stadium 批量删除事务提交==========");
    }

    @Override
    public List<Stadium> stadiumManagerFindByPage(Integer stadiumManagerId, Integer pageIndex, Integer pageSize) throws FindFailedException {
        StadiumManager stadiumManager = null;
        try {
            stadiumManager = stadiumManagerMapper.findById(stadiumManagerId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumManager 查询失败，数据库发生未知异常！stadiumManagerId = " + stadiumManagerId);
            throw new FindFailedException("查询失败，数据库发生未知异常！");
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
    public Integer stadiumManagerGetCount(Integer stadiumManagerId) throws FindFailedException {
        StadiumManager stadiumManager = null;
        try {
            stadiumManager = stadiumManagerMapper.findById(stadiumManagerId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumManager 查询失败，数据库发生未知异常！stadiumManagerId = " + stadiumManagerId);
            throw new FindFailedException("查询失败，数据库发生未知异常！");
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
        if (imagePaths.length > 5) {
            throw new Exception("体育场馆最多上传5张照片！");
        }
        logger.info("Stadium imagePaths = " + imagePathsString + " 通过检查！");
        return false;
    }
}
