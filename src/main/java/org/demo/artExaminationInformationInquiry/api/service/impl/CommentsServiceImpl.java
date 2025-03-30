package org.demo.artExaminationInformationInquiry.api.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.demo.artExaminationInformationInquiry.api.entity.Comments;
import org.demo.artExaminationInformationInquiry.api.mapper.CommentsMapper;
import org.demo.artExaminationInformationInquiry.api.service.ICommentsService;
import org.demo.artExaminationInformationInquiry.api.service.ICommentsUsersService;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
import java.util.List;

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

    private final ICommentsUsersService commentsUsersService;

    public CommentsServiceImpl(ICommentsUsersService commentsUsersService) {
        this.commentsUsersService = commentsUsersService;
    }

    @Override
    public Boolean insertComment(Comments comments) {
        return save(comments);
    }

    @Override
    public Page<Comments> selectCommentListByArticleId(Long articleId, Integer pageNum, Integer pageSize) {
        Page<Comments> page = new Page<>(pageNum, pageSize);
        Page<Comments> page1 = lambdaQuery().eq(Comments::getArticleId, articleId).isNull(Comments::getReplyId).page(page);
        page1.getRecords().forEach(comments -> {
            List<Comments> commentsList = lambdaQuery().eq(Comments::getReplyId, comments.getCommentsId()).list();
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
    public Boolean thumbsUp(Long commentId,Long userId) {
        Comments comments = getById(commentId);
        comments.setThumb(comments.getThumb() + 1);
org.demo.artExaminationInformationInquiry.api.entity.CommentsUsers commentsUsers = new org.demo.artExaminationInformationInquiry.api.entity.CommentsUsers();
        commentsUsers.setCommentsId(commentId);
        commentsUsers.setUsersId(userId);
        commentsUsersService.save(commentsUsers);
        return updateById(comments);
    }

    @Override
    @Transactional
    public Boolean cancelThumbsUp(Long commentId,Long userId) {
        Comments comments = getById(commentId);
        comments.setThumb(comments.getThumb() - 1);
        commentsUsersService.deleteCommentUsersById(commentId);
        return updateById(comments);
    }
}
