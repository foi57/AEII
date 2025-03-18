package org.demo.artExaminationInformationInquiry.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.demo.artExaminationInformationInquiry.api.entity.UniversityMajor;
import org.demo.artExaminationInformationInquiry.api.mapper.UniversityMajorMapper;
import org.demo.artExaminationInformationInquiry.api.service.IUniversityMajorService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-03-11
 */
@Service
public class UniversityMajorServiceImpl extends ServiceImpl<UniversityMajorMapper, UniversityMajor> implements IUniversityMajorService {

    @Override
    public Long[] getUniversityIdListByMajorId(Long majorId) {
        return lambdaQuery().eq(UniversityMajor::getMajorId, majorId).list().stream().map(UniversityMajor::getUniversityId).toArray(Long[]::new);
    }

    @Override
    public void deleteUniversityMajorByMajorId(Long majorId, Long universityId) {
        remove(new QueryWrapper<UniversityMajor>()
                .eq("major_id", majorId)
                .eq("university_id", universityId));
    }
}
