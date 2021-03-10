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
     * 获取所有banner
     * @return          List Banner
     */
    List<Banner> findAll();
}
