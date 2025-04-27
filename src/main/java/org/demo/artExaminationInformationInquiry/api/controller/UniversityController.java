package org.demo.artExaminationInformationInquiry.api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.demo.artExaminationInformationInquiry.api.entity.University;
import org.demo.artExaminationInformationInquiry.api.service.IUniversityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;



/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-03-04
 */
@Slf4j
@RestController
@RequestMapping("/api/university")
public class UniversityController {
    IUniversityService universityService;
    UniversityController(IUniversityService universityService) {
        this.universityService = universityService;
    }


    public Page<University> getUniversityListByName(@RequestParam("name") String name, @RequestParam("page") int pageNum, @RequestParam("size") int pageSize){
        return universityService.selectUniversityListByName(name,pageNum,pageSize);
    }

    @GetMapping("/selectUniversityList")
    public Page<University> getUniversityList(@RequestParam("name") String name, @RequestParam("area") String area, @RequestParam("type") String type, @RequestParam("level") String level, @RequestParam("features") String features, @RequestParam("page") int pageNum, @RequestParam("size") int pageSize){
        return universityService.selectUniversityList(name,area,type,level,features,pageNum,pageSize);
    }

    @PostMapping("/uploadImg")
    public ResponseEntity<Map<String, Object>> uploadSchoolBadge(
    @RequestParam("file") MultipartFile file,
    @RequestParam(value = "universityId") Long universityId,
    @RequestParam(value="universitySchoolBadge")String universitySchoolBadge) {

    try {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get(System.getProperty("user.dir") + "\\file\\university\\");
        Files.createDirectories(uploadPath);
        Files.copy(file.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);

        // 删除旧文件
        if (universitySchoolBadge != null && !universitySchoolBadge.trim().isEmpty()) {
            Path oldFilePath = uploadPath.resolve(System.getProperty("user.dir") +"\\file\\university\\"+universitySchoolBadge);
            if (Files.exists(oldFilePath)) {
                Files.delete(oldFilePath);
            }
        }


        // 更新数据库记录
        if(universityId != -1) {
            universityService.lambdaUpdate()
                .eq(University::getUniversityId, universityId)
                .set(University::getUniversitySchoolbadge, fileName)
                .update();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", fileName);
        return ResponseEntity.ok(response);
    } catch (IOException e) {
        log.error("文件上传失败", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "文件上传失败"));
    }
}

    @PutMapping("/updateUniversity")
    public boolean updateUniversity(@RequestBody University university) {
        return universityService.updateById(university);
    }

    @DeleteMapping("/deleteUniversity")
    public boolean deleteUniversity(@RequestParam("id") Long id) {
        return universityService.removeById(id);
    }

    @GetMapping("/selectEstablishmentUniversity")
    public Page<University> selectEstablishmentUniversity(@RequestParam("majorId") Long majorId,@RequestParam("name") String name,@RequestParam("area") String area,@RequestParam("type") String type,
            @RequestParam("level") String level,@RequestParam("features") String features
            ,@RequestParam("page") int pageNum, @RequestParam("size") int pageSize){
        log.debug("majorId:{},name:{},area:{},type:{},level:{},features:{},pagNum:{},pageSize:{}",majorId,name,area,type,level,features,pageNum,pageSize);
        return universityService.selectAffiliatedUniversityList(majorId,name,area,type,level,features,pageNum,pageSize);
    }
    @GetMapping("/selectById")
    public University selectUniversityById(@RequestParam("id") Long id){
        return universityService.getById(id);
    }
    @PostMapping("/insert")
    public String postMethodName(@RequestBody University university) {
        return universityService.insertUniversity(university)?"success":"fail";
    }
    
}
