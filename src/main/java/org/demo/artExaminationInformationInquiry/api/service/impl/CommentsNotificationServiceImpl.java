package org.demo.artExaminationInformationInquiry.api.service.impl;

import java.util.List;

import org.demo.artExaminationInformationInquiry.api.entity.Comments;
import org.demo.artExaminationInformationInquiry.api.entity.CommentsNotification;
import org.demo.artExaminationInformationInquiry.api.mapper.CommentsNotificationMapper;
import org.demo.artExaminationInformationInquiry.api.service.ICommentsNotificationService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.demo.artExaminationInformationInquiry.api.service.ICommentsService;
/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-04-01
 */
@Service
@Slf4j
public class CommentsNotificationServiceImpl extends ServiceImpl<CommentsNotificationMapper, CommentsNotification> implements ICommentsNotificationService {
    
    private final ICommentsService commentsService;
    
    public CommentsNotificationServiceImpl(@Lazy ICommentsService commentsService) {
        this.commentsService = commentsService;
    }
    @Override
    public Boolean insertCommentNotification(CommentsNotification commentsNotification) {
        // 插入评论通知
        return save(commentsNotification);
    }

    @Override
    public Page<Comments> getCommentNotificationByUsersIdAndCategory(Long userId, String category,
            Integer pageNum, Integer pageSize) {
        // 根据用户id和分类查询评论通知
        Page<CommentsNotification> page = new Page<>(pageNum, pageSize);
        
        // 使用MybatisPlus的条件构造器查询
        // 注意：JSON_CONTAINS函数用于检查JSON数组中是否包含特定值
        Page<CommentsNotification> notificationPage = lambdaQuery()
            .apply("JSON_CONTAINS(users_id, {0})", userId.toString())
            .eq(category != null && !category.isEmpty(), CommentsNotification::getCategory, category)
            .orderByDesc(CommentsNotification::getCreateTime)
            .page(page);
        log.debug("notificationPage:{}",notificationPage);
        // 如果需要返回Comments对象，需要进一步处理
        // 这里假设需要根据通知中的commentsId查询对应的评论
        Page<Comments> commentsPage = new Page<>(pageNum, pageSize);
        commentsPage.setTotal(notificationPage.getTotal());
        
        // 获取所有通知中的评论ID
        List<Long> replyCommentsIds = notificationPage.getRecords().stream()
            .map(CommentsNotification::getReplyCommentsId)
            .toList();
        
        // 创建通知ID到通知对象的映射，方便后续查找
        var notificationMap = notificationPage.getRecords().stream()
            .collect(java.util.stream.Collectors.toMap(
                CommentsNotification::getReplyCommentsId, 
                notification -> notification,
                (existing, replacement) -> existing
            ));
        
        // 这里需要注入CommentsService来查询评论
        if (!replyCommentsIds.isEmpty()) {
            // 查询这些ID对应的评论
            List<Comments> commentsList = commentsService.lambdaQuery()
                .in(Comments::getCommentsId, replyCommentsIds)
                .list();
            
            // 为每个评论设置已读状态
            commentsList.forEach(comment -> {
                CommentsNotification notification = notificationMap.get(comment.getCommentsId());
                if (notification != null) {
                    // 使用transient字段或其他方式将已读状态传递给评论对象
                    comment.setIsRead(notification.getIsRead());
                    // 同时设置通知ID，方便前端标记已读
                    comment.setNotificationId( notification.getId());
                }
            });
                
            // 将评论设置到Page对象中
            commentsPage.setRecords(commentsList);
        }
        
        return commentsPage;
    }
    @Override
    public Boolean markAsRead(Long notificationId) {
        // 标记通知为已读
        CommentsNotification notification = getById(notificationId);
        if (notification != null) {
            notification.setIsRead(true);
            return updateById(notification);
        } else {
            return false;
           }
    }
}
