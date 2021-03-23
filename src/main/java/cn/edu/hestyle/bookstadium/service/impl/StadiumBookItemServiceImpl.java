package cn.edu.hestyle.bookstadium.service.impl;

import cn.edu.hestyle.bookstadium.entity.Notice;
import cn.edu.hestyle.bookstadium.entity.Stadium;
import cn.edu.hestyle.bookstadium.entity.StadiumBook;
import cn.edu.hestyle.bookstadium.entity.StadiumBookItem;
import cn.edu.hestyle.bookstadium.mapper.NoticeMapper;
import cn.edu.hestyle.bookstadium.mapper.StadiumBookItemMapper;
import cn.edu.hestyle.bookstadium.mapper.StadiumBookMapper;
import cn.edu.hestyle.bookstadium.mapper.StadiumMapper;
import cn.edu.hestyle.bookstadium.service.IStadiumBookItemService;
import cn.edu.hestyle.bookstadium.service.exception.AddFailedException;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;
import cn.edu.hestyle.bookstadium.util.ResponseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/14 5:26 下午
 */
@Service
public class StadiumBookItemServiceImpl implements IStadiumBookItemService {
    /** 预约成功通知title、content */
    private static final String STADIUM_BOOK_TITLE = "体育场馆预约";
    private static final String STADIUM_BOOK_CONTENT = "成功预约了体育场馆【%s】的时间段【%s - %s】";

    private static final Logger logger = LoggerFactory.getLogger(StadiumBookItemServiceImpl.class);

    @Resource
    private NoticeMapper noticeMapper;
    @Resource
    private StadiumMapper stadiumMapper;
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
        // 预约成功通知
        Stadium stadium = null;
        try {
            stadium = stadiumMapper.findById(stadiumBook.getStadiumId());
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Stadium 查询失败，数据库发生未知异常！stadiumId = " + stadiumBook.getStadiumId());
            throw new AddFailedException("预约失败，数据库发生未知异常！");
        }
        Notice notice = new Notice();
        notice.setToAccountType(Notice.TO_ACCOUNT_USER);
        notice.setAccountId(userId);
        notice.setTitle(STADIUM_BOOK_TITLE);
        if (stadium != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ResponseResult.DATETIME_FORMAT);
            notice.setContent(String.format(STADIUM_BOOK_CONTENT, stadium.getName(), simpleDateFormat.format(stadiumBook.getStartTime()), simpleDateFormat.format(stadiumBook.getEndTime())));
        }
        notice.setGeneratedTime(new Date());
        notice.setIsDelete(0);
        try {
            noticeMapper.add(notice);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Notice 添加失败，数据库发生未知错误！notice = " + notice);
            throw new AddFailedException("点赞失败，数据库发生未知错误！");
        }
        logger.warn("Notice 添加成功！notice = " + notice);
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

    @Override
    public Integer getCountByStadiumBookId(Integer stadiumBookId) throws FindFailedException {
        if (stadiumBookId == null) {
            logger.warn("StadiumBookItem 查找失败，未指定stadiumBookId！");
            throw new FindFailedException("查询失败，未指定需要查找的场馆预约！");
        }
        Integer count = 0;
        try {
            count = stadiumBookItemMapper.getCountByStadiumBookId(stadiumBookId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumBookItem 查找失败，数据库发生未知异常！stadiumBookId = " + stadiumBookId);
            throw new FindFailedException("查询失败，数据库发生未知异常！");
        }
        return count;
    }
}
