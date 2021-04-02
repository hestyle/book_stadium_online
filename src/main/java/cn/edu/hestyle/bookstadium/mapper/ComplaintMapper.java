package cn.edu.hestyle.bookstadium.mapper;

import cn.edu.hestyle.bookstadium.entity.Complaint;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/4/2 10:23 上午
 */
@Mapper
public interface ComplaintMapper {

    /**
     * 查找所有的Complaint（未删除）
     * @param beginIndex        beginIndex
     * @param pageSize          pageSize
     * @return                  List Complaint
     */
    List<Complaint> findAllByPage(Integer beginIndex, Integer pageSize, String titleKey);

    /**
     * 获取所有未删除的Complaint数量
     * @return                  未删除的Complaint数量
     */
    Integer getAllCount(String titleKey);
}
