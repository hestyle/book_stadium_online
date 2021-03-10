package cn.edu.hestyle.bookstadium.mapper;

import cn.edu.hestyle.bookstadium.entity.Banner;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/10 5:00 下午
 */
@Mapper
public interface BannerMapper {

    /**
     * 添加banner
     * @param banner    banner
     */
    void add(Banner banner);

    /**
     * 获取所有banner
     * @return          List Banner
     */
    List<Banner> findAll();

    /**
     * 通过id查找Banner
     * @param id        Banner id
     * @return          Banner
     */
    Banner findById(Integer id);

    /**
     * 更新Banner
     * @param banner    Banner
     */
    void update(Banner banner);

    /**
     * 获取当前轮播的数量
     * @return          当前所有未删除的banner数量
     */
    Integer getAllCount();
}
