package org.demo.artExaminationInformationInquiry.api.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.demo.artExaminationInformationInquiry.api.entity.Feedback;
import org.demo.artExaminationInformationInquiry.api.service.IFeedbackService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-04-29
 */
@RestController
@Slf4j
@RequestMapping("/api/feedback")
public class FeedbackController {
    IFeedbackService feedbackService;  
    FeedbackController(IFeedbackService feedbackService) {
       this.feedbackService = feedbackService; 
    }
    @PostMapping("/upload")
    public String upload(MultipartFile file) {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get(System.getProperty("user.dir") + "\\file\\feedback\\");
        try {
            Files.createDirectories(uploadPath);
            Files.copy(file.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        }catch (Exception e) {
            e.printStackTrace();
            return "上传失败"; 
        }
       
    }
    @PostMapping("/insert")
    public Boolean insert(Feedback feedback) {
        return feedbackService.insertFeedback(feedback);
    }

    @GetMapping("/select")
    public Page<Feedback> select(int pageNum, int pageSize) {
        return feedbackService.selectFeedbackPage(pageNum,pageSize);
    }
    
}
