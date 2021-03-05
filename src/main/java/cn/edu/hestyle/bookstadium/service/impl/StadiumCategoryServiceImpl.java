package cn.edu.hestyle.bookstadium.service.impl;

import cn.edu.hestyle.bookstadium.entity.StadiumCategory;
import cn.edu.hestyle.bookstadium.mapper.StadiumCategoryMapper;
import cn.edu.hestyle.bookstadium.service.IStadiumCategoryService;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/4 3:04 下午
 */
@Service
public class StadiumCategoryServiceImpl implements IStadiumCategoryService {

    private static final Logger logger = LoggerFactory.getLogger(StadiumCategoryServiceImpl.class);

    @Resource
    private StadiumCategoryMapper stadiumCategoryMapper;

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
}
