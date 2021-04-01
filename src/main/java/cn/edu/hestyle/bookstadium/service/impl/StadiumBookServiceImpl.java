package cn.edu.hestyle.bookstadium.service.impl;

import cn.edu.hestyle.bookstadium.entity.*;
import cn.edu.hestyle.bookstadium.mapper.*;
import cn.edu.hestyle.bookstadium.service.IStadiumBookService;
import cn.edu.hestyle.bookstadium.service.exception.AddFailedException;
import cn.edu.hestyle.bookstadium.service.exception.DeleteFailedException;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;
import cn.edu.hestyle.bookstadium.service.exception.ModifyFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/6 11:13 上午
 */
@Service
public class StadiumBookServiceImpl implements IStadiumBookService {
    /** 回复场馆评论删除通知体育场馆管理员title、content */
    private static final String STADIUM_BOOK_ITEM_DELETE_STADIUM_MANAGER_TITLE = "场馆预约删除";
    private static final String STADIUM_BOOK_ITEM_DELETE_STADIUM_MANAGER_CONTENT = "系统管理员【%s】,因【%s】, 删除了你所创建的【%s】场馆的【%s-%s】预约场次，已经预约用户已被取消！";
    /** 回复场馆评论删除通知用户的title、content */
    private static final String STADIUM_BOOK_ITEM_DELETE_USER_TITLE = "场馆预约删除";
    private static final String STADIUM_BOOK_ITEM_DELETE_USER_CONTENT = "系统管理员【%s】,因【%s】, 删除了【%s】场馆的【%s-%s】预约场次，你的预约已被取消！";

    private static final Logger logger = LoggerFactory.getLogger(StadiumBookServiceImpl.class);

    @Resource
    private NoticeMapper noticeMapper;
    @Resource
    private StadiumMapper stadiumMapper;
    @Resource
    private SystemManagerMapper systemManagerMapper;
    @Resource
    private StadiumManagerMapper stadiumManagerMapper;
    @Resource
    private StadiumBookMapper stadiumBookMapper;
    @Resource
    private StadiumBookItemMapper stadiumBookItemMapper;

    @Override
    public void stadiumManagerAdd(Integer stadiumManagerId, StadiumBook stadiumBook) throws AddFailedException {
        StadiumManager stadiumManager = null;
        try {
            stadiumManager = stadiumManagerMapper.findById(stadiumManagerId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumManager 查询失败，数据库发生未知异常！id = " + stadiumManagerId);
            throw new AddFailedException("添加失败，数据库发生未知异常！");
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
        if (!stadiumManager.getId().equals(stadium.getStadiumManagerId())) {
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
        // 检查该Stadium是否能插入这个预约时间段
        boolean flag = false;
        try {
            flag = checkStadiumBookTime(stadiumBook.getStadiumId(), null, stadiumBook.getStartTime(), stadiumBook.getEndTime());
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumBook 修改失败，该场馆时段段冲突！stadiumId = " + stadiumBook.getStadiumId());
            throw new ModifyFailedException("添加失败，该场馆预约的时间段将会与已经添加的场馆预约冲突！");
        }
        if (!flag) {
            logger.warn("StadiumBook 修改失败，该场馆时段段冲突！stadiumId = " + stadiumBook.getStadiumId());
            throw new ModifyFailedException("添加失败，该场馆预约的时间段将会与已经添加的场馆预约冲突！");
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
    public void stadiumManagerModify(Integer stadiumManagerId, StadiumBook stadiumBook) throws ModifyFailedException {
        StadiumManager stadiumManager = null;
        try {
            stadiumManager = stadiumManagerMapper.findById(stadiumManagerId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumManager 查询失败，数据库发生未知异常！stadiumManagerId = " + stadiumManagerId);
            throw new ModifyFailedException("修改失败，数据库发生未知异常！");
        }
        // 检查StadiumBook是否存在
        if (stadiumBook.getId() == null) {
            logger.warn("StadiumBook 修改失败，未指定需要修改预约！stadiumBook = " + stadiumBook);
            throw new ModifyFailedException("修改失败，未指定需要修改的预约！");
        }
        StadiumBook stadiumBookModify = null;
        try {
            stadiumBookModify = stadiumBookMapper.findById(stadiumBook.getId());
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumBook 查询失败，数据库发生未知异常！stadiumBookId = " + stadiumBook.getId());
            throw new ModifyFailedException("修改失败，数据库发生未知异常！");
        }
        if (stadiumBookModify == null) {
            logger.warn("StadiumBook 修改失败，指定的体育场馆预约不存在！stadiumBook = " + stadiumBook);
            throw new ModifyFailedException("修改失败，场馆预约 id = " + stadiumBook.getId() + "不存在！");
        }
        // 检查StadiumManager是否有权限修改StadiumBook
        Stadium stadium = null;
        try {
            stadium = stadiumMapper.findById(stadiumBookModify.getStadiumId());
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Stadium 查询失败，数据库发生未知异常！stadiumId = " + stadiumBookModify.getStadiumId());
            throw new ModifyFailedException("修改失败，数据库发生未知异常！");
        }
        if (!stadiumManager.getId().equals(stadium.getStadiumManagerId())) {
            logger.warn("StadiumBook 修改失败，stadiumManager = " + stadiumManager + "，无权限修改体育场馆 Stadium = " + stadium + "的预约！");
            throw new ModifyFailedException("修改失败，体育场馆 " + stadium.getName() + " 不是您的账户创建的，无法修改其场馆预约！");
        }
        // 检查是否修改正在预约的场馆预约
        if (stadiumBookModify.getBookState() == 1) {
            if (stadiumBook.getStartTime() != null || stadiumBook.getEndTime() != null) {
                logger.warn("StadiumBook 修改失败，正在预约的场馆预约时段无法修改！");
                throw new ModifyFailedException("修改失败，正在预约的场馆预约时段无法修改！");
            }
            if (stadiumBook.getIsDelete() != null) {
                logger.warn("StadiumBook 修改失败，无法删除正在预约的场馆预约！");
                throw new ModifyFailedException("修改失败，无法删除正在预约的场馆预约！");
            }
        }
        // 检查修改字段的合法性
        if (stadiumBook.getStartTime() != null) {
            stadiumBookModify.setStartTime(stadiumBook.getStartTime());
        }
        if (stadiumBook.getEndTime() != null) {
            stadiumBookModify.setEndTime(stadiumBook.getEndTime());
        }
        if (stadiumBookModify.getStartTime().compareTo(new Date()) < 0) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
            logger.warn("StadiumBook 修改失败，场馆预约时段的起始时间在当前时间的前面！");
            throw new ModifyFailedException("修改失败，场馆预约时段的起始时间在当前时间 " + dateFormat.format(new Date()) + " 的前面！");
        }
        if (stadiumBookModify.getStartTime().compareTo(stadiumBookModify.getEndTime()) >= 0) {
            logger.warn("StadiumBook 修改失败，场馆预约时段起、止节点非法！data = " + stadiumBook);
            throw new ModifyFailedException("修改失败，场馆预约时段的起始时间节点 在 终止节点的后面！");
        }
        // 检查该Stadium是否能插入这个预约时间段
        boolean flag = false;
        try {
            flag = checkStadiumBookTime(stadiumBook.getStadiumId(), stadiumBookModify.getId(), stadiumBookModify.getStartTime(), stadiumBookModify.getEndTime());
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumBook 修改失败，该场馆时段段冲突！stadiumId = " + stadiumBookModify.getStadiumId());
            throw new ModifyFailedException("修改失败，该场馆预约的时间段将会与已经添加的场馆预约冲突！");
        }
        if (!flag) {
            logger.warn("StadiumBook 修改失败，该场馆时段段冲突！stadiumId = " + stadiumBookModify.getStadiumId());
            throw new ModifyFailedException("修改失败，该场馆预约的时间段将会与已经添加的场馆预约冲突！");
        }
        if (stadiumBook.getBookState() != null) {
            if (stadiumBook.getBookState() < 0 || stadiumBook.getBookState() > 3) {
                logger.warn("StadiumBook 修改失败，场馆预约状态修改错误！data = " + stadiumBook);
                throw new ModifyFailedException("修改失败，场馆预约状态修改非法，只有暂不开始预约、开始预约、停止预约三种状态！");
            }
            if (stadiumBook.getBookState() < stadiumBookModify.getBookState()) {
                logger.warn("StadiumBook 修改失败，场馆预约状态修改错误，状态不可逆！data = " + stadiumBook);
                throw new ModifyFailedException("修改失败，场馆预约状态修改非法！");
            }
            stadiumBookModify.setBookState(stadiumBook.getBookState());
        }
        if (stadiumBook.getMaxBookCount() != null) {
            if (stadiumBook.getMaxBookCount() < stadiumBookModify.getNowBookCount()) {
                logger.warn("StadiumBook 修改失败，场馆预约容量修改小于已经预约的数量！data = " + stadiumBook);
                throw new ModifyFailedException("修改失败，场馆预约容量修改小于当前已经预约的人数！");
            }
            stadiumBookModify.setMaxBookCount(stadiumBook.getMaxBookCount());
        }
        if (stadiumBook.getIsDelete() != null) {
            if (stadiumBook.getIsDelete() < 0 || stadiumBook.getIsDelete() > 2) {
                logger.warn("StadiumBook 修改失败，场馆预约删除状态修改非法！data = " + stadiumBook);
                throw new ModifyFailedException("修改失败，场馆预约只能有未删除/删除两种状态！");
            }
            if (stadiumBook.getIsDelete() < stadiumBookModify.getIsDelete()) {
                logger.warn("StadiumBook 修改失败，场馆预约删除状态不可逆！data = " + stadiumBook);
                throw new ModifyFailedException("修改失败，场馆预约删除状态不可逆！");
            }
            if (stadiumBookModify.getNowBookCount() > 0 && stadiumBookModify.getEndTime().after(new Date())) {
                logger.warn("StadiumBook 修改失败，无法删除有用户预约，且还未到预约时间段的终止时间的场馆预约！stadiumBookModify = " + stadiumBookModify);
                throw new ModifyFailedException("修改失败，无法删除有用户预约，且还未到预约时间段的终止时间的场馆预约！");
            }
            stadiumBookModify.setIsDelete(stadiumBook.getIsDelete());
        }
        stadiumBookModify.setModifiedUser(stadiumManager.getUsername());
        stadiumBookModify.setModifiedTime(new Date());
        try {
            stadiumBookMapper.update(stadiumBookModify);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumBook 更新失败，数据库发生未知异常！stadiumBookModify = " + stadiumBookModify);
            throw new ModifyFailedException("修改失败，数据库发生未知异常！");
        }
        logger.info("StadiumBook 更新成功！stadiumBookModify = " + stadiumBookModify);
    }

    @Override
    @Transactional
    public void stadiumManagerDeleteByIdList(Integer stadiumManagerId, List<Integer> stadiumBookIdList) throws DeleteFailedException {
        StadiumManager stadiumManager = null;
        try {
            stadiumManager = stadiumManagerMapper.findById(stadiumManagerId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumManager 查询失败，数据库发生未知异常！stadiumManagerId = " + stadiumManagerId);
            throw new DeleteFailedException("修改失败，数据库发生未知异常！");
        }
        if (stadiumBookIdList == null || stadiumBookIdList.size() == 0) {
            logger.warn("StadiumBook 批量删除失败，未指定需要删除的场馆预约！stadiumBookIdList = " + stadiumBookIdList);
            throw new DeleteFailedException("批量删除失败，未指定需要删除的场馆预约！");
        }
        // 用于记录stadiumManager拥有的 stadium id
        HashSet<Integer> stadiumIdSet = new HashSet<>();
        for (Integer stadiumBookId : stadiumBookIdList) {
            // 检查StadiumBook是否存在
            if (stadiumBookId == null) {
                logger.warn("StadiumBook 批量删除失败，场馆预约id格式错误！stadiumBookIdList = " + stadiumBookIdList);
                throw new DeleteFailedException("StadiumBook 批量删除失败，场馆预约id格式错误！");
            }
            StadiumBook stadiumBook = null;
            try {
                stadiumBook = stadiumBookMapper.findById(stadiumBookId);
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("StadiumBook 查询失败，数据库发生未知异常！stadiumBookId = " + stadiumBookId);
                throw new DeleteFailedException("StadiumBook 批量删除失败，数据库发生未知异常！");
            }
            if (stadiumBook == null) {
                logger.warn("StadiumBook 批量删除失败，指定的体育场馆预约不存在！stadiumBookId = " + stadiumBookId);
                throw new DeleteFailedException("StadiumBook 批量删除失败，场馆预约 id = " + stadiumBookId + "不存在！");
            }
            // 检查StadiumManager是否有权限修改StadiumBook
            if (!stadiumIdSet.contains(stadiumBook.getStadiumId())) {
                Stadium stadium = null;
                try {
                    stadium = stadiumMapper.findById(stadiumBook.getStadiumId());
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.warn("Stadium 查询失败，数据库发生未知异常！stadiumId = " + stadiumBook.getStadiumId());
                    throw new DeleteFailedException("StadiumBook 批量删除失败，数据库发生未知异常！");
                }
                if (!stadiumManager.getId().equals(stadium.getStadiumManagerId())) {
                    logger.warn("StadiumBook 修改失败，stadiumManager = " + stadiumManager + "，无权限修改体育场馆 Stadium = " + stadium + "的预约！");
                    throw new DeleteFailedException("修改失败，体育场馆 " + stadium.getName() + " 不是您的账户创建的，无法修改其场馆预约！");
                }
                stadiumIdSet.add(stadium.getStadiumManagerId());
            }
            // 检查该stadiumBook是否可以删除
            if (stadiumBook.getBookState() == 1) {
                logger.warn("StadiumBook 删除失败，无法删除正在进行预约的场馆预约！stadiumBook = " + stadiumBook);
                throw new DeleteFailedException("StadiumBook 批量删除失败，无法删除id = " + stadiumBook.getId() + "正在进行预约的场馆预约！");
            }
            if (stadiumBook.getNowBookCount() > 0 && stadiumBook.getEndTime().after(new Date())) {
                logger.warn("StadiumBook 删除失败，无法删除有用户预约，且还未到预约时间段的终止时间的场馆预约！stadiumBook = " + stadiumBook);
                throw new DeleteFailedException("修改失败，无法删除有用户预约，且还未到预约时间段的终止时间的场馆预约 id = " + stadiumBook.getId() + " ！");
            }
            try {
                stadiumBookMapper.deleteById(stadiumBookId);
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("StadiumBook 删除失败，数据库发生未知异常！stadiumBook = " + stadiumBook);
                throw new DeleteFailedException("StadiumBook 批量删除失败，数据库发生未知异常！");
            }
        }
        logger.warn("StadiumBook 批量删除成功！stadiumBookIdList = " + stadiumBookIdList);
    }

    @Override
    public List<StadiumBook> stadiumManagerFindAllByPage(Integer stadiumManagerId, Integer pageIndex, Integer pageSize) throws FindFailedException {
        // 查询stadiumManager
        StadiumManager stadiumManager = null;
        try {
            stadiumManager = stadiumManagerMapper.findById(stadiumManagerId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumManager 查找失败，数据库发生未知异常！stadiumManagerId = " + stadiumManagerId);
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
    public Integer stadiumManagerGetAllCount(Integer stadiumManagerId) throws FindFailedException {
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
            count = stadiumBookMapper.stadiumManagerGetAllCount(stadiumManager.getId());
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumBook 查询失败，数据库发生未知异常！ stadiumManagerId= " + stadiumManager.getId());
            throw new FindFailedException("查询失败，数据库发生未知异常！");
        }
        logger.warn("StadiumBook 查询成功， stadiumManagerId= " + stadiumManager.getId() + "，count = " + count);
        return count;
    }

    @Override
    public List<StadiumBook> userFindByStadiumIdAndPage(Integer stadiumId, Integer pageIndex, Integer pageSize) throws FindFailedException {
        if (stadiumId == null) {
            logger.warn("StadiumBook 查找失败，未指定stadiumId！");
            throw new FindFailedException("查询失败，未指定需要查找的体育场馆ID！");
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
            stadiumBookList = stadiumBookMapper.userFindByStadiumIdAndPage(stadiumId, (pageIndex - 1) * pageSize, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumBook 查找失败，数据库发生未知异常！stadiumId = " + stadiumId);
            throw new FindFailedException("查询失败，数据库发生未知异常！");
        }
        logger.warn("StadiumBook 查找成功！stadiumBookList = " + stadiumBookList);
        return stadiumBookList;
    }

    @Override
    public List<StadiumBook> systemManagerFindByStadiumIdAndPage(Integer stadiumId, Integer pageIndex, Integer pageSize) throws FindFailedException {
        if (stadiumId == null) {
            logger.warn("StadiumBook 查找失败，未指定stadiumId！");
            throw new FindFailedException("查询失败，未指定stadiumId！");
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
            stadiumBookList = stadiumBookMapper.systemManagerFindByStadiumIdAndPage(stadiumId, (pageIndex - 1) * pageSize, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumBook 查找失败，数据库发生未知异常！stadiumId = " + stadiumId);
            throw new FindFailedException("查询失败，数据库发生未知异常！");
        }
        logger.warn("StadiumBook 查找成功！stadiumBookList = " + stadiumBookList);
        return stadiumBookList;
    }

    @Override
    public Integer systemManagerGetCountById(Integer stadiumId) throws FindFailedException {
        if (stadiumId == null) {
            logger.warn("StadiumBook 查找失败，未指定stadiumId！");
            throw new FindFailedException("查询失败，未指定stadiumId！");
        }
        Integer count = 0;
        try {
            count = stadiumBookMapper.systemManagerGetCountById(stadiumId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumBook 查询失败，数据库发生未知异常！ stadiumId= " + stadiumId);
            throw new FindFailedException("查询失败，数据库发生未知异常！");
        }
        logger.warn("StadiumBook 查询成功， stadiumId= " + stadiumId + "，count = " + count);
        return count;
    }

    @Override
    @Transactional
    public void systemManagerDeleteById(Integer systemManagerId, Integer stadiumBookId, String deleteReason) {
        if (stadiumBookId == null) {
            logger.warn("StadiumBook 删除失败！未指定stadiumBookId！");
            throw new FindFailedException("操作失败，未指定需要删除的场馆预约ID！");
        }
        StadiumBook stadiumBook = null;
        try {
            stadiumBook = stadiumBookMapper.findById(stadiumBookId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumBook 查询失败！数据库发生未知异常！");
            throw new FindFailedException("操作失败，数据库发生未知异常！");
        }
        if (stadiumBook == null || (stadiumBook.getIsDelete() != null && stadiumBook.getIsDelete() == 1)) {
            logger.warn("StadiumBook 删除失败！该stadiumBook不存在或已被删除！stadiumBook = " + stadiumBook);
            throw new FindFailedException("操作失败，该stadiumBook不存在或已被删除！");
        }
        if (deleteReason == null || deleteReason.length() == 0) {
            logger.warn("StadiumBook 删除失败！未填写删除原因！stadiumBook = " + stadiumBook);
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
        // 1、更新stadiumBook为删除状态
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
        Stadium stadium = null;
        try {
            stadium = stadiumMapper.findById(stadiumBook.getStadiumId());
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Stadium 查找失败！数据库发生未知异常！stadiumId = " + stadiumBook.getStadiumId());
            throw new FindFailedException("操作失败，数据库发生未知异常！");
        }
        if (stadium == null) {
            logger.warn("Stadium 查找失败！数据库内部发生错误！stadiumId = " + stadiumBook.getStadiumId() + " 不存在！");
            throw new FindFailedException("操作失败，数据库内部发生错误！");
        }
        // 通知对应的user
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Notice managerNotice = new Notice();
        managerNotice.setToAccountType(Notice.TO_ACCOUNT_STADIUM_MANAGER);
        managerNotice.setAccountId(stadium.getStadiumManagerId());
        managerNotice.setTitle(STADIUM_BOOK_ITEM_DELETE_STADIUM_MANAGER_TITLE);
        managerNotice.setContent(String.format(STADIUM_BOOK_ITEM_DELETE_STADIUM_MANAGER_CONTENT, systemManager.getUsername(), deleteReason, stadium.getName(), simpleDateFormat.format(stadiumBook.getStartTime()), simpleDateFormat.format(stadiumBook.getEndTime())));
        managerNotice.setGeneratedTime(new Date());
        managerNotice.setIsDelete(0);
        try {
            noticeMapper.add(managerNotice);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Notice 添加失败，数据库发生未知错误！managerNotice = " + managerNotice);
            throw new AddFailedException("回复失败，数据库发生未知错误！");
        }
        logger.warn("Notice 添加成功！managerNotice = " + managerNotice);
        // 3、取消已经预约了该场次的用户预约
        Integer beginIndex = 0;
        Integer pageSize = 10;
        Integer bookItemCount = 0;
        try {
            bookItemCount = stadiumBookItemMapper.getCountByStadiumBookId(stadiumBookId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumBookItem 查找失败！数据库发生未知异常！stadiumBookId = " + stadiumBookId);
            throw new FindFailedException("操作失败，数据库发生未知异常！");
        }
        // 分页查询该StadiumBook的所有StadiumBookItem
        while (beginIndex < bookItemCount) {
            List<StadiumBookItem> stadiumBookItemList = null;
            try {
                stadiumBookItemList = stadiumBookItemMapper.findByStadiumBookIdAndPage(stadiumBookId, beginIndex, pageSize);
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("StadiumBookItem 查找失败！数据库发生未知异常！stadiumBookId = " + stadiumBookId + ",beginIndex = " + beginIndex + ", pageSize" + pageSize);
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
            beginIndex += pageSize;
        }
    }

    private boolean checkStadiumBookTime(Integer stadiumId, Integer stadiumBookId, Date startTime, Date endTime) throws Exception {
        List<StadiumBook> stadiumBookList = null;
        try {
            stadiumBookList = stadiumBookMapper.userFindByStadiumIdAndPage(stadiumId, 0, Integer.MAX_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("StadiumBook 查询失败，数据库发生未知异常！stadiumId = " + stadiumId);
            throw new ModifyFailedException("操作失败，数据库发生未知异常！");
        }
        if (stadiumBookList == null || stadiumBookList.size() == 0) {
            return true;
        }
        // 按照StartTime升序排序
        Collections.sort(stadiumBookList, new Comparator<StadiumBook>() {
            @Override
            public int compare(StadiumBook o1, StadiumBook o2) {
                return o1.getStartTime().compareTo(o2.getStartTime());
            }
        });
        int startTimeIndex = stadiumBookList.size();
        int endTimeIndex = -1;
        // 查找startTimeIndex
        for (int i = stadiumBookList.size() - 1; i >= 0; --i) {
            StadiumBook stadiumBook = stadiumBookList.get(i);
            if (stadiumBook.getId().equals(stadiumBookId)) {
                // stadiumBookId != null 说明是时间段修改
                startTimeIndex = i;
                continue;
            }
            if (stadiumBook.getEndTime().before(startTime)) {
                startTimeIndex = i + 1;
                break;
            }
            if (stadiumBook.getStartTime().before(startTime)) {
                throw new Exception("该体育场馆的预约时间段冲突！");
            }
            startTimeIndex = i + 1;
        }
        // 查找endTimeIndex
        for (int i = 0; i < stadiumBookList.size(); ++i) {
            StadiumBook stadiumBook = stadiumBookList.get(i);
            if (stadiumBook.getId().equals(stadiumBookId)) {
                // stadiumBookId != null 说明是时间段修改
                endTimeIndex = i;
                continue;
            }
            if (stadiumBook.getStartTime().after(endTime)) {
                endTimeIndex = i - 1;
                break;
            }
            if (stadiumBook.getEndTime().after(endTime)) {
                throw new Exception("该体育场馆的预约时间段冲突！");
            }
            endTimeIndex = i - 1;
        }
        if (startTimeIndex < endTimeIndex) {
            throw new Exception("该体育场馆的预约时间段冲突！");
        }
        return true;
    }
}
