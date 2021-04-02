package cn.edu.hestyle.bookstadium.service.impl;

import cn.edu.hestyle.bookstadium.entity.Complaint;
import cn.edu.hestyle.bookstadium.entity.ComplaintVO;
import cn.edu.hestyle.bookstadium.entity.StadiumManager;
import cn.edu.hestyle.bookstadium.entity.User;
import cn.edu.hestyle.bookstadium.mapper.ComplaintMapper;
import cn.edu.hestyle.bookstadium.mapper.StadiumManagerMapper;
import cn.edu.hestyle.bookstadium.mapper.UserMapper;
import cn.edu.hestyle.bookstadium.service.IComplaintService;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/4/2 11:04 上午
 */
@Service
public class ComplaintServiceImpl implements IComplaintService {
    private static final Logger logger = LoggerFactory.getLogger(ComplaintServiceImpl.class);

    @Resource
    private UserMapper userMapper;
    @Resource
    private StadiumManagerMapper stadiumManagerMapper;
    @Resource
    private ComplaintMapper complaintMapper;

    @Override
    public List<ComplaintVO> systemManagerFindAllByPage(Integer pageIndex, Integer pageSize, String titleKey) {
        // 检查页码是否合法
        if (pageIndex < 1) {
            throw new FindFailedException("查询失败，页码 " + pageIndex + " 非法，必须大于0！");
        }
        // 检查页大小是否合法
        if (pageSize < 1) {
            throw new FindFailedException("查询失败，页大小 " + pageSize + " 非法，必须大于0！");
        }
        // 去除titleKey中的特殊字符
        if (titleKey != null && titleKey.length() != 0) {
            titleKey = titleKey.replaceAll("%", "").replaceAll("'", "").replaceAll("\\?", "");
        }
        List<Complaint> complaintList = null;
        try {
            complaintList = complaintMapper.findAllByPage((pageIndex - 1) * pageSize, pageSize, titleKey);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Complaint 查找失败，数据库发生未知错误！pageIndex = " + pageIndex + "，pageSize = " + pageSize);
            throw new FindFailedException("查找失败，数据库发生未知错误！");
        }
        List<ComplaintVO> complaintVOList = new ArrayList<>();
        if (complaintList != null && complaintList.size() != 0) {
            for (Complaint complaint : complaintList) {
                complaintVOList.add(toComplaintVO(complaint));
            }
        }
        return complaintVOList;
    }

    @Override
    public Integer getAllCount(String titleKey) {
        // 去除titleKey中的特殊字符
        if (titleKey != null && titleKey.length() != 0) {
            titleKey = titleKey.replaceAll("%", "").replaceAll("'", "").replaceAll("\\?", "");
        }
        Integer count = 0;
        try {
            count = complaintMapper.getAllCount(titleKey);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Complaint 数量查找失败，数据库发生未知错误！");
            throw new FindFailedException("查找失败，数据库发生未知错误！");
        }
        return count;
    }

    /**
     * complaint to complaintVO（填充投诉人、被投诉人、处罚账号信息）
     * @param complaint     complaint
     * @return              complaintVO
     */
    private ComplaintVO toComplaintVO(Complaint complaint) {
        ComplaintVO complaintVO = complaint.toComplaintVO();
        // 填充投诉人账号信息
        if (complaintVO.getComplainantAccountType().equals(Complaint.COMPLAIN_ACCOUNT_TYPE_USER)) {
            User complainantUser = null;
            try {
                complainantUser = userMapper.findById(complaintVO.getComplainantAccountId());
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("User 查找失败，数据库发生未知错误！complainantUserId = " + complaintVO.getComplainantAccountId());
                throw new FindFailedException("查找失败，数据库发生未知错误！");
            }
            if (complainantUser != null) {
                complaintVO.setComplainantUsername(complainantUser.getUsername());
                complaintVO.setComplainantAvatarPath(complainantUser.getAvatarPath());
            }
        } else if (complaintVO.getComplainantAccountType().equals(Complaint.COMPLAIN_ACCOUNT_TYPE_STADIUM_MANAGER)) {
            StadiumManager complainantStadiumManager = null;
            try {
                complainantStadiumManager = stadiumManagerMapper.findById(complaintVO.getComplainantAccountId());
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("StadiumManager 查找失败，数据库发生未知错误！complainantStadiumManagerId = " + complaintVO.getComplainantAccountId());
                throw new FindFailedException("查找失败，数据库发生未知错误！");
            }
            if (complainantStadiumManager != null) {
                complaintVO.setComplainantUsername(complainantStadiumManager.getUsername());
                complaintVO.setComplainantAvatarPath(complainantStadiumManager.getAvatarPath());
            }
        }
        // 填充被投诉人账号信息
        if (complaintVO.getRespondentAccountType().equals(Complaint.COMPLAIN_ACCOUNT_TYPE_USER)) {
            User respondentUser = null;
            try {
                respondentUser = userMapper.findById(complaintVO.getRespondentAccountId());
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("User 查找失败，数据库发生未知错误！respondentUserId = " + complaintVO.getRespondentAccountId());
                throw new FindFailedException("查找失败，数据库发生未知错误！");
            }
            if (respondentUser != null) {
                complaintVO.setRespondentUsername(respondentUser.getUsername());
                complaintVO.setRespondentAvatarPath(respondentUser.getAvatarPath());
            }
        } else if (complaintVO.getRespondentAccountType().equals(Complaint.COMPLAIN_ACCOUNT_TYPE_STADIUM_MANAGER)) {
            StadiumManager respondentStadiumManager = null;
            try {
                respondentStadiumManager = stadiumManagerMapper.findById(complaintVO.getRespondentAccountId());
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("StadiumManager 查找失败，数据库发生未知错误！respondentStadiumManagerId = " + complaintVO.getRespondentAccountId());
                throw new FindFailedException("查找失败，数据库发生未知错误！");
            }
            if (respondentStadiumManager != null) {
                complaintVO.setRespondentUsername(respondentStadiumManager.getUsername());
                complaintVO.setRespondentAvatarPath(respondentStadiumManager.getAvatarPath());
            }
        }
        if (complaintVO.getPunishAccountType() != null) {
            // 填充被处罚账号信息
            if (complaintVO.getPunishAccountType().equals(Complaint.COMPLAIN_ACCOUNT_TYPE_USER)) {
                User punishUser = null;
                try {
                    punishUser = userMapper.findById(complaintVO.getPunishAccountId());
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.warn("User 查找失败，数据库发生未知错误！punishUserId = " + complaintVO.getPunishAccountId());
                    throw new FindFailedException("查找失败，数据库发生未知错误！");
                }
                if (punishUser != null) {
                    complaintVO.setPunishUsername(punishUser.getUsername());
                    complaintVO.setPunishAvatarPath(punishUser.getAvatarPath());
                }
            } else if (complaintVO.getPunishAccountType().equals(Complaint.COMPLAIN_ACCOUNT_TYPE_STADIUM_MANAGER)) {
                StadiumManager punishStadiumManager = null;
                try {
                    punishStadiumManager = stadiumManagerMapper.findById(complaintVO.getPunishAccountId());
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.warn("StadiumManager 查找失败，数据库发生未知错误！punishStadiumManagerId = " + complaintVO.getPunishAccountId());
                    throw new FindFailedException("查找失败，数据库发生未知错误！");
                }
                if (punishStadiumManager != null) {
                    complaintVO.setPunishUsername(punishStadiumManager.getUsername());
                    complaintVO.setPunishAvatarPath(punishStadiumManager.getAvatarPath());
                }
            }
        }
        return complaintVO;
    }
}
