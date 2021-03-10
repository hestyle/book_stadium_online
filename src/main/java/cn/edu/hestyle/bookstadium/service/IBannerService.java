package cn.edu.hestyle.bookstadium.service;

import cn.edu.hestyle.bookstadium.entity.Banner;
import cn.edu.hestyle.bookstadium.service.exception.AddFailedException;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;

import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/10 5:08 下午
 */
public interface IBannerService {

    /**
     * （systemManager）添加banner
     * @param systemManagerId       systemManagerId
     * @param banner                banner
     * @throws AddFailedException   添加异常
     */
    void add(Integer systemManagerId, Banner banner) throws AddFailedException;

    /**
     * 获取所有未删除的Banner
     * @return                      List Banner
     * @throws FindFailedException  查询失败异常
     */
    List<Banner> findAll() throws FindFailedException;
}
