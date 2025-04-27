package org.demo.artExaminationInformationInquiry.api.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.demo.artExaminationInformationInquiry.api.entity.University;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-03-04
 */
public interface IUniversityService extends IService<University> {
    Page<University> selectUniversityListByName(String name, int pageNum, int pageSize);
    Page<University> selectUniversityList(String name, String area,String type,String level,String features,int pageNum, int pageSize);
    Page<University> selectAffiliatedUniversityList(Long majorId,String name, String area,String type,String level,String features,int pageNum, int pageSize);
    University selectUniversityByName(String name);
    Boolean insertUniversity(University university);
}
