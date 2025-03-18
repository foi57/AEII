package org.demo.artExaminationInformationInquiry.api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.demo.artExaminationInformationInquiry.api.entity.UniversityMajor;
import org.demo.artExaminationInformationInquiry.api.entity.majorUniversityIdsDTO;
import org.demo.artExaminationInformationInquiry.api.service.IUniversityMajorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-03-11
 */
@Slf4j
@RestController
@RequestMapping("/api/universityMajor")
public class UniversityMajorController {
    private final IUniversityMajorService universityMajorService;
    public UniversityMajorController(IUniversityMajorService universityMajorService) {
        this.universityMajorService = universityMajorService;
    }
    @DeleteMapping("/deleteUniversityMajor")
    public void deleteUniversityMajor(Long majorId, Long universityId){
        log.debug("majorId:{},university:{}",majorId,universityId);
        universityMajorService.deleteUniversityMajorByMajorId(majorId,universityId);
    }
    @PostMapping("/insertUniversityMajor")
    public void insertUniversityMajor( @RequestBody majorUniversityIdsDTO dto){
        log.debug("majorNamesDTO:{}",dto);
        List<Long> universityIds = dto.getUniversityIds();
        Long majorId = dto.getMajorId();

        // 创建关联关系示例
        universityIds.forEach(universityId -> {
            UniversityMajor relation = new UniversityMajor();
            relation.setMajorId(majorId);
            relation.setUniversityId(universityId);
            universityMajorService.save(relation);
        });
    }

}
