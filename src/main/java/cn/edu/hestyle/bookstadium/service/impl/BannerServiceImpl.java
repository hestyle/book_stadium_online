package cn.edu.hestyle.bookstadium.service.impl;

import cn.edu.hestyle.bookstadium.entity.Banner;
import cn.edu.hestyle.bookstadium.mapper.BannerMapper;
import cn.edu.hestyle.bookstadium.service.IBannerService;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/10 5:10 下午
 */
@Service
public class BannerServiceImpl implements IBannerService {
    private static final Logger logger = LoggerFactory.getLogger(BannerServiceImpl.class);

    @Resource
    private BannerMapper bannerMapper;

    @Override
    public List<Banner> findAll() throws FindFailedException {
        List<Banner> bannerList = null;
        try {
            bannerList = bannerMapper.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Banner 查询失败，数据库发生未知异常！");
            throw new FindFailedException("查询失败，数据库发生未知异常!");
        }
        logger.warn("Banner 查询成功！bannerList = " + bannerList);
        return bannerList;
    }
}
