package cn.edu.hestyle.bookstadium.service;

import cn.edu.hestyle.bookstadium.entity.Announcement;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/23 12:27 下午
 */
public interface IAnnouncementService {
    /**
     * 保存announcement
     * @param announcement  announcement
     */
    void save(Announcement announcement);

    /**
     * 查找Announcement
     */
    Announcement find();
}
