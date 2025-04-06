package org.demo.artExaminationInformationInquiry.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;

import org.demo.artExaminationInformationInquiry.api.entity.Comments;
import org.demo.artExaminationInformationInquiry.api.service.ICommentsNotificationService;
import org.springframework.web.bind.annotation.PostMapping;


/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-04-01
 */
@Slf4j
 @RestController
@RequestMapping("/api/commentsNotification")
public class CommentsNotificationController {
    ICommentsNotificationService commentsNotificationService;
    CommentsNotificationController(final ICommentsNotificationService commentsNotificationService){
        this.commentsNotificationService = commentsNotificationService;
    }

    @PostMapping("/selectByUsersIdCategory")
    public Page<Comments> selectByUsersIdsCategory(Long usersId, final String category,final int pageNum,final int pageSize) {
        log.debug("usersId:{},category:{},pageNum:{},pageSize:{}",usersId,category,pageNum,pageSize);
       return commentsNotificationService.getCommentNotificationByUsersIdAndCategory(usersId, category,pageNum,pageSize);
    }
    
    @PostMapping("/markAsRead")
    public Boolean postMethodName(Long notificationId) {
       return commentsNotificationService.markAsRead(notificationId);
    }
    
    @PostMapping("/markAllAsRead")
    public Boolean markAllAsRead(Long usersId, String category) {
        return commentsNotificationService.markAllAsRead(usersId, category);
    }
    
    @PostMapping("/countUnRead")
    public Long countCommentsNotificationUnRead(Long usersId, final String category) {
       return commentsNotificationService.countCommentsNotificationUnRead(usersId,category);
    }
}
