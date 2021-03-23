package cn.edu.hestyle.bookstadium.mapper;

import cn.edu.hestyle.bookstadium.entity.Notice;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/23 4:47 下午
 */
@Mapper
public interface NoticeMapper {
    /**
     * 通过toAccountType、accountId分页查找Notice
     * @param toAccountType     toAccountType
     * @param accountId         accountId
     * @param beginIndex        beginIndex
     * @param pageSize          pageSize
     * @return                  List Notice
     */
    List<Notice> findByIdAndPage(Integer toAccountType, Integer accountId, Integer beginIndex, Integer pageSize);
}
