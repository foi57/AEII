package org.demo.artExaminationInformationInquiry.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.demo.artExaminationInformationInquiry.api.entity.Major;
import org.demo.artExaminationInformationInquiry.api.mapper.MajorMapper;
import org.demo.artExaminationInformationInquiry.api.service.IMajorService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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
}
