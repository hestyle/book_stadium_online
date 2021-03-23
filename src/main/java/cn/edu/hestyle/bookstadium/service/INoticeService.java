package cn.edu.hestyle.bookstadium.service;

import cn.edu.hestyle.bookstadium.entity.Notice;

import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/23 4:55 下午
 */
public interface INoticeService {
    /**
     * 通过toAccountType、accountId分页查找Notice
     * @param toAccountType     toAccountType
     * @param accountId         accountId
     * @param pageIndex         pageIndex
     * @param pageSize          pageSize
     * @return                  List Notice
     */
    List<Notice> findByIdAndPage(Integer toAccountType, Integer accountId, Integer pageIndex, Integer pageSize);
}
