package cn.edu.hestyle.bookstadium.service.impl;

import cn.edu.hestyle.bookstadium.entity.Stadium;
import cn.edu.hestyle.bookstadium.entity.UserStadiumBookItem;
import cn.edu.hestyle.bookstadium.mapper.StadiumMapper;
import cn.edu.hestyle.bookstadium.mapper.UserStadiumBookItemMapper;
import cn.edu.hestyle.bookstadium.service.IUserStadiumBookItemService;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/15 3:05 下午
 */
@Service
public class UserStadiumBookItemServiceImpl implements IUserStadiumBookItemService {
    private static final Logger logger = LoggerFactory.getLogger(UserStadiumBookItemServiceImpl.class);


    @Resource
    private StadiumMapper stadiumMapper;
    @Resource
    private UserStadiumBookItemMapper userStadiumBookItemMapper;

    @Override
    public List<UserStadiumBookItem> findByUserIdAndPage(Integer userId, Integer pageIndex, Integer pageSize) throws FindFailedException {
        // 检查页码是否合法
        if (pageIndex < 1) {
            throw new FindFailedException("查询失败，页码 " + pageIndex + " 非法，必须大于0！");
        }
        // 检查页大小是否合法
        if (pageSize < 1) {
            throw new FindFailedException("查询失败，页大小 " + pageSize + " 非法，必须大于0！");
        }
        List<UserStadiumBookItem> userStadiumBookItemList = null;
        try {
            userStadiumBookItemList = userStadiumBookItemMapper.findByUserIdAndPage(userId, (pageIndex - 1) * pageSize, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("UserStadiumBookItem 查找失败，数据库发生未知异常！userId = " + userId + ", pageIndex = " + pageIndex + ", pageSize = " + pageSize);
            throw new FindFailedException("查询失败，数据库发生未知异常！");
        }
        if (userStadiumBookItemList != null && userStadiumBookItemList.size() != 0) {
            for (UserStadiumBookItem userStadiumBookItem : userStadiumBookItemList) {
                Stadium stadium = null;
                try {
                    stadium = stadiumMapper.findById(userStadiumBookItem.getStadiumId());
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.warn("Stadium 查找失败，数据库发生未知异常！stadiumId = " + userStadiumBookItem.getStadiumId());
                    throw new FindFailedException("查询失败，数据库发生未知异常！");
                }
                if (stadium != null) {
                    userStadiumBookItem.setStadiumName(stadium.getName());
                    userStadiumBookItem.setStadiumAddress(stadium.getAddress());
                    userStadiumBookItem.setStadiumImagePaths(stadium.getImagePaths());
                }
            }
        }
        logger.warn("UserStadiumBookItem 查找成功！userId = " + userId + ", userStadiumBookItemList = " + userStadiumBookItemList);
        return userStadiumBookItemList;
    }
}
