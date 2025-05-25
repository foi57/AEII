package org.demo.artExaminationInformationInquiry.api.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.demo.artExaminationInformationInquiry.api.entity.Major;
import org.demo.artExaminationInformationInquiry.api.entity.MajorCount;
import org.demo.artExaminationInformationInquiry.api.entity.MajorTypeCount;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-02-28
 */
public interface MajorMapper extends BaseMapper<Major> {

    @Select("SELECT major_category AS typeName, COUNT(*) AS typeCount " +
            "FROM major GROUP BY major_category")
    List<MajorTypeCount> selectMajorTypeCount();

    @Select("SELECT major_name AS majorName FROM major " +
            "WHERE major_category = #{category}")
    List<MajorCount> selectMajorsByCategory(@Param("category") String category);

}
