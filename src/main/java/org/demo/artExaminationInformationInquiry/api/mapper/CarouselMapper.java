package org.demo.artExaminationInformationInquiry.api.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.demo.artExaminationInformationInquiry.api.entity.Carousel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-04-19
 */
@Mapper
public interface CarouselMapper extends BaseMapper<Carousel> {

    /**
     * 获取当前最大的排序值
     * @return 最大排序值，如果表中没有数据或 sort_order 都为 null 则返回 null
     */
    @Select("SELECT MAX(sort_order) FROM carousel")
    Integer getMaxSortOrder();

    @Select("SELECT * FROM carousel WHERE sort_order < #{sortOrder} ORDER BY sort_order DESC LIMIT 1")
    Carousel findPrevious(int sortOrder);

    @Select("SELECT * FROM carousel WHERE sort_order > #{sortOrder} ORDER BY sort_order ASC LIMIT 1")
    Carousel findNext(int sortOrder);
}

