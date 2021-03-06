package cn.edu.hestyle.bookstadium.service.impl;

import cn.edu.hestyle.bookstadium.entity.StadiumBook;
import cn.edu.hestyle.bookstadium.entity.StadiumManager;
import cn.edu.hestyle.bookstadium.mapper.StadiumBookMapper;
import cn.edu.hestyle.bookstadium.mapper.StadiumManagerMapper;
import cn.edu.hestyle.bookstadium.mapper.StadiumMapper;
import cn.edu.hestyle.bookstadium.service.IStadiumBookService;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/6 11:13 上午
 */
@Service
public class StadiumBookServiceImpl implements IStadiumBookService {

    private static final Logger logger = LoggerFactory.getLogger(StadiumBookServiceImpl.class);

    @Resource
    private StadiumMapper stadiumMapper;
    @Resource
    private StadiumManagerMapper stadiumManagerMapper;
    @Resource
    private StadiumBookMapper stadiumBookMapper;

    @Override
    public List<StadiumBook> stadiumManagerFindAllByPage(String stadiumManagerUsername, Integer pageIndex, Integer pageSize) throws FindFailedException {
        // 查询stadiumManager
        StadiumManager stadiumManager = null;
        try {
            stadiumManager = stadiumManagerMapper.findByUsername(stadiumManagerUsername);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumManager 查找失败，数据库发生未知异常！stadiumManagerUsername = " + stadiumManagerUsername);
            throw new FindFailedException("查询失败，数据库发生未知异常！");
        }
        if (stadiumManager == null) {
            logger.warn("StadiumManager 查找失败，用户名 stadiumManagerUsername = " + stadiumManagerUsername + " 未查找到账号！");
            throw new FindFailedException("查询失败，用户名 " + stadiumManagerUsername + " 未注册！");
        }
        // 检查页码是否合法
        if (pageIndex < 1) {
            throw new FindFailedException("查询失败，页码 " + pageIndex + " 非法，必须大于0！");
        }
        // 检查页大小是否合法
        if (pageSize < 1) {
            throw new FindFailedException("查询失败，页大小 " + pageSize + " 非法，必须大于0！");
        }
        List<StadiumBook> stadiumBookList = null;
        try {
            stadiumBookList = stadiumBookMapper.stadiumManagerFindAllByPage(stadiumManager.getId(), (pageIndex - 1) * pageSize, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumBook 查找失败，数据库发生未知异常！stadiumManagerId = " + stadiumManager.getId());
            throw new FindFailedException("查询失败，数据库发生未知异常！");
        }
        logger.warn("StadiumBook 查找成功！stadiumBookList = " + stadiumBookList);
        return stadiumBookList;
    }

    @Override
    public Integer stadiumManagerGetAllCount(String stadiumManagerUsername) throws FindFailedException {
        StadiumManager stadiumManager = null;
        try {
            stadiumManager = stadiumManagerMapper.findByUsername(stadiumManagerUsername);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumManager 查询失败，数据库发生未知异常！username = " + stadiumManagerUsername);
            throw new FindFailedException("查询失败，数据库发生未知异常！");
        }
        if (stadiumManager == null) {
            logger.warn("StadiumManager 查询失败，username = " + stadiumManagerUsername + "用户未注册！");
            throw new FindFailedException("查询失败，username = " + stadiumManagerUsername + "用户未注册！");
        }
        Integer count = 0;
        try {
            count = stadiumBookMapper.stadiumManagerGetAllCount(stadiumManager.getId());
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumBook 查询失败，数据库发生未知异常！ stadiumManagerId= " + stadiumManager.getId());
            throw new FindFailedException("查询失败，数据库发生未知异常！");
        }
        logger.warn("StadiumBook 查询成功， stadiumManagerId= " + stadiumManager.getId() + "，count = " + count);
        return count;
    }
}
