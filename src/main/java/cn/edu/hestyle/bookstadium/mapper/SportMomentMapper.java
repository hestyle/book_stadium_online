package cn.edu.hestyle.bookstadium.mapper;

import cn.edu.hestyle.bookstadium.entity.SportMoment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/16 4:47 下午
 */
@Mapper
public interface SportMomentMapper {

    /**
     * 分页查询
     * @param beginIndex        beginIndex
     * @param pageSize          pageSize
     * @return                  List SportMoment
     */
    List<SportMoment> findByPage(Integer beginIndex, Integer pageSize);
}
