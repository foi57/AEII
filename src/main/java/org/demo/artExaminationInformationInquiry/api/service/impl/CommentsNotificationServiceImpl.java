package org.demo.artExaminationInformationInquiry.api.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.demo.artExaminationInformationInquiry.api.entity.Comments;
import org.demo.artExaminationInformationInquiry.api.entity.CommentsNotification;
import org.demo.artExaminationInformationInquiry.api.entity.CommentsNotificationRead;
import org.demo.artExaminationInformationInquiry.api.entity.Users;
import org.demo.artExaminationInformationInquiry.api.mapper.CommentsNotificationMapper;
import org.demo.artExaminationInformationInquiry.api.service.ICommentsNotificationReadService;
import org.demo.artExaminationInformationInquiry.api.service.ICommentsNotificationService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.demo.artExaminationInformationInquiry.api.service.ICommentsService;
import org.demo.artExaminationInformationInquiry.api.service.IUsersService;
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
    private final ICommentsNotificationReadService commentsNotificationReadService;
    private final IUsersService usersService;

    public CommentsNotificationServiceImpl(
            @Lazy ICommentsService commentsService,
            ICommentsNotificationReadService commentsNotificationReadService,
            IUsersService usersService) {
        this.commentsService = commentsService;
        this.commentsNotificationReadService = commentsNotificationReadService;
        this.usersService = usersService;
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
        
        // 获取用户的已读状态记录
        // 修改：直接初始化最终的 Map，避免后续重新赋值
        final Map<Long, Boolean> readStatusMap = new HashMap<>();
        if (!notificationPage.getRecords().isEmpty()) {
            List<Long> notificationIds = notificationPage.getRecords().stream()
                .map(CommentsNotification::getId)
                .toList();
                
            // 查询用户对这些通知的已读状态
            List<CommentsNotificationRead> readRecords = commentsNotificationReadService.lambdaQuery()
                .in(CommentsNotificationRead::getCommentsNotificationId, notificationIds)
                .eq(CommentsNotificationRead::getUsersId, userId)
                .list();
                
            // 修改：直接向已有的 Map 中添加元素，而不是重新赋值
            readRecords.forEach(record -> 
                readStatusMap.put(
                    record.getCommentsNotificationId(),
                    record.getIsRead()
                )
            );
        }
        
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
                    // 从已读状态映射中获取状态，如果不存在则默认为未读(false)
                    Boolean isRead = readStatusMap.getOrDefault(notification.getId(), false);
                    comment.setIsRead(isRead);
                    // 同时设置通知ID，方便前端标记已读
                    comment.setNotificationId(notification.getId());
                }
            });
                
            // 将评论设置到Page对象中
            commentsPage.setRecords(commentsList);
        }
        
        return commentsPage;
    }
    
    @Override
    public Boolean markAsRead(Long notificationId) {
        // 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Users currentUser = usersService.selectUsersByName(username);
        Long userId = currentUser.getUsersId();
        
        // 查找是否已有记录
        CommentsNotificationRead readRecord = commentsNotificationReadService.lambdaQuery()
            .eq(CommentsNotificationRead::getCommentsNotificationId, notificationId)
            .eq(CommentsNotificationRead::getUsersId, userId)
            .one();
            
        if (readRecord != null) {
            // 更新已有记录
            readRecord.setIsRead(true);
            return commentsNotificationReadService.updateById(readRecord);
        } else {
            // 创建新记录
            CommentsNotificationRead newRecord = new CommentsNotificationRead();
            newRecord.setCommentsNotificationId(notificationId);
            newRecord.setUsersId(userId);
            newRecord.setIsRead(true);
            return commentsNotificationReadService.save(newRecord);
        }
    }
    
    @Override
    public Long countCommentsNotificationUnRead(Long user, String category) {
        // 先获取该用户该分类的所有通知
        List<CommentsNotification> notifications = lambdaQuery()
            .apply("JSON_CONTAINS(users_id, {0})", user.toString())
            .eq(CommentsNotification::getCategory, category)
            .list();
            
        if (notifications.isEmpty()) {
            return 0L;
        }
        
        // 获取通知ID列表
        List<Long> notificationIds = notifications.stream()
            .map(CommentsNotification::getId)
            .toList();
            
        // 查询已读记录
        List<CommentsNotificationRead> readRecords = commentsNotificationReadService.lambdaQuery()
            .in(CommentsNotificationRead::getCommentsNotificationId, notificationIds)
            .eq(CommentsNotificationRead::getUsersId, user)
            .eq(CommentsNotificationRead::getIsRead, true)
            .list();
            
        // 已读通知ID集合
        Set<Long> readNotificationIds = readRecords.stream()
            .map(CommentsNotificationRead::getCommentsNotificationId)
            .collect(java.util.stream.Collectors.toSet());
            
        // 计算未读数量 = 总数 - 已读数
        return (long) notifications.size() - readNotificationIds.size();
    }

    @Override
    @Transactional
    public Boolean markAllAsRead(Long userId, String category) {
        
            // 查询该用户该分类下所有未读通知
                List<CommentsNotification> notifications = lambdaQuery()
                .apply("JSON_CONTAINS(users_id, {0})", userId.toString())
                .eq(category != null && !category.isEmpty(), CommentsNotification::getCategory, category)
                .list();

            if (notifications.isEmpty()) {
                return true; // 没有未读通知，直接返回成功
            }
      
            // 获取所有通知ID
            List<Long> notificationIds = notifications.stream()
                .map(CommentsNotification::getId)
                .toList();
                
            // 批量创建或更新已读记录
            List<CommentsNotificationRead> readRecords = new ArrayList<>();
            for (Long notificationId : notificationIds) {
                // 查找是否已有记录
                CommentsNotificationRead existingRecord = commentsNotificationReadService.lambdaQuery()
                    .eq(CommentsNotificationRead::getCommentsNotificationId, notificationId)
                    .eq(CommentsNotificationRead::getUsersId, userId)
                    .one();
                    
                if (existingRecord != null) {
                    // 更新已有记录
                    existingRecord.setIsRead(true);
                    readRecords.add(existingRecord);
                } else {
                    // 创建新记录
                    CommentsNotificationRead newRecord = new CommentsNotificationRead();
                    newRecord.setCommentsNotificationId(notificationId);
                    newRecord.setUsersId(userId);
                    newRecord.setIsRead(true); // 设置为已读
                    readRecords.add(newRecord);
                }
            }
            
            // 批量保存或更新
            return commentsNotificationReadService.saveOrUpdateBatch(readRecords);
        
    }
}
