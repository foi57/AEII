package org.demo.artExaminationInformationInquiry.api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;

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
@Slf4j
@RequestMapping("/api/comments")
public class CommentsController {
    ICommentsService commentsService;
    CommentsController(ICommentsService commentsService){
        this.commentsService = commentsService;
    }
    @PostMapping("/insert")
    public Comments insertComment(Comments comments) {
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
    @PostMapping("/selectListByArticleId")
    public Page<Comments> selectListByArticleId(Long articleId, Integer pageNum, Integer pageSize,Long usersId) {
        return commentsService.selectCommentListByArticleId(articleId, pageNum, pageSize,usersId);
    }

    @PostMapping("/delete")
    public Boolean deleteById(Long commentId) {
        return commentsService.deleteCommentById(commentId);
    }

    @PostMapping("/thumbsUp")
    public Boolean thumbsUp(Long commentId,Long usersId) {
        return commentsService.thumbsUp(commentId,usersId);
    }

    @PostMapping("/cancelThumbsUp")
    public Boolean cancelThumbsUp(Long commentId,Long usersId) {
        return commentsService.cancelThumbsUp(commentId,usersId);
    }

    @PostMapping("/selectListByUserId")
    public Page<Comments> selectListByUserId(Long usersId,Integer pageNum,Integer pageSize) {
        log.debug("usersId:{},pageNum:{},pageSize:{}",usersId,pageNum,pageSize);
        Page<Comments> commentsPage = commentsService.selectCommentsByUsersId(usersId, pageNum, pageSize); 
        log.debug("commentsPage:{}",commentsPage);
        return commentsPage;
    }
    
    @PostMapping("/selectById")
    public Comments postMethodName(Long id) {
        return commentsService.selectCommentById(id);
    }
    
}
