package org.demo.artExaminationInformationInquiry.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.demo.artExaminationInformationInquiry.api.entity.University;
import org.demo.artExaminationInformationInquiry.api.entity.UniversityCollection;
import org.demo.artExaminationInformationInquiry.api.service.IUniversityCollectionService;
import org.demo.artExaminationInformationInquiry.api.service.IUniversityService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-03-24
 */
@Slf4j
@RestController
@RequestMapping("/api/universityCollection")
public class UniversityCollectionController {
    private final IUniversityCollectionService universityCollectionService;
    private final IUniversityService universityService;
    UniversityCollectionController(IUniversityCollectionService universityCollectionService, IUniversityService universityService){
        this.universityCollectionService = universityCollectionService;
        this.universityService = universityService;
    }
    @PostMapping("/insert")
    public String insert(UniversityCollection universityCollection){
        log.debug("universityCollection: {}", universityCollection);
        universityCollectionService.save(universityCollection);
        return "success";
    }
    @PostMapping("/selectByUserIdUniversityId")
    public UniversityCollection selectCollectionByUserIdUniversityId(@Param("usersId") Long usersId, @Param("universityId") Long universityId){
        return universityCollectionService.lambdaQuery().eq(UniversityCollection::getUsersId,usersId).eq(UniversityCollection::getUniversityId,universityId).one();
    }
    @PostMapping("/delete")
    public String delete(@Param("usersId") Long usersId, @Param("universityId") Long universityId){
        QueryWrapper<UniversityCollection> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("users_id", usersId);
        queryWrapper.eq("university_id", universityId);
        universityCollectionService.remove(queryWrapper);
        return "success";
    }
    @PostMapping("/selectByUserIdAndUniversityName")
    public Page<University> selectByUserId(Long usersId,String universityName ,int page, int size){

        log.debug("usersId: {},university{},pageNum{},pageSize{}", usersId,universityName,page,size);
        return  universityService.lambdaQuery()
                .like(University::getUniversityName, universityName)  // 添加主查询的模糊查询条件
                .inSql(University::getUniversityId,
                        "SELECT university_id FROM university_collection WHERE users_id = " + usersId)  // 修复参数占位符
                .page(new Page<>(page, size));
    }
}
