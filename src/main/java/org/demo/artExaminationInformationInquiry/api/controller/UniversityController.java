package org.demo.artExaminationInformationInquiry.api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-03-04
 */
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
        Path uploadPath = Paths.get("E:\\code\\java\\learn\\ArtExaminationInformationInquiry\\file\\university");
        Files.createDirectories(uploadPath);
        Files.copy(file.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);

        // 删除旧文件
        if (universitySchoolBadge != null) {
            Path oldFilePath = uploadPath.resolve("E:\\code\\java\\learn\\ArtExaminationInformationInquiry\\file\\university\\"+universitySchoolBadge);
            if (Files.exists(oldFilePath)) {
                Files.delete(oldFilePath);
            }
        }


        // 更新数据库记录
        if(universityId != null) {
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
}
