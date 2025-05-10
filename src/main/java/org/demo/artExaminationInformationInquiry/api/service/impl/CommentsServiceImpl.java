package org.demo.artExaminationInformationInquiry.api.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.demo.artExaminationInformationInquiry.Enum.CommentsNotificationEnum;
import org.demo.artExaminationInformationInquiry.api.entity.Comments;
import org.demo.artExaminationInformationInquiry.api.entity.CommentsNotificationRead;
import org.demo.artExaminationInformationInquiry.api.mapper.CommentsMapper;
import org.demo.artExaminationInformationInquiry.api.service.ICommentsNotificationReadService;
import org.demo.artExaminationInformationInquiry.api.service.ICommentsNotificationService;
import org.demo.artExaminationInformationInquiry.api.service.ICommentsService;
import org.demo.artExaminationInformationInquiry.api.service.ICommentsUsersService;
import org.demo.artExaminationInformationInquiry.api.service.IUsersService;
import org.demo.artExaminationInformationInquiry.api.entity.CommentsNotification;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.demo.artExaminationInformationInquiry.api.entity.CommentsUsers;
import org.demo.artExaminationInformationInquiry.api.entity.Users;

import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-03-27
 */
@Service
public class CommentsServiceImpl extends ServiceImpl<CommentsMapper, Comments> implements ICommentsService {
    @Override
    public Comments selectCommentById(Long commentId) {
        return lambdaQuery().eq(Comments::getCommentsId, commentId).one();
    }
    
    private final ICommentsNotificationService commentsNotificationService;
    private final ICommentsNotificationReadService commentsNotificationReadService;
    private final IUsersService usersService;
    private final ICommentsUsersService commentsUsersService;

    public CommentsServiceImpl(
            ICommentsUsersService commentsUsersService,
            ICommentsNotificationService commentsNotificationService,
            ICommentsNotificationReadService commentsNotificationReadService,
            IUsersService usersService) {
        this.commentsUsersService = commentsUsersService;
        this.commentsNotificationService = commentsNotificationService;
        this.commentsNotificationReadService = commentsNotificationReadService;
        this.usersService = usersService;
    }


    @Override
    public Page<Comments> selectCommentsByUsersId(Long usersId, Integer pageNum, Integer pageSize) {
        return null;
        
    }

    @Override
    @Transactional
    public Comments insertComment(Comments comments) {
        boolean saveResult = save(comments);
        
        // 获取当前登录用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // 获取用户名
        
        // 通过用户名查询用户信息
        Users currentUser = usersService.selectUsersByName(username);
        Long currentUserId = currentUser.getUsersId();
        
        if (comments.getReplyId() != null) {
            Comments replyComments = selectCommentById(comments.getReplyId());
            
            // 只有当回复的不是自己的评论时，才创建通知
            if (!replyComments.getUsersId().equals(currentUserId)) {
                // 创建评论通知
                CommentsNotification commentsNotification = new CommentsNotification();
                commentsNotification.setCommentsId(replyComments.getCommentsId());
                commentsNotification.setReplyCommentsId(comments.getCommentsId());
                commentsNotification.setUsersId(List.of(replyComments.getUsersId()));
                commentsNotification.setCategory(CommentsNotificationEnum.replyMe.name());
                commentsNotificationService.save(commentsNotification);
                
                // 为每个接收通知的用户创建通知读取记录
                for (Long userId : List.of(replyComments.getUsersId())) {
                    CommentsNotificationRead notificationRead = new CommentsNotificationRead();
                    notificationRead.setCommentsNotificationId(commentsNotification.getId());
                    notificationRead.setUsersId(userId);
                    notificationRead.setIsRead(false);
                    commentsNotificationReadService.save(notificationRead);
                }
            }
        }
        
        if (!comments.getToUsersId().isEmpty()) {
            // 过滤掉当前用户ID，避免给自己发通知
            List<Long> filteredUserIds = comments.getToUsersId().stream()
                .filter(id -> !id.equals(currentUserId))
                .toList();
                
            if (!filteredUserIds.isEmpty()) {
                // 创建评论通知
                CommentsNotification commentsNotification = new CommentsNotification();
                Comments replyComments = selectCommentById(comments.getReplyId());
                if (replyComments != null) {
                    commentsNotification.setCommentsId(replyComments.getCommentsId());
                }
                commentsNotification.setReplyCommentsId(comments.getCommentsId());
                commentsNotification.setUsersId(filteredUserIds);
                commentsNotification.setCategory(CommentsNotificationEnum.toMe.name());
                commentsNotificationService.save(commentsNotification);
                
                // 为每个接收通知的用户创建通知读取记录
                for (Long userId : filteredUserIds) {
                    CommentsNotificationRead notificationRead = new CommentsNotificationRead();
                    notificationRead.setCommentsNotificationId(commentsNotification.getId());
                    notificationRead.setUsersId(userId);
                    notificationRead.setIsRead(false);
                    commentsNotificationReadService.save(notificationRead);
                }
            }
        }
        
        if (saveResult) {
            return comments;
        }
        return null;
    }

    @Override
    @Transactional
    public Page<Comments> selectCommentListByArticleId(Long articleId, Integer pageNum, Integer pageSize, Long usersId) {
        Page<Comments> page = new Page<>(pageNum, pageSize);
        // 添加按创建时间降序排序
        Page<Comments> page1 = lambdaQuery()
                .eq(Comments::getArticleId, articleId)
                .isNull(Comments::getReplyId)
                .orderByDesc(Comments::getCreateTime)  // 按创建时间降序排序，最新的评论显示在前面
                .page(page);
        
        page1.getRecords().forEach(comments -> {
            CommentsUsers commentsUser = commentsUsersService.lambdaQuery()
                    .eq(CommentsUsers::getCommentsId, comments.getCommentsId())
                    .eq(CommentsUsers::getUsersId, usersId)
                    .one();
            comments.setIsThumbsUp(commentsUser != null);
            
            // 子评论也按时间排序
            List<Comments> commentsList = lambdaQuery()
                    .eq(Comments::getReplyId, comments.getCommentsId())
                    .orderByDesc(Comments::getCreateTime)  // 子评论也按创建时间降序排序
                    .list();
                    
            commentsList.forEach(comment -> {
                CommentsUsers subCommentsUser = commentsUsersService.lambdaQuery()
                        .eq(CommentsUsers::getCommentsId, comment.getCommentsId())
                        .eq(CommentsUsers::getUsersId, usersId)
                        .one();
                comment.setIsThumbsUp(subCommentsUser != null);
            });
            comments.setSubComments(commentsList);
        });
        return page1;
    }

    @Override
    public Boolean deleteCommentById(Long commentId) {
        return removeById(commentId);
    }

    @Override
    @Transactional
    public Boolean thumbsUp(Long commentId, Long userId) {
        
        Comments comments = lambdaQuery().eq(Comments::getCommentsId, commentId).one();
        comments.setThumb(comments.getThumb() + 1);
        // 使用简化的导入方式，避免使用全限定名
        CommentsUsers commentsUsers = new CommentsUsers();
        commentsUsers.setCommentsId(commentId);
        commentsUsers.setUsersId(userId);
        commentsUsersService.save(commentsUsers);
        return lambdaUpdate().eq(Comments::getCommentsId, commentId).update(comments);
    }

    @Override
    @Transactional
    public Boolean cancelThumbsUp(Long commentId, Long userId) {
        Comments comments = lambdaQuery().eq(Comments::getCommentsId, commentId).one();
        comments.setThumb(comments.getThumb() - 1);
        // 修改这里，根据评论ID和用户ID一起删除点赞记录
        // 避免错误地删除其他用户的点赞记录
        commentsUsersService.lambdaQuery()
                .eq(CommentsUsers::getCommentsId, commentId)
                .eq(CommentsUsers::getUsersId, userId)
                .oneOpt()
                .ifPresent(commentsUsers -> commentsUsersService.lambdaUpdate().eq(CommentsUsers::getUsersId, commentsUsers.getUsersId()).eq(CommentsUsers::getUsersId, userId).remove());
        return lambdaUpdate().eq(Comments::getCommentsId, commentId).update(comments);
    }

}
