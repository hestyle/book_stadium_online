package cn.edu.hestyle.bookstadium.mapper;

import cn.edu.hestyle.bookstadium.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/23 11:29 上午
 */
@Mapper
public interface AnnouncementMapper {
    /**
     * 添加announcement
     * @param announcement  announcement
     */
    void add(Announcement announcement);

    /**
     * 更新announcement
     * @param announcement  announcement
     */
    void update(Announcement announcement);

    /**
     * 通过announcementId查找
     * @param announcementId    announcementId
     */
    Announcement findById(Integer announcementId);
}
