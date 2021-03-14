package cn.edu.hestyle.bookstadium.service.impl;

import cn.edu.hestyle.bookstadium.entity.StadiumBook;
import cn.edu.hestyle.bookstadium.entity.StadiumBookItem;
import cn.edu.hestyle.bookstadium.mapper.StadiumBookItemMapper;
import cn.edu.hestyle.bookstadium.mapper.StadiumBookMapper;
import cn.edu.hestyle.bookstadium.service.IStadiumBookItemService;
import cn.edu.hestyle.bookstadium.service.exception.AddFailedException;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/14 5:26 下午
 */
@Service
public class StadiumBookItemServiceImpl implements IStadiumBookItemService {
    private static final Logger logger = LoggerFactory.getLogger(StadiumBookItemServiceImpl.class);

    @Resource
    private StadiumBookMapper stadiumBookMapper;
    @Resource
    private StadiumBookItemMapper stadiumBookItemMapper;

    @Override
    @Transactional
    public void userAdd(Integer userId, Integer stadiumBookId) throws AddFailedException {
        if (stadiumBookId == null) {
            logger.info("StadiumBookItem 查找失败，未指定stadiumBookId！");
            throw new AddFailedException("预约失败，未指定需要预约的场馆预约！");
        }
        StadiumBook stadiumBook = null;
        try {
            stadiumBook = stadiumBookMapper.findById(stadiumBookId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumBook 查找失败，数据库发生未知异常！stadiumBookId = " + stadiumBookId);
            throw new AddFailedException("预约失败，数据库发生未知异常！");
        }
        if (stadiumBook == null || stadiumBook.getIsDelete() != 0) {
            logger.warn("StadiumBookItem 添加失败，不存在该预约！stadiumBookId = " + stadiumBookId);
            throw new AddFailedException("预约失败，不存在这个场馆预约！");
        }
        if (stadiumBook.getBookState() != 1) {
            logger.warn("StadiumBookItem 添加失败，场馆预约场次暂未开放预约！stadiumBook = " + stadiumBook);
            throw new AddFailedException("预约失败，该场馆预约场次暂未开放预约！");
        }
        if (stadiumBook.getMaxBookCount() <= stadiumBook.getNowBookCount()) {
            logger.warn("StadiumBookItem 添加失败，场馆预约场次已达预约上限！stadiumBook = " + stadiumBook);
            throw new AddFailedException("预约失败，该场馆预约场次已达预约上限！");
        }
        if (stadiumBook.getStartTime().compareTo(new Date()) <= 0) {
            logger.warn("StadiumBookItem 添加失败，已过场馆预约场次起始时间！stadiumBook = " + stadiumBook);
            throw new AddFailedException("预约失败，已过该场馆预约场次的起始时间！");
        }
        StadiumBookItem stadiumBookItem = null;
        try {
            stadiumBookItem = stadiumBookItemMapper.findByStadiumBookIdAndUserId(stadiumBookId, userId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumBookItem 查找失败，数据库发生未知异常！stadiumBookId = " + stadiumBookId + ", userId = " + userId);
            throw new AddFailedException("预约失败，数据库发生未知异常！");
        }
        if (stadiumBookItem != null) {
            logger.warn("StadiumBookItem 重复预约！stadiumBookItem = " + stadiumBookItem);
            throw new AddFailedException("预约失败，您已经预约过了！");
        }
        StadiumBookItem additionStadiumBookItem = new StadiumBookItem();
        additionStadiumBookItem.setStadiumBookId(stadiumBookId);
        additionStadiumBookItem.setUserId(userId);
        additionStadiumBookItem.setBookedTime(new Date());
        additionStadiumBookItem.setStadiumCommentId(null);
        additionStadiumBookItem.setIsDelete(0);
        try {
            stadiumBookItemMapper.add(additionStadiumBookItem);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumBookItem 添加失败，数据库发生未知异常！additionStadiumBookItem = " + additionStadiumBookItem);
            throw new AddFailedException("预约失败，数据库发生未知异常！");
        }
        // 更新已预约的用户数
        stadiumBook.setNowBookCount(stadiumBook.getNowBookCount() + 1);
        try {
            stadiumBookMapper.update(stadiumBook);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumBook 更新失败，数据库发生未知异常！stadiumBook = " + stadiumBook);
            throw new AddFailedException("预约失败，数据库发生未知异常！");
        }
        logger.info("StadiumBookItem 添加成功！additionStadiumBookItem = " + additionStadiumBookItem);
    }

    @Override
    public List<StadiumBookItem> findByStadiumBookIdAndPage(Integer stadiumBookId, Integer pageIndex, Integer pageSize) throws FindFailedException {
        if (stadiumBookId == null) {
            logger.warn("StadiumBookItem 查找失败，未指定stadiumBookId！");
            throw new FindFailedException("查询失败，未指定需要查找的场馆预约！");
        }
        // 检查页码是否合法
        if (pageIndex < 1) {
            throw new FindFailedException("查询失败，页码 " + pageIndex + " 非法，必须大于0！");
        }
        // 检查页大小是否合法
        if (pageSize < 1) {
            throw new FindFailedException("查询失败，页大小 " + pageSize + " 非法，必须大于0！");
        }
        List<StadiumBookItem> stadiumBookItemList = null;
        try {
            stadiumBookItemList = stadiumBookItemMapper.findByStadiumBookIdAndPage(stadiumBookId, (pageIndex - 1) * pageSize, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumBookItem 查找失败，数据库发生未知异常！stadiumBookId = " + stadiumBookId);
            throw new FindFailedException("查询失败，数据库发生未知异常！");
        }
        logger.info("StadiumBookItem 查找成功！stadiumBookItemList = " + stadiumBookItemList);
        return stadiumBookItemList;
    }
}
