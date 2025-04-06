package org.demo.artExaminationInformationInquiry.api.service;

import java.util.List;

import org.demo.artExaminationInformationInquiry.api.entity.Comments;
import org.demo.artExaminationInformationInquiry.api.entity.CommentsNotification;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-04-01
 */
public interface ICommentsNotificationService extends IService<CommentsNotification> {
    Boolean insertCommentNotification(CommentsNotification commentsNotification);
    Page<Comments> getCommentNotificationByUsersIdAndCategory(Long usersIds,String catrgory,Integer pageNum,Integer pageSize);
    Boolean markAsRead(Long notificationId);
    Long countCommentsNotificationUnRead(Long user,String category);
    // 新增一键已读所有评论通知方法
    Boolean markAllAsRead(Long userId, String category);
}
