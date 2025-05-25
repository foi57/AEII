package org.demo.artExaminationInformationInquiry.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.demo.artExaminationInformationInquiry.api.entity.Major;
import org.demo.artExaminationInformationInquiry.api.entity.MajorCount;
import org.demo.artExaminationInformationInquiry.api.entity.MajorTypeCount;
import org.demo.artExaminationInformationInquiry.api.mapper.MajorMapper;
import org.demo.artExaminationInformationInquiry.api.service.IMajorService;
import org.demo.artExaminationInformationInquiry.api.service.IUniversityMajorService;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-02-28
 */
@Service
public class MajorServiceImpl extends ServiceImpl<MajorMapper, Major> implements IMajorService {
    IUniversityMajorService universityMajorService;

    MajorServiceImpl(IUniversityMajorService universityMajorService){
        this.universityMajorService = universityMajorService;
    }

    @Override
    public Page<Major> selectMajorListByName(String name, int pageNum, int pageSize) {
        Page<Major> page = new Page<>(pageNum, pageSize);
        return lambdaQuery().like(Major::getMajorName, name).page(page);
    }

    @Override
    public boolean updateMajor(Major major) {
        return updateById(major);
    }

    @Override
    public boolean deleteMajor(Long id) {
        return removeById(id);
    }

    @Override
    public List<Major> selectMajorListByUniversityId(Long universityId) {
        return lambdaQuery()
                .exists("SELECT 1 FROM university_major mu WHERE mu.major_id = major.major_id AND mu.university_id = {0}", universityId)
                .list();
    }

    @Override
    public List<MajorCount> selectMajorListCount() {
       List<Major> MajorList = list();
       List<MajorCount> MajorCountList = new ArrayList<>();
        MajorList.forEach(major -> {
            MajorCount majorCount = new MajorCount();
            majorCount.setMajorName(major.getMajorName());
            majorCount.setCount(universityMajorService.getUniversityIdListByMajorId(major.getMajorId()).length);
            MajorCountList.add(majorCount);
        });
        return MajorCountList;
    }

    @Override
    public List<MajorTypeCount> selectMajorTypeCount() {
        // 1. 使用自定义SQL查询类型统计
        List<MajorTypeCount> typeList = baseMapper.selectMajorTypeCount();
        
        // 2. 为每个类型查询下属专业及数量
        typeList.forEach(type -> {
            List<MajorCount> majors = baseMapper.selectMajorsByCategory(type.getTypeName());
            type.setMajors(majors);
        });
        
        return typeList;
    }

    @Override
    public Page<Major> selectMajorListByNameAndcategory(String name, String category, int pageNum, int pageSize) {
        Page<Major> page = new Page<>(pageNum, pageSize);
        return lambdaQuery().like(Major::getMajorName, name).like(Major::getMajorCategory, category).page(page);
    }

}
