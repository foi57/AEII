package org.demo.artExaminationInformationInquiry.api.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.demo.artExaminationInformationInquiry.api.entity.Comments;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-03-27
 */
public interface ICommentsService extends IService<Comments> {
    // 将返回类型从Boolean改为Comments
    Comments insertComment(Comments comments);
    Page<Comments> selectCommentListByArticleId(Long articleId, Integer pageNum, Integer pageSize,Long usersId);
    Boolean deleteCommentById(Long commentId);
    Boolean thumbsUp(Long commentId,Long usersId);
    Boolean cancelThumbsUp(Long commentId,Long usersId);
    Page<Comments> selectCommentsByUsersId(Long usersId,Integer pageNum,Integer pageSize);
    Comments selectCommentById(Long commentId);
}
