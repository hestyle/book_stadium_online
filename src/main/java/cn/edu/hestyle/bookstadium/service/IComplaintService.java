package cn.edu.hestyle.bookstadium.service;

import cn.edu.hestyle.bookstadium.entity.ComplaintVO;

import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/4/2 10:54 上午
 */
public interface IComplaintService {
    /**
     * 分页查询Complaint（未删除）
     * @param pageIndex     pageIndex
     * @param pageSize      pageSize
     * @return              List ComplaintVO
     */
    List<ComplaintVO> systemManagerFindAllByPage(Integer pageIndex, Integer pageSize, String titleKey);

    /**
     * 获取所有未删除的Complaint数量
     * @return              未删除的Complaint数量
     */
    Integer getAllCount(String titleKey);
}
