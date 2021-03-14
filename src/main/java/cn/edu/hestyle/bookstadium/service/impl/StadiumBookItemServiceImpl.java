package cn.edu.hestyle.bookstadium.service.impl;

import cn.edu.hestyle.bookstadium.entity.StadiumBookItem;
import cn.edu.hestyle.bookstadium.mapper.StadiumBookItemMapper;
import cn.edu.hestyle.bookstadium.service.IStadiumBookItemService;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
    private StadiumBookItemMapper stadiumBookItemMapper;

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
