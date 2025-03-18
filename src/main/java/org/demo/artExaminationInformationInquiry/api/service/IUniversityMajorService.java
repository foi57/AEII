package org.demo.artExaminationInformationInquiry.api.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.demo.artExaminationInformationInquiry.api.entity.UniversityMajor;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-03-11
 */
public interface IUniversityMajorService extends IService<UniversityMajor> {
    Long[] getUniversityIdListByMajorId(Long majorId);
    void deleteUniversityMajorByMajorId(Long majorId,Long universityId);
}
