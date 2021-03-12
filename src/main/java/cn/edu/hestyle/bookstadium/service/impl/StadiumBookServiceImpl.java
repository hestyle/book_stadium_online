package cn.edu.hestyle.bookstadium.service.impl;

import cn.edu.hestyle.bookstadium.entity.Stadium;
import cn.edu.hestyle.bookstadium.entity.StadiumBook;
import cn.edu.hestyle.bookstadium.entity.StadiumManager;
import cn.edu.hestyle.bookstadium.mapper.StadiumBookMapper;
import cn.edu.hestyle.bookstadium.mapper.StadiumManagerMapper;
import cn.edu.hestyle.bookstadium.mapper.StadiumMapper;
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
import java.util.Date;
import java.util.HashSet;
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
}
