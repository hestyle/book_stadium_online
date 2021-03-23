package cn.edu.hestyle.bookstadium.service.impl;

import cn.edu.hestyle.bookstadium.entity.Announcement;
import cn.edu.hestyle.bookstadium.mapper.AnnouncementMapper;
import cn.edu.hestyle.bookstadium.service.IAnnouncementService;
import cn.edu.hestyle.bookstadium.service.exception.AddFailedException;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;
import cn.edu.hestyle.bookstadium.service.exception.ModifyFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/23 12:34 下午
 */
@Service
public class AnnouncementServiceImpl implements IAnnouncementService {
    /** 默认只有一个Announcement，且id = 1 */
    private static final Integer ANNOUNCEMENT_ID = 1;
    /** Announcement title最大长度 */
    private static final Integer ANNOUNCEMENT_TITLE_MAX_LENGTH = 12;
    /** Announcement content最大长度 */
    private static final Integer ANNOUNCEMENT_CONTENT_MAX_LENGTH = 200;
    private static final Logger logger = LoggerFactory.getLogger(AnnouncementServiceImpl.class);

    @Resource
    private AnnouncementMapper announcementMapper;

    @Override
    public void save(Announcement announcement) {
        if (announcement == null) {
            logger.warn("Announcement 保存失败，announcement为空！");
            throw new AddFailedException("保存失败，公告不能为空！");
        }
        Announcement announcementModify = null;
        try {
            // 默认查找id = 1的announcement
            announcementModify = announcementMapper.findById(ANNOUNCEMENT_ID);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Announcement 查询失败，数据库发生未知异常！");
            throw new FindFailedException("保存失败，数据库发生未知异常！");
        }
        if (announcementModify == null) {
            // 没有查找到id = 1的announcement，创建一个
            announcementModify = new Announcement();
            announcementModify.setCreatedUser("SystemManager");
            announcementModify.setCreatedTime(new Date());
        }
        if (announcement.getIsDelete() != null && announcement.getIsDelete().equals(1)) {
            // 删除操作 = 清空内容
            announcementModify.setTitle(null);
            announcementModify.setContent(null);
        } else {
            // 检查字段合法性
            if (announcement.getTitle() == null || announcement.getTitle().length() == 0) {
                logger.warn("Announcement 修改失败，公告title不能为空！announcement = " + announcement);
                throw new FindFailedException("保存失败，公告标题不能为空！");
            }
            if (announcement.getTitle().length() > ANNOUNCEMENT_TITLE_MAX_LENGTH) {
                logger.warn("Announcement 修改失败，公告title超过了" + ANNOUNCEMENT_TITLE_MAX_LENGTH + "字符！announcement = " + announcement);
                throw new FindFailedException("保存失败，公告标题超过了" + ANNOUNCEMENT_TITLE_MAX_LENGTH + "字符！");
            }
            announcementModify.setTitle(announcement.getTitle());
            // 检查字段合法性
            if (announcement.getContent() == null || announcement.getContent().length() == 0) {
                logger.warn("Announcement 修改失败，公告content不能为空！announcement = " + announcement);
                throw new FindFailedException("保存失败，公告内容不能为空！");
            }
            if (announcement.getContent().length() > ANNOUNCEMENT_CONTENT_MAX_LENGTH) {
                logger.warn("Announcement 修改失败，公告content超过了" + ANNOUNCEMENT_CONTENT_MAX_LENGTH + "字符！announcement = " + announcement);
                throw new FindFailedException("保存失败，公告内容超过了" + ANNOUNCEMENT_CONTENT_MAX_LENGTH + "字符！");
            }
            announcementModify.setContent(announcement.getContent());
        }
        announcementModify.setModifiedUser("SystemManager");
        announcementModify.setModifiedTime(new Date());
        try {
            if (announcementModify.getId() == null) {
                announcementMapper.add(announcementModify);
            } else {
                announcementMapper.update(announcementModify);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Announcement 保存失败，数据库发生未知异常！announcementModify = " + announcementModify);
            throw new ModifyFailedException("保存失败，数据库发生未知异常！");
        }
        logger.warn("Announcement 保存成功！announcementModify = " + announcementModify);
    }

    @Override
    public Announcement find() {
        Announcement announcement = null;
        try {
            // 默认查找id = 1的announcement
            announcement = announcementMapper.findById(ANNOUNCEMENT_ID);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Announcement 查询失败，数据库发生未知异常！");
            throw new FindFailedException("查询失败，数据库发生未知异常！");
        }
        // 如果没有查找到，则在数据库创建
        if (announcement == null) {
            announcement = new Announcement();
            announcement.setCreatedUser("SystemManager");
            announcement.setCreatedTime(new Date());
            try {
                announcementMapper.add(announcement);
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("Announcement 添加失败，数据库发生未知异常！");
            }
        }
        return announcement;
    }
}
