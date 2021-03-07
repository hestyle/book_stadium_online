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
import java.util.HashMap;
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
    public void stadiumManagerModify(String stadiumManagerUsername, HashMap<String, Object> modifyDataMap) throws ModifyFailedException {
        StadiumManager stadiumManager = null;
        try {
            stadiumManager = stadiumManagerMapper.findByUsername(stadiumManagerUsername);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumManager 查询失败，数据库发生未知异常！username = " + stadiumManagerUsername);
            throw new ModifyFailedException("添加失败，数据库发生未知异常！");
        }
        if (stadiumManager == null) {
            logger.warn("Stadium 修改失败，username = " + stadiumManagerUsername + "用户未注册！");
            throw new ModifyFailedException("修改失败，username = " + stadiumManagerUsername + "用户未注册！");
        }
        if (!modifyDataMap.containsKey("id")) {
            logger.warn("Stadium 修改失败，场馆id未填写！data = " + modifyDataMap);
            throw new ModifyFailedException("修改失败，未指定需要修改的场馆id！");
        }
        Integer id = (Integer) modifyDataMap.get("id");
        if (id == null) {
            logger.warn("Stadium 修改失败，场馆id未填写！data = " + modifyDataMap);
            throw new ModifyFailedException("修改失败，未指定需要修改的场馆id！");
        }
        // stadium是否存在
        Stadium stadium = null;
        try {
            stadium = stadiumMapper.findById(id);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Stadium 查询失败，数据库发生未知异常！data = " + modifyDataMap);
            throw new ModifyFailedException("修改失败，数据库发生未知异常！");
        }
        if (stadium == null) {
            logger.warn("Stadium 修改失败，不存在 id = " + id + "的体育场馆！");
            throw new ModifyFailedException("修改失败，不存在 id = " + id + "的体育场馆！");
        }
        // 检查该stadiumManager是否有权限修改stadium
        if (!stadiumManager.getId().equals(stadium.getManagerId())) {
            logger.warn("Stadium 修改失败，stadiumManagerId = " + stadiumManager.getId() + "没有体育场馆 data = " + stadium + " 修改权限！");
            throw new ModifyFailedException("修改失败，" + stadiumManagerUsername + " 无权限修改 id = " + id + "的体育场馆！");
        }
        // name
        if (modifyDataMap.containsKey("name")) {
            String name = (String) modifyDataMap.get("name");
            if (name == null || name.length() == 0) {
                logger.warn("Stadium 修改失败，场馆name不能设置为空！data = " + modifyDataMap);
                throw new ModifyFailedException("修改失败，体育场馆的名称不能设置为空！");
            }
            if (name.length() > 20) {
                logger.warn("Stadium 修改失败，场馆name长度超过20个字符！data = " + modifyDataMap);
                throw new ModifyFailedException("修改失败，体育场馆的名称不能超过20个字符！");
            }
            stadium.setName(name);
        }
        // address
        if (modifyDataMap.containsKey("address")) {
            String address = (String) modifyDataMap.get("address");
            if (address == null || address.length() == 0) {
                logger.warn("Stadium 修改失败，场馆address不能设置为空！data = " + modifyDataMap);
                throw new ModifyFailedException("修改失败，体育场馆的地址不能设置为空！");
            }
            if (address.length() > 200) {
                logger.warn("Stadium 修改失败，场馆address长度超过200个字符！data = " + modifyDataMap);
                throw new ModifyFailedException("修改失败，体育场馆的地址不能超过200个字符！");
            }
            stadium.setAddress(address);
        }
        // description
        if (modifyDataMap.containsKey("description")) {
            String description = (String) modifyDataMap.get("description");
            if (description != null && description.length() > 200) {
                logger.warn("Stadium 修改失败，场馆description长度超过200个字符！data = " + modifyDataMap);
                throw new ModifyFailedException("修改失败，体育场馆的描述不能超过200个字符！");
            }
            stadium.setDescription(description);
        }
        // isDelete
        if (modifyDataMap.containsKey("isDelete")) {
            Integer isDelete = (Integer) modifyDataMap.get("isDelete");
            if (isDelete == null || (!isDelete.equals(0) && !isDelete.equals(1))) {
                logger.warn("Stadium 修改失败，场馆删除状态异常！data = " + modifyDataMap);
                throw new ModifyFailedException("修改失败，体育场馆的删除状态设置异常！");
            }
            stadium.setIsDelete(isDelete);
        }
        // categoryIds
        if (modifyDataMap.containsKey("categoryIds")) {
            String categoryIds = (String) modifyDataMap.get("categoryIds");
            try {
                checkCategoryIds(categoryIds);
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("Stadium 添加失败，categoryIds列表格式错误！data = " + modifyDataMap);
                throw new ModifyFailedException("修改失败，场馆分类格式错误！");
            }
            stadium.setCategoryIds(categoryIds);
        }
        // imagePaths
        if (modifyDataMap.containsKey("imagePaths")) {
            String imagePaths = (String) modifyDataMap.get("imagePaths");
            try {
                checkImagePaths(imagePaths);
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("Stadium 添加失败，image path列表格式错误！data = " + modifyDataMap);
                throw new ModifyFailedException("修改失败，场馆图片格式错误！");
            }
            stadium.setImagePaths(imagePaths);
        }
        stadium.setModifiedUser(stadiumManagerUsername);
        stadium.setModifiedTime(new Date());
        try {
            stadiumMapper.update(stadium);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Stadium 修改失败，数据库发生未知异常！data = " + stadium);
            throw new ModifyFailedException("修改失败，数据库发生未知异常！");
        }
        logger.warn("Stadium 修改成功！data = " + stadium);
    }

    @Override
    @Transactional
    public void stadiumManagerDeleteByIds(String stadiumManagerUsername, List<Integer> stadiumIds) throws DeleteFailedException {
        StadiumManager stadiumManager = null;
        try {
            stadiumManager = stadiumManagerMapper.findByUsername(stadiumManagerUsername);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumManager 查询失败，数据库发生未知异常！username = " + stadiumManagerUsername);
            throw new DeleteFailedException("删除失败，数据库发生未知异常！");
        }
        if (stadiumManager == null) {
            logger.warn("Stadium 查询失败，username = " + stadiumManagerUsername + "用户未注册！");
            throw new DeleteFailedException("删除失败，username = " + stadiumManagerUsername + "用户未注册！");
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
            if (!stadium.getManagerId().equals(stadiumManager.getId())) {
                logger.warn("Stadium 删除失败，无删除权限 stadium = " + stadium + "\n\tStadiumManager = " + stadiumManager);
                logger.warn("Stadium 批量删除事务回滚==========");
                throw new DeleteFailedException("删除失败，不存在id = " + stadiumId + " 的体育场馆！");
            }
            stadium.setIsDelete(1);
            stadium.setModifiedUser(stadiumManagerUsername);
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
        if (imagePaths.length > 5) {
            throw new Exception("体育场馆最多上传5张照片！");
        }
        logger.info("Stadium imagePaths = " + imagePathsString + " 通过检查！");
        return false;
    }
}
