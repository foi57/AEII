package org.demo.artExaminationInformationInquiry.api.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.demo.artExaminationInformationInquiry.api.entity.University;
import org.demo.artExaminationInformationInquiry.api.entity.UniversityMajor;
import org.demo.artExaminationInformationInquiry.api.mapper.UniversityMapper;
import org.demo.artExaminationInformationInquiry.api.service.IUniversityMajorService;
import org.demo.artExaminationInformationInquiry.api.service.IUniversityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-03-04
 */
@Service
public class UniversityServiceImpl extends ServiceImpl<UniversityMapper, University> implements IUniversityService {

    private final IUniversityMajorService universityMajorService;
    UniversityServiceImpl(IUniversityMajorService universityMajorService){
        this.universityMajorService = universityMajorService;
    }

    @Override
    public Page<University> selectUniversityListByName(String name, int pageNum, int pageSize) {
        Page<University> page = new Page<>(pageNum, pageSize);
        return lambdaQuery().like(University::getUniversityName, name).page(page);
    }

    @Override
    public Page<University> selectUniversityList(String name, String area, String level,String type, String features, int pageNum, int pageSize) {
        Page<University> page = new Page<>(pageNum, pageSize);
        return lambdaQuery().like(University::getUniversityName, name).like(University::getUniversityArea, area).like(University::getUniversityType, type).like(University::getUniversityLevel, level).like(University::getUniversityFeatures, features).page(page);
    }

    @Transactional
    @Override
    public Page<University> selectAffiliatedUniversityList(Long majorId, String name, String area, String type, String level, String features, int pageNum, int pageSize) {
       Long[] universityIdList = universityMajorService.getUniversityIdListByMajorId(majorId);
        Page<University> page = new Page<>(pageNum, pageSize);
        return lambdaQuery().in(University::getUniversityId,  universityIdList).like(University::getUniversityName, name).like(University::getUniversityArea, area).like(University::getUniversityType, type).like(University::getUniversityLevel, level).like(University::getUniversityFeatures, features).page(page);
    }


}
