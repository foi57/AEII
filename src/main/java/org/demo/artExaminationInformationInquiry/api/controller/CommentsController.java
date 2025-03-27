package org.demo.artExaminationInformationInquiry.api.controller;

import org.demo.artExaminationInformationInquiry.api.entity.Comments;
import org.demo.artExaminationInformationInquiry.api.service.ICommentsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.demo.artExaminationInformationInquiry.util.uploadFileUtil;

import java.io.IOException;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-03-27
 */
@RestController
@RequestMapping("/api/comments")
public class CommentsController {
    ICommentsService commentsService;
    CommentsController(ICommentsService commentsService){
        this.commentsService = commentsService;
    }
    @PostMapping("/insert")
    public Boolean insertComment(Comments comments) {
        return commentsService.insertComment(comments);
    }
    @PostMapping("/upload/img")
    public String uploadImg(MultipartFile file) throws IOException {
        String dir="/file/comment";
        uploadFileUtil uploadFileUtil = new uploadFileUtil();
        try {
           return uploadFileUtil.upload(file, dir);

        }catch (IOException e){
            return "上传失败";
        }

    }
}
