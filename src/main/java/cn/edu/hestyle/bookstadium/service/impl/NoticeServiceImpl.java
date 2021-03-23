package cn.edu.hestyle.bookstadium.service.impl;

import cn.edu.hestyle.bookstadium.entity.Notice;
import cn.edu.hestyle.bookstadium.mapper.NoticeMapper;
import cn.edu.hestyle.bookstadium.service.INoticeService;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/23 4:56 下午
 */
@Service
public class NoticeServiceImpl implements INoticeService {
    private static final Logger logger = LoggerFactory.getLogger(NoticeServiceImpl.class);

    @Resource
    private NoticeMapper noticeMapper;

    @Override
    public List<Notice> findByIdAndPage(Integer toAccountType, Integer accountId, Integer pageIndex, Integer pageSize) {
        if (toAccountType == null) {
            logger.warn("Notice 查找失败，未指定toAccountType参数！");
            throw new FindFailedException("查找失败，缺少toAccountType参数！");
        }
        if (accountId == null) {
            logger.warn("Notice 查找失败，未指定accountId参数！");
            throw new FindFailedException("查找失败，缺少accountId参数！");
        }
        // 检查页码是否合法
        if (pageIndex < 1) {
            throw new FindFailedException("查询失败，页码 " + pageIndex + " 非法，必须大于0！");
        }
        // 检查页大小是否合法
        if (pageSize < 1) {
            throw new FindFailedException("查询失败，页大小 " + pageSize + " 非法，必须大于0！");
        }
        List<Notice> noticeList = null;
        try {
            noticeList = noticeMapper.findByIdAndPage(toAccountType, accountId, (pageIndex - 1) * pageSize, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Notice 查找失败，数据库发生未知错误！toAccountType = " + toAccountType + "，accountId = " + accountId + "，pageIndex = " + pageIndex + "，pageSize = " + pageSize);
            throw new FindFailedException("查找失败，数据库发生未知错误！");
        }
        return noticeList;
    }
}
