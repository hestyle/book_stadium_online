package cn.edu.hestyle.bookstadium.service.impl;

import cn.edu.hestyle.bookstadium.entity.Stadium;
import cn.edu.hestyle.bookstadium.entity.StadiumBook;
import cn.edu.hestyle.bookstadium.entity.StadiumManager;
import cn.edu.hestyle.bookstadium.mapper.StadiumBookMapper;
import cn.edu.hestyle.bookstadium.mapper.StadiumManagerMapper;
import cn.edu.hestyle.bookstadium.mapper.StadiumMapper;
import cn.edu.hestyle.bookstadium.service.IStadiumBookService;
import cn.edu.hestyle.bookstadium.service.exception.AddFailedException;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    public void stadiumManagerAdd(String stadiumManagerUsername, StadiumBook stadiumBook) throws AddFailedException {
        StadiumManager stadiumManager = null;
        try {
            stadiumManager = stadiumManagerMapper.findByUsername(stadiumManagerUsername);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumManager 查询失败，数据库发生未知异常！username = " + stadiumManagerUsername);
            throw new AddFailedException("添加失败，数据库发生未知异常！");
        }
        if (stadiumManager == null) {
            logger.warn("StadiumManager 查询失败，username = " + stadiumManagerUsername + "用户未注册！");
            throw new AddFailedException("添加失败，username = " + stadiumManagerUsername + "用户未注册！");
        }
        // 检查stadium是否存在
        if (stadiumBook.getStadiumId() == null) {
            logger.warn("StadiumBook 添加失败，未指定需要添加预约的体育场馆！stadiumBook = " + stadiumBook);
            throw new AddFailedException("添加失败，未指定需要添加预约的体育场馆id ！");
        }
        Stadium stadium = null;
        try {
            stadium = stadiumMapper.findById(stadiumBook.getStadiumId());
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Stadium 查询失败，数据库发生未知异常！stadiumId = " + stadiumBook.getStadiumId());
            throw new AddFailedException("添加失败，数据库发生未知异常！");
        }
        if (stadium == null) {
            logger.warn("StadiumBook 添加失败，指定的体育场馆不存在！stadiumBook = " + stadiumBook);
            throw new AddFailedException("添加失败，指体育场馆id = " + stadiumBook.getStadiumId() + "不存在！");
        }
        // 检查StadiumManager是否有权限添加Stadium的预约
        if (!stadiumManager.getId().equals(stadium.getManagerId())) {
            logger.warn("StadiumBook 添加失败，stadiumManager = " + stadiumManager + "，无权限添加体育场馆 Stadium = " + stadium + "的预约！");
            throw new AddFailedException("添加失败，体育场馆 " + stadium.getName() + " 不是您的账户创建的！");
        }
        // 检查字段
        if (stadiumBook.getStartTime() == null) {
            logger.warn("StadiumBook 添加失败，未指定场馆预约的时间段起始时间，StadiumBook = " + stadiumBook);
            throw new AddFailedException("添加失败，未指定体育场馆预约的时间段的起始时间！");
        }
        if (stadiumBook.getEndTime() == null) {
            logger.warn("StadiumBook 添加失败，未指定场馆预约的时间段末尾时间，StadiumBook = " + stadiumBook);
            throw new AddFailedException("添加失败，未指定体育场馆预约的时间段的末尾时间！");
        }
        if (stadiumBook.getStartTime().compareTo(stadiumBook.getEndTime()) >= 0) {
            logger.warn("StadiumBook 添加失败，场馆预约的时间段非法，StadiumBook = " + stadiumBook);
            throw new AddFailedException("添加失败，体育场馆预约的时间段的 末尾时间节点 在 起始时间节点 前面！");
        }
        if (stadiumBook.getStartTime().compareTo(new Date()) < 0) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
            logger.warn("StadiumBook 添加失败，场馆预约的时间段起始时间，StadiumBook = " + stadiumBook);
            throw new AddFailedException("添加失败，体育场馆预约的时间段的起始时间节点在当前时间" + dateFormat.format(new Date()) + "的前面！");
        }
        if (stadiumBook.getBookState() == null) {
            stadiumBook.setBookState(0);
        } else if (stadiumBook.getBookState() < 0 || stadiumBook.getBookState() > 3) {
            logger.warn("StadiumBook 添加失败，场馆预约状态非法，StadiumBook = " + stadiumBook);
            throw new AddFailedException("添加失败，体育场馆预约状态非法！");
        }
        if (stadiumBook.getMaxBookCount() == null || stadiumBook.getMaxBookCount() < 1) {
            logger.warn("StadiumBook 添加失败，场馆预约容量非法，StadiumBook = " + stadiumBook);
            throw new AddFailedException("添加失败，体育场馆预约容量，≥ 1 ！");
        }
        stadiumBook.setNowBookCount(0);
        stadiumBook.setIsDelete(0);
        stadiumBook.setCreatedUser(stadiumManager.getUsername());
        stadiumBook.setCreatedTime(new Date());
        try {
            stadiumBookMapper.add(stadiumBook);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumBook 添加失败，数据库发生未知异常！stadiumBook = " + stadiumBook);
            throw new AddFailedException("添加失败，数据库发生未知异常！");
        }
        logger.warn("StadiumBook 添加成功！stadiumBook = " + stadiumBook);
    }

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
