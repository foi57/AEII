package org.demo.artExaminationInformationInquiry.api.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.demo.artExaminationInformationInquiry.api.entity.Comments;
import org.demo.artExaminationInformationInquiry.api.entity.Notification;
import org.demo.artExaminationInformationInquiry.api.mapper.CommentsMapper;
import org.demo.artExaminationInformationInquiry.api.service.ICommentsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
