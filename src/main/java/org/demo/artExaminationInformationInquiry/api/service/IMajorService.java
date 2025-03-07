package org.demo.artExaminationInformationInquiry.api.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.demo.artExaminationInformationInquiry.api.entity.Major;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-02-28
 */
public interface IMajorService extends IService<Major> {
    Page<Major> selectMajorListByName(String name, int pageNum, int pageSize);
    boolean updateMajor(Major major);
    boolean deleteMajor(Long id);
}
