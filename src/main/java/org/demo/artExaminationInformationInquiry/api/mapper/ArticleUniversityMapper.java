package org.demo.artExaminationInformationInquiry.api.mapper;

import org.apache.ibatis.annotations.Param;
import org.demo.artExaminationInformationInquiry.api.entity.ArticleUniversity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;


/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-03-08
 */
public interface ArticleUniversityMapper extends BaseMapper<ArticleUniversity> {
    void batchInsert(@Param("list") List<ArticleUniversity> relations);
}

