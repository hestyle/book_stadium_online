package cn.edu.hestyle.bookstadium.service.impl;

import cn.edu.hestyle.bookstadium.entity.*;
import cn.edu.hestyle.bookstadium.mapper.*;
import cn.edu.hestyle.bookstadium.service.IStadiumService;
import cn.edu.hestyle.bookstadium.service.exception.AddFailedException;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;
import cn.edu.hestyle.bookstadium.service.exception.ModifyFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import javax.annotation.Resource;
import java.io.File;
import java.text.SimpleDateFormat;
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
    /** 回复场馆删除通知体育场馆管理员title、content */
    private static final String STADIUM_DELETE_STADIUM_MANAGER_TITLE = "体育场馆删除";
    private static final String STADIUM_DELETE_STADIUM_MANAGER_CONTENT = "系统管理员【%s】,因【%s】, 删除了你所创建的【%s】场馆，已经预约用户已被取消！";
    /** 回复场馆评论删除通知体育场馆管理员title、content */
    private static final String STADIUM_BOOK_DELETE_STADIUM_MANAGER_TITLE = "场馆预约删除";
    private static final String STADIUM_BOOK_DELETE_STADIUM_MANAGER_CONTENT = "系统管理员【%s】,因【%s】, 删除了你所创建的【%s】场馆的【%s-%s】预约场次，已经预约用户已被取消！";
    /** 回复场馆删除通知用户的title、content */
    private static final String STADIUM_BOOK_ITEM_DELETE_USER_TITLE = "体育场馆删除";
    private static final String STADIUM_BOOK_ITEM_DELETE_USER_CONTENT = "系统管理员【%s】,因【%s】, 删除了【%s】场馆，你的【%s-%s】预约场次已被取消！";

    private static final Logger logger = LoggerFactory.getLogger(StadiumServiceImpl.class);

    @Resource
    private NoticeMapper noticeMapper;
    @Resource
    private StadiumMapper stadiumMapper;
    @Resource
    private StadiumBookMapper stadiumBookMapper;
    @Resource
    private StadiumBookItemMapper stadiumBookItemMapper;
    @Resource
    private StadiumCategoryMapper stadiumCategoryMapper;
    @Resource
    private SystemManagerMapper systemManagerMapper;
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
    public List<Stadium> findByNameKeyAndPage(String nameKey, Integer pageIndex, Integer pageSize) throws FindFailedException {
        // 去除nameKey中的特殊字符
        if (nameKey != null && nameKey.length() != 0) {
            nameKey = nameKey.replaceAll("%", "").replaceAll("'", "").replaceAll("\\?", "");
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
            stadiumList = stadiumMapper.findByNameKeyAndPage(nameKey, (pageIndex - 1) * pageSize, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Stadium 查询失败，数据库发生未知异常！nameKey = " + nameKey);
            throw new FindFailedException("查询失败，数据库发生未知异常！");
        }
        logger.warn("Stadium 查询成功！nameKey = " + nameKey + ", stadiumList = " + stadiumList);
        return stadiumList;
    }

    @Override
    public Integer getCount(String nameKey) throws FindFailedException {
        // 去除nameKey中的特殊字符
        if (nameKey != null && nameKey.length() != 0) {
            nameKey = nameKey.replaceAll("%", "").replaceAll("'", "").replaceAll("\\?", "");
        }
        Integer count = 0;
        try {
            count = stadiumMapper.getCount(nameKey);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Stadium 查询失败，数据库发生未知异常！ nameKey= " + nameKey);
            throw new FindFailedException("查询失败，数据库发生未知异常！");
        }
        logger.warn("Stadium 查询成功， nameKey= " + nameKey + "，count = " + count);
        return count;
    }

    @Override
    public List<Stadium> findByPage(Integer pageIndex, Integer pageSize) throws FindFailedException {
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
            stadiumList = stadiumMapper.findByPage((pageIndex - 1) * pageSize, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Stadium 查询失败，数据库发生未知异常！ pageIndex= " + pageIndex + "，pageSize=" + pageSize);
            throw new FindFailedException("查询失败，数据库发生未知异常！");
        }
        logger.warn("Stadium 查询成功， pageIndex= " + pageIndex + "，pageSize=" + pageSize + "，data = " + stadiumList);
        return stadiumList;
    }

    @Override
    public List<Stadium> findByStadiumCategoryId(Integer stadiumCategoryId, Integer pageIndex, Integer pageSize) throws FindFailedException {
        if (stadiumCategoryId == null) {
            logger.warn("Stadium 查询失败，未指定需要查询的场馆分类ID！ stadiumCategoryId= " + stadiumCategoryId);
            throw new FindFailedException("查询失败，未指定需要查询的场馆分类ID！");
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
            stadiumList = stadiumMapper.findByStadiumCategoryId(stadiumCategoryId,(pageIndex - 1) * pageSize, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Stadium 查询失败，数据库发生未知异常！ stadiumCategoryId= " + stadiumCategoryId);
            throw new FindFailedException("查询失败，数据库发生未知异常！");
        }
        logger.warn("Stadium 查询成功， stadiumCategoryId= " + stadiumCategoryId + "，data = " + stadiumList);
        return stadiumList;
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
    public List<Stadium> stadiumManagerFindByPage(Integer stadiumManagerId, Integer pageIndex, Integer pageSize, String nameKey) throws FindFailedException {
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
        // 去除nameKey中的特殊字符
        if (nameKey != null && nameKey.length() != 0) {
            nameKey = nameKey.replaceAll("%", "").replaceAll("'", "").replaceAll("\\?", "");
        }
        List<Stadium> stadiumList = null;
        try {
            stadiumList = stadiumMapper.stadiumManagerFindByPage(stadiumManager.getId(),(pageIndex - 1) * pageSize, pageSize, nameKey);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Stadium 查询失败，数据库发生未知异常！ stadiumManagerId= " + stadiumManager.getId());
            throw new FindFailedException("查询失败，数据库发生未知异常！");
        }
        logger.warn("Stadium 查询成功， stadiumManagerId= " + stadiumManager.getId() + "，data = " + stadiumList);
        return stadiumList;
    }

    @Override
    public Integer stadiumManagerGetCount(Integer stadiumManagerId, String nameKey) throws FindFailedException {
        StadiumManager stadiumManager = null;
        try {
            stadiumManager = stadiumManagerMapper.findById(stadiumManagerId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumManager 查询失败，数据库发生未知异常！stadiumManagerId = " + stadiumManagerId);
            throw new FindFailedException("查询失败，数据库发生未知异常！");
        }
        // 去除nameKey中的特殊字符
        if (nameKey != null && nameKey.length() != 0) {
            nameKey = nameKey.replaceAll("%", "").replaceAll("'", "").replaceAll("\\?", "");
        }
        Integer count = 0;
        try {
            count = stadiumMapper.stadiumManagerGetCount(stadiumManager.getId(), nameKey);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Stadium 查询失败，数据库发生未知异常！ stadiumManagerId= " + stadiumManager.getId());
            throw new FindFailedException("查询失败，数据库发生未知异常！");
        }
        logger.warn("Stadium 查询成功， stadiumManagerId= " + stadiumManager.getId() + "，count = " + count);
        return count;
    }

    @Override
    @Transactional
    public void systemManagerDeleteById(Integer systemManagerId, Integer stadiumId, String deleteReason) {
        if (stadiumId == null) {
            logger.warn("Stadium 删除失败！未指定stadiumId！");
            throw new FindFailedException("操作失败，未指定需要删除的场馆ID！");
        }
        Stadium stadium = null;
        try {
            stadium = stadiumMapper.findById(stadiumId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Stadium 查询失败！数据库发生未知异常！stadiumId = " + stadiumId);
            throw new FindFailedException("操作失败，数据库发生未知异常！");
        }
        if (stadium == null || (stadium.getIsDelete() != null && stadium.getIsDelete() == 1)) {
            logger.warn("Stadium 删除失败！该stadium不存在或已被删除！stadium = " + stadium);
            throw new FindFailedException("操作失败，该stadium不存在或已被删除！");
        }
        if (deleteReason == null || deleteReason.length() == 0) {
            logger.warn("Stadium 删除失败！未填写删除原因！stadium = " + stadium);
            throw new FindFailedException("操作失败，未填写删除原因！");
        }
        SystemManager systemManager = null;
        try {
            systemManager = systemManagerMapper.findById(systemManagerId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SystemManager 查找失败！数据库发生未知错误！systemManagerId = " + systemManagerId);
            throw new FindFailedException("操作失败，数据库发生未知错误！");
        }
        // 1、更新stadium为删除状态
        stadium.setIsDelete(1);
        stadium.setModifiedUser(systemManager.getUsername());
        stadium.setModifiedTime(new Date());
        try {
            stadiumMapper.update(stadium);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Stadium 更新失败！数据库发生未知异常！stadium = " + stadium);
            throw new FindFailedException("操作失败，数据库发生未知异常！");
        }
        // 2、通知体育场馆管理员
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Notice stadiumDeleteNotice = new Notice();
        stadiumDeleteNotice.setToAccountType(Notice.TO_ACCOUNT_STADIUM_MANAGER);
        stadiumDeleteNotice.setAccountId(stadium.getStadiumManagerId());
        stadiumDeleteNotice.setTitle(STADIUM_DELETE_STADIUM_MANAGER_TITLE);
        stadiumDeleteNotice.setContent(String.format(STADIUM_DELETE_STADIUM_MANAGER_CONTENT, systemManager.getUsername(), deleteReason, stadium.getName()));
        stadiumDeleteNotice.setGeneratedTime(new Date());
        stadiumDeleteNotice.setIsDelete(0);
        try {
            noticeMapper.add(stadiumDeleteNotice);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Notice 添加失败，数据库发生未知错误！stadiumDeleteNotice = " + stadiumDeleteNotice);
            throw new AddFailedException("回复失败，数据库发生未知错误！");
        }
        logger.warn("Notice 添加成功！managerNotice = " + stadiumDeleteNotice);
        // 3、取消已经预约了该场次的用户预约
        Integer stadiumBookBeginIndex = 0;
        Integer pageSize = 10;
        Integer stadiumBookCount = 0;
        try {
            stadiumBookCount = stadiumBookMapper.systemManagerGetCountById(stadiumId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumBook 查找失败！数据库发生未知异常！stadiumId = " + stadiumId);
            throw new FindFailedException("操作失败，数据库发生未知异常！");
        }
        // 分页查询该Stadium的所有StadiumBook
        while (stadiumBookBeginIndex < stadiumBookCount) {
            List<StadiumBook> stadiumBookList = null;
            try {
                stadiumBookList = stadiumBookMapper.systemManagerFindByStadiumIdAndPage(stadiumId, stadiumBookBeginIndex, pageSize);
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("StadiumBook 查找失败！数据库发生未知异常！stadiumId = " + stadiumId + ", stadiumBookBeginIndex = " + stadiumBookBeginIndex + ", pageSize" + pageSize);
                throw new FindFailedException("操作失败，数据库发生未知异常！");
            }
            if (stadiumBookList != null && stadiumBookList.size() != 0) {
                for (StadiumBook stadiumBook : stadiumBookList) {
                    if (stadiumBook.getIsDelete() != null && stadiumBook.getIsDelete() != 0) {
                        continue;
                    }
                    // 更新stadiumBookItem为删除状态
                    stadiumBook.setIsDelete(1);
                    stadiumBook.setModifiedUser(systemManager.getUsername());
                    stadiumBook.setModifiedTime(new Date());
                    try {
                        stadiumBookMapper.update(stadiumBook);
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.warn("StadiumBook 更新失败！数据库发生未知异常！stadiumBook = " + stadiumBook);
                        throw new FindFailedException("操作失败，数据库发生未知异常！");
                    }
                    // 2、通知体育场馆管理员
                    Notice stadiumBookDeleteNotice = new Notice();
                    stadiumBookDeleteNotice.setToAccountType(Notice.TO_ACCOUNT_STADIUM_MANAGER);
                    stadiumBookDeleteNotice.setAccountId(stadium.getStadiumManagerId());
                    stadiumBookDeleteNotice.setTitle(STADIUM_BOOK_DELETE_STADIUM_MANAGER_TITLE);
                    stadiumBookDeleteNotice.setContent(String.format(STADIUM_BOOK_DELETE_STADIUM_MANAGER_CONTENT, systemManager.getUsername(), deleteReason, stadium.getName(), simpleDateFormat.format(stadiumBook.getStartTime()), simpleDateFormat.format(stadiumBook.getEndTime())));
                    stadiumBookDeleteNotice.setGeneratedTime(new Date());
                    stadiumBookDeleteNotice.setIsDelete(0);
                    try {
                        noticeMapper.add(stadiumBookDeleteNotice);
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.warn("Notice 添加失败，数据库发生未知错误！stadiumBookDeleteNotice = " + stadiumBookDeleteNotice);
                        throw new AddFailedException("回复失败，数据库发生未知错误！");
                    }
                    logger.warn("Notice 添加成功！stadiumBookDeleteNotice = " + stadiumBookDeleteNotice);
                    // 3、取消已经预约了该场次的用户预约
                    Integer stadiumBookItemBeginIndex = 0;
                    Integer stadiumBookItemCount = 0;
                    try {
                        stadiumBookItemCount = stadiumBookItemMapper.getCountByStadiumBookId(stadiumBook.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.warn("StadiumBookItem 查找失败！数据库发生未知异常！stadiumBookId = " + stadiumBook.getId());
                        throw new FindFailedException("操作失败，数据库发生未知异常！");
                    }
                    // 分页查询该StadiumBook的所有StadiumBookItem
                    while (stadiumBookItemBeginIndex < stadiumBookItemCount) {
                        List<StadiumBookItem> stadiumBookItemList = null;
                        try {
                            stadiumBookItemList = stadiumBookItemMapper.findByStadiumBookIdAndPage(stadiumBook.getId(), stadiumBookItemBeginIndex, pageSize);
                        } catch (Exception e) {
                            e.printStackTrace();
                            logger.warn("StadiumBookItem 查找失败！数据库发生未知异常！stadiumBookId = " + stadiumBook.getId() + ",beginIndex = " + stadiumBookItemBeginIndex + ", pageSize = " + pageSize);
                            throw new FindFailedException("操作失败，数据库发生未知异常！");
                        }
                        if (stadiumBookItemList != null && stadiumBookItemList.size() != 0) {
                            for (StadiumBookItem stadiumBookItem : stadiumBookItemList) {
                                if (stadiumBookItem.getIsDelete() != null && stadiumBookItem.getIsDelete() != 0) {
                                    continue;
                                }
                                // 更新stadiumBookItem为删除状态
                                stadiumBookItem.setIsDelete(1);
                                try {
                                    stadiumBookItemMapper.update(stadiumBookItem);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    logger.warn("StadiumBookItem 查找失败！数据库发生未知异常！stadiumBookItem = " + stadiumBookItem);
                                    throw new FindFailedException("操作失败，数据库发生未知异常！");
                                }
                                // 通知对应的user
                                Notice notice = new Notice();
                                notice.setToAccountType(Notice.TO_ACCOUNT_USER);
                                notice.setAccountId(stadiumBookItem.getUserId());
                                notice.setTitle(STADIUM_BOOK_ITEM_DELETE_USER_TITLE);
                                notice.setContent(String.format(STADIUM_BOOK_ITEM_DELETE_USER_CONTENT, systemManager.getUsername(), deleteReason, stadium.getName(), simpleDateFormat.format(stadiumBook.getStartTime()), simpleDateFormat.format(stadiumBook.getEndTime())));
                                notice.setGeneratedTime(new Date());
                                notice.setIsDelete(0);
                                try {
                                    noticeMapper.add(notice);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    logger.warn("Notice 添加失败，数据库发生未知错误！notice = " + notice);
                                    throw new AddFailedException("回复失败，数据库发生未知错误！");
                                }
                                logger.warn("Notice 添加成功！notice = " + notice);
                            }
                        }
                        stadiumBookItemBeginIndex += pageSize;
                    }
                }
            }
            stadiumBookBeginIndex += pageSize;
        }
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
