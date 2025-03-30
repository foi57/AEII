package org.demo.artExaminationInformationInquiry.api.service;

import org.demo.artExaminationInformationInquiry.api.entity.CommentsUsers;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-03-30
 */
public interface ICommentsUsersService extends IService<CommentsUsers> {
    Boolean insertCommentUsers(CommentsUsers commentsUsers);
    Boolean deleteCommentUsersById(Long commentId);
}
