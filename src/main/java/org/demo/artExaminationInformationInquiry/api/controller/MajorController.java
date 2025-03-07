package org.demo.artExaminationInformationInquiry.api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.demo.artExaminationInformationInquiry.api.entity.Major;
import org.demo.artExaminationInformationInquiry.api.service.IMajorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-02-28
 */
@RestController
@RequestMapping("/api/major")
public class MajorController {
    IMajorService majorService;
    MajorController(IMajorService majorService) {
        this.majorService = majorService;
    }
    Logger logger = LoggerFactory.getLogger(MajorController.class);
    @GetMapping("/selectMajorListByName")
    public Page<Major> selectMajorListByName(@RequestParam("name") String name, @RequestParam("page") Integer pageNum, @RequestParam("size") Integer pageSize) {
        logger.debug("name: {} pageNum: {} pageSize: {}", name, pageNum, pageSize);
        return majorService.selectMajorListByName(name, pageNum, pageSize);
    }

    @PutMapping("/updateMajor")
    public boolean updateMajor(@RequestBody Major major) {
        logger.debug("major: {}", major);
        return majorService.updateById(major);
    }
    @DeleteMapping("/deleteMajor")
    public boolean deleteMajor(@RequestParam("id") Long id) {
        logger.debug("id: {}", id);
        return majorService.removeById(id);
    }
    @PostMapping("/insertMajor")
    public boolean insertMajor(@RequestBody Major major) {
        logger.debug("major: {}", major);
        return majorService.save(major);
    }
}
