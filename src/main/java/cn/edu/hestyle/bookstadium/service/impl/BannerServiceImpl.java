package cn.edu.hestyle.bookstadium.service.impl;

import cn.edu.hestyle.bookstadium.entity.Banner;
import cn.edu.hestyle.bookstadium.entity.SystemManager;
import cn.edu.hestyle.bookstadium.mapper.BannerMapper;
import cn.edu.hestyle.bookstadium.mapper.SystemManagerMapper;
import cn.edu.hestyle.bookstadium.service.IBannerService;
import cn.edu.hestyle.bookstadium.service.exception.AddFailedException;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/10 5:10 下午
 */
@Service
public class BannerServiceImpl implements IBannerService {
    /** APP banner数量最大值 */
    private static final Integer BANNER_MAX_COUNT = 6;
    private static final Logger logger = LoggerFactory.getLogger(BannerServiceImpl.class);

    @Resource
    private BannerMapper bannerMapper;
    @Resource
    private SystemManagerMapper systemManagerMapper;

    @Override
    public void add(Integer systemManagerId, Banner banner) throws AddFailedException {
        SystemManager systemManager = null;
        try {
            systemManager = systemManagerMapper.findById(systemManagerId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SystemManager 查询失败，数据库发生未知异常！");
            throw new AddFailedException("添加失败，数据库发生未知异常!");
        }
        // 检查各个字段
        if (banner.getTitle() == null || banner.getTitle().length() == 0) {
            logger.warn("Banner 添加失败，未设置轮播title！banner = " + banner);
            throw new AddFailedException("Banner 添加失败，未设置轮播title!");
        }
        if (banner.getTitle().length() > 15) {
            logger.warn("Banner 添加失败，轮播title过长，超过15个字符！banner = " + banner);
            throw new AddFailedException("Banner 添加失败，轮播title过长，超过15个字符!");
        }
        if (banner.getContent() == null || banner.getContent().length() == 0) {
            logger.warn("Banner 添加失败，未设置轮播content！banner = " + banner);
            throw new AddFailedException("Banner 添加失败，未设置轮播content!");
        }
        if (banner.getContent().length() > 10000) {
            logger.warn("Banner 添加失败，轮播content过长，超过10000个字符！banner = " + banner);
            throw new AddFailedException("Banner 添加失败，轮播content过长，超过10000个字符!");
        }
        boolean isOk = false;
        try {
            isOk = this.checkBannerImagePath(banner.getImagePath());
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Banner 添加失败，轮播图片路径非法，资源不在服务器上！banner = " + banner);
            throw new AddFailedException("Banner 添加失败，轮播图片路径非法，资源不在服务器上!");
        }
        if (!isOk) {
            logger.warn("Banner 添加失败，未填写轮播图片路径！banner = " + banner);
            throw new AddFailedException("Banner 添加失败，未上传轮播图片资源!");
        }
        // 检查当前已经添加的banner数量是否超限
        Integer bannerCount = 0;
        try {
            bannerCount = bannerMapper.getAllCount();
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Banner 添加失败，数据库发生未知异常！");
            throw new AddFailedException("添加失败，数据库发生未知异常!");
        }
        if (bannerCount >= BANNER_MAX_COUNT) {
            logger.warn("Banner 添加失败，APP轮播项最多为" + BANNER_MAX_COUNT + "个！");
            throw new AddFailedException("添加失败，APP轮播项最多为" + BANNER_MAX_COUNT + "个！");
        }
        banner.setIsDelete(0);
        banner.setCreatedUser(systemManager.getUsername());
        banner.setCreatedTime(new Date());
        try {
            bannerMapper.add(banner);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Banner 添加失败，数据库发生未知异常！");
            throw new AddFailedException("添加失败，数据库发生未知异常!");
        }
        logger.warn("Banner 添加成功！banner = " + banner);
    }

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

    /**
     * 检查BannerImagePath的合法性
     * @param imagePath             imagePath
     * @return                      是否合法
     * @throws Exception            image path异常
     */
    private boolean checkBannerImagePath(String imagePath) throws Exception {
        if (imagePath == null || imagePath.length() == 0) {
            return false;
        }
        try {
            String pathNameTemp = ResourceUtils.getURL("classpath:").getPath() + "static/upload/image/banner";
            String pathNameTruth = pathNameTemp.replace("target", "src").replace("classes", "main/resources");
            String filePath = pathNameTruth + imagePath.substring(imagePath.lastIndexOf('/'));
            File file = new File(filePath);
            if (!file.exists()) {
                throw new Exception(imagePath + "文件不存在！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(imagePath + "文件不存在！");
        }
        logger.info("Banner imagePath = " + imagePath + " 通过检查！");
        return true;
    }
}
