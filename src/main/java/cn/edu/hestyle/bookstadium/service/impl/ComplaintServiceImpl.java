package cn.edu.hestyle.bookstadium.service.impl;

import cn.edu.hestyle.bookstadium.entity.*;
import cn.edu.hestyle.bookstadium.mapper.ComplaintMapper;
import cn.edu.hestyle.bookstadium.mapper.NoticeMapper;
import cn.edu.hestyle.bookstadium.mapper.StadiumManagerMapper;
import cn.edu.hestyle.bookstadium.mapper.UserMapper;
import cn.edu.hestyle.bookstadium.service.IComplaintService;
import cn.edu.hestyle.bookstadium.service.exception.DeleteFailedException;
import cn.edu.hestyle.bookstadium.service.exception.FindFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/4/2 11:04 上午
 */
@Service
public class ComplaintServiceImpl implements IComplaintService {
    /** 投诉人处理title、content */
    private static final String COMPLAINANT_HANDLE_TITLE = "投诉处理";
    private static final String COMPLAINANT_HANDLE_CONTENT = "系统已处理您的投诉【%s】，积分奖惩【%s】，奖惩描述【%s】";
    /** 被投诉人处理title、content */
    private static final String RESPONDENT_HANDLE_TITLE = "投诉处理";
    private static final String RESPONDENT_HANDLE_CONTENT = "您的账号被其他用户投诉【%s...】，积分奖惩【%s】，奖惩描述【%s】";
    /** （未处理）投诉删除title、content */
    private static final String RESPONDENT_DELETE_TITLE = "投诉删除";
    private static final String RESPONDENT_DELETE_CONTENT = "您的投诉【%s】已被系统删除，删除原因【%s】";
    private static final Logger logger = LoggerFactory.getLogger(ComplaintServiceImpl.class);

    @Resource
    private NoticeMapper noticeMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private StadiumManagerMapper stadiumManagerMapper;
    @Resource
    private ComplaintMapper complaintMapper;

    @Override
    @Transactional
    public void systemManagerHandle(Complaint complaint) {
        if (complaint == null || complaint.getId() == null) {
            logger.warn("Complaint 处理失败，未指定需要处理的投诉！");
            throw new FindFailedException("操作失败，未指定需要处理的投诉！");
        }
        Complaint complaintModify = null;
        try {
            complaintModify = complaintMapper.findById(complaint.getId());
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Complaint 查找失败，数据库发生未知错误！complaintId = " + complaint.getId());
            throw new FindFailedException("查找失败，数据库发生未知错误！");
        }
        if (complaintModify == null) {
            logger.warn("Complaint 处理失败，不存在该投诉！complaint = " + complaint);
            throw new FindFailedException("操作失败，不存在该投诉！");
        }
        if (complaintModify.getHasHandled() != 0) {
            logger.warn("Complaint 尝试处理已经处理的投诉！complaintModify = " + complaintModify);
            throw new FindFailedException("操作失败，该投诉已经处理！");
        }
        // 投诉人处理
        Integer complainantHandleCreditScore = complaint.getComplainantHandleCreditScore();
        if (complainantHandleCreditScore == null) {
            logger.warn("Complaint 未填写投诉人的积分奖惩！complaint = " + complaint);
            throw new FindFailedException("操作失败，未填写投诉人的积分奖惩！");
        }
        String complainantHandleDescription = complaint.getComplainantHandleDescription();
        if (complainantHandleDescription == null || complainantHandleDescription.length() == 0) {
            logger.warn("Complaint 未填写投诉人的处理描述！complaint = " + complaint);
            throw new FindFailedException("操作失败，未填写投诉人的处理描述！");
        }
        // Complainant积分更新
        complaintModify.setComplainantHandleCreditScore(complainantHandleCreditScore);
        complaintModify.setComplainantHandleDescription(complainantHandleDescription);
        if (!complainantHandleCreditScore.equals(0)) {
            // 投诉人的积分发生了变化，更新user/stadiumManager表
            if (complaintModify.getComplainantAccountType().equals(Complaint.COMPLAIN_ACCOUNT_TYPE_USER)) {
                User complainantUser = null;
                try {
                    complainantUser = userMapper.findById(complaintModify.getComplainantAccountId());
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.warn("User 查找失败，数据库发生未知错误！complainantUserId = " + complaintModify.getComplainantAccountId());
                    throw new FindFailedException("操作失败，数据库发生未知错误！");
                }
                if (complainantUser != null) {
                    complainantUser.setCreditScore(complainantUser.getCreditScore() + complaintModify.getComplainantHandleCreditScore());
                    complainantUser.setModifiedUser("System");
                    complainantUser.setModifiedTime(new Date());
                    try {
                        userMapper.update(complainantUser);
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.warn("User 更新失败，数据库发生未知错误！complainantUser = " + complainantUser);
                        throw new FindFailedException("操作失败，数据库发生未知错误！");
                    }
                    logger.warn("User 更新成功！complainantUser = " + complainantUser);
                }
            } else if (complaintModify.getComplainantAccountType().equals(Complaint.COMPLAIN_ACCOUNT_TYPE_STADIUM_MANAGER)) {
                StadiumManager complainantStadiumManager = null;
                try {
                    complainantStadiumManager = stadiumManagerMapper.findById(complaintModify.getComplainantAccountId());
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.warn("StadiumManager 查找失败，数据库发生未知错误！complainantStadiumManager = " + complaintModify.getComplainantAccountId());
                    throw new FindFailedException("操作失败，数据库发生未知错误！");
                }
                if (complainantStadiumManager != null) {
                    complainantStadiumManager.setCreditScore(complainantStadiumManager.getCreditScore() + complaintModify.getComplainantHandleCreditScore());
                    complainantStadiumManager.setModifiedUser("System");
                    complainantStadiumManager.setModifiedTime(new Date());
                    try {
                        stadiumManagerMapper.update(complainantStadiumManager);
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.warn("StadiumManager 更新失败，数据库发生未知错误！complainantStadiumManager = " + complainantStadiumManager);
                        throw new FindFailedException("操作失败，数据库发生未知错误！");
                    }
                    logger.warn("StadiumManager 更新成功！complainantStadiumManager = " + complainantStadiumManager);
                }
            }
        }
        // 发送投诉处理通知
        Notice complainantNotice = new Notice();
        complainantNotice.setToAccountType(complaintModify.getComplainantAccountType());
        complainantNotice.setAccountId(complaintModify.getComplainantAccountId());
        complainantNotice.setTitle(COMPLAINANT_HANDLE_TITLE);
        complainantNotice.setContent(String.format(COMPLAINANT_HANDLE_CONTENT, complaintModify.getTitle(), complaintModify.getComplainantHandleCreditScore(), complaintModify.getComplainantHandleDescription()));
        complainantNotice.setGeneratedTime(new Date());
        complainantNotice.setIsDelete(0);
        try {
            noticeMapper.add(complainantNotice);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Notice 添加失败，数据库发生未知错误！complainantNotice = " + complainantNotice);
            throw new FindFailedException("操作失败，数据库发生未知错误！");
        }
        // 被投诉人处理
        Integer respondentHandleCreditScore = complaint.getRespondentHandleCreditScore();
        if (respondentHandleCreditScore == null) {
            logger.warn("Complaint 未填写被投诉人的积分奖惩！complaint = " + complaint);
            throw new FindFailedException("操作失败，未填写被投诉人的积分奖惩！");
        }
        String respondentHandleDescription = complaint.getRespondentHandleDescription();
        if (!respondentHandleCreditScore.equals(0) && (respondentHandleDescription == null || respondentHandleDescription.length() == 0)) {
            logger.warn("Complaint 未填写被投诉人的处理描述！complaint = " + complaint);
            throw new FindFailedException("操作失败，未填写被投诉人的处理描述！");
        }
        // Complainant积分更新
        complaintModify.setRespondentHandleCreditScore(respondentHandleCreditScore);
        complaintModify.setRespondentHandleDescription(respondentHandleDescription);
        if (!respondentHandleCreditScore.equals(0)) {
            // 被投诉人的积分发生了变化，更新user/stadiumManager表
            if (complaintModify.getRespondentAccountType().equals(Complaint.COMPLAIN_ACCOUNT_TYPE_USER)) {
                User respondentUser = null;
                try {
                    respondentUser = userMapper.findById(complaintModify.getRespondentAccountId());
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.warn("User 查找失败，数据库发生未知错误！respondentUserId = " + complaintModify.getRespondentAccountId());
                    throw new FindFailedException("操作失败，数据库发生未知错误！");
                }
                if (respondentUser != null) {
                    respondentUser.setCreditScore(respondentUser.getCreditScore() + complaintModify.getRespondentHandleCreditScore());
                    respondentUser.setModifiedUser("System");
                    respondentUser.setModifiedTime(new Date());
                    try {
                        userMapper.update(respondentUser);
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.warn("User 更新失败，数据库发生未知错误！respondentUser = " + respondentUser);
                        throw new FindFailedException("操作失败，数据库发生未知错误！");
                    }
                    logger.warn("User 更新成功！respondentUser = " + respondentUser);
                }
            } else if (complaintModify.getRespondentAccountType().equals(Complaint.COMPLAIN_ACCOUNT_TYPE_STADIUM_MANAGER)) {
                StadiumManager respondentStadiumManager = null;
                try {
                    respondentStadiumManager = stadiumManagerMapper.findById(complaintModify.getRespondentAccountId());
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.warn("StadiumManager 查找失败，数据库发生未知错误！respondentStadiumManagerId = " + complaintModify.getRespondentAccountId());
                    throw new FindFailedException("操作失败，数据库发生未知错误！");
                }
                if (respondentStadiumManager != null) {
                    respondentStadiumManager.setCreditScore(respondentStadiumManager.getCreditScore() + complaintModify.getRespondentAccountId());
                    respondentStadiumManager.setModifiedUser("System");
                    respondentStadiumManager.setModifiedTime(new Date());
                    try {
                        stadiumManagerMapper.update(respondentStadiumManager);
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.warn("StadiumManager 更新失败，数据库发生未知错误！respondentStadiumManager = " + respondentStadiumManager);
                        throw new FindFailedException("操作失败，数据库发生未知错误！");
                    }
                    logger.warn("StadiumManager 更新成功！respondentStadiumManager = " + respondentStadiumManager);
                }
            }
        }
        if (respondentHandleDescription != null && respondentHandleDescription.length() != 0) {
            // 发送被投诉处理通知
            Notice respondentNotice = new Notice();
            respondentNotice.setToAccountType(complaintModify.getRespondentAccountType());
            respondentNotice.setAccountId(complaintModify.getRespondentAccountId());
            respondentNotice.setTitle(RESPONDENT_HANDLE_TITLE);
            respondentNotice.setContent(String.format(RESPONDENT_HANDLE_CONTENT, complaintModify.getDescription(), complaintModify.getRespondentHandleCreditScore(), complaintModify.getRespondentHandleDescription()));
            respondentNotice.setGeneratedTime(new Date());
            respondentNotice.setIsDelete(0);
            try {
                noticeMapper.add(respondentNotice);
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("Notice 添加失败，数据库发生未知错误！respondentNotice = " + respondentNotice);
                throw new FindFailedException("操作失败，数据库发生未知错误！");
            }
            logger.warn("Notice 添加成功！respondentNotice = " + respondentNotice);
        }
        // 更新
        complaintModify.setHasHandled(1);
        complaintModify.setHandledTime(new Date());
        try {
            complaintMapper.update(complaintModify);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Complaint 更新失败，数据库发生未知错误！complaintModify = " + complaintModify);
            throw new FindFailedException("操作失败，数据库发生未知错误！");
        }
        logger.warn("Complaint 更新成功！complaintModify = " + complaintModify);
    }

    @Override
    @Transactional
    public void systemManagerDeleteById(Integer complaintId, String deleteReason) {
        if (complaintId == null) {
            logger.warn("Complaint 删除失败，未指定需要处理的投诉！");
            throw new DeleteFailedException("操作失败，未指定需要删除的投诉！");
        }
        Complaint complaint = null;
        try {
            complaint = complaintMapper.findById(complaintId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Complaint 查找失败，数据库发生未知错误！complaintId = " + complaintId);
            throw new FindFailedException("查找失败，数据库发生未知错误！");
        }
        if (complaint == null) {
            logger.warn("Complaint 删除失败，不存在该投诉！complaintId = " + complaintId);
            throw new FindFailedException("操作失败，不存在该投诉！");
        }
        if (deleteReason == null || deleteReason.length() == 0) {
            logger.warn("Complaint 删除失败，未设置删除原因！");
            throw new FindFailedException("操作失败，未设置删除原因！");
        }
        if (complaint.getHasHandled() != 0) {
            // 当前complaint未处理，通知投诉人
            Notice complainantNotice = new Notice();
            complainantNotice.setToAccountType(complaint.getComplainantAccountType());
            complainantNotice.setAccountId(complaint.getComplainantAccountId());
            complainantNotice.setTitle(RESPONDENT_DELETE_TITLE);
            complainantNotice.setContent(String.format(RESPONDENT_DELETE_CONTENT, complaint.getTitle(), deleteReason));
            complainantNotice.setGeneratedTime(new Date());
            complainantNotice.setIsDelete(0);
            try {
                noticeMapper.add(complainantNotice);
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("Notice 添加失败，数据库发生未知错误！complainantNotice = " + complainantNotice);
                throw new FindFailedException("操作失败，数据库发生未知错误！");
            }
            logger.warn("Notice 添加成功！complainantNotice = " + complainantNotice);
        }
        complaint.setIsDelete(1);
        try {
            complaintMapper.update(complaint);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Complaint 更新失败，数据库发生未知错误！complaint = " + complaint);
            throw new FindFailedException("操作失败，数据库发生未知错误！");
        }
        logger.warn("Complaint 更新成功！complaint = " + complaint);
    }

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
        return complaintVO;
    }
}
